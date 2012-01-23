package de.dst.mybatis.preload;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import de.dst.mybatis.domain.IdentifiedObject;

/**
 * Injected into services in order to preload dependent entities according to
 * given preload rules.
 */
@Component("preloadHelper")
public class PreloadHelper {

	@Autowired
	ServiceProvider serviceProvider;

	/**
	 * Preload proxied dependent entities.
	 * 
	 * @param entity
	 *            top level entity
	 * @param preloads
	 *            list of preload rules
	 * @param deepPreloading
	 *            recurse down object tree while preloading?
	 */
	public void preload(final Object entity, final Preload[] preloads,
			final boolean deepPreloading) {
		preload(entity, createPreloadObjects(preloads), deepPreloading);
	}

	/**
	 * Prelaod proxied dependent entities. Recurse down object tree.
	 * 
	 * @param entity
	 *            top level entity
	 * @param preloads
	 *            list of preload rules
	 */
	public void preload(final Object entity, final Preload[] preloads) {
		this.preload(entity, preloads, true);
	}

	/**
	 * Preload proxied dependent entities.
	 * 
	 * @param entity
	 *            top level entity
	 * @param preloads
	 *            list of resolved preload rules
	 * @param deepPreloading
	 *            recurse down object tree while preloading?
	 */
	protected void preload(Object entity, final PreloadAccessor[] preloads,
			final boolean deepPreloading) {
		// quit if there aren't any rules
		if (preloads == null) {
			return;
		}
		HashSet<Object> visited = new HashSet<Object>();
		Map<Object, Object> alreadyLoadedPreloadedEntities = new HashMap<Object, Object>();

		Map<PreloadAccessor, Collection<Object>> bulkPreloadMap;
		do {
			bulkPreloadMap = new HashMap<PreloadAccessor, Collection<Object>>();
			collectPreloadEntities(bulkPreloadMap, visited, entity, preloads,
					alreadyLoadedPreloadedEntities);
			entity = invokeBulkPreloads(bulkPreloadMap, preloads,
					alreadyLoadedPreloadedEntities);
		} while (deepPreloading && bulkPreloadMap.size() > 0);

	}

	/**
	 * Build up a map so that every preload rule relates to the entities
	 * activating it
	 * 
	 * @param bulkPreloadMap
	 *            preload -> entities activating that preload
	 * @param visited
	 *            set of visited entities
	 * @param entity
	 *            top level entity (or a collection of entities)
	 * @param preloads
	 *            list of resolved preload rules
	 * @param alreadyLoadedEntities
	 *            Map: ID -> Entity
	 */
	@SuppressWarnings("unchecked")
	private void collectPreloadEntities(
			final Map<PreloadAccessor, Collection<Object>> bulkPreloadMap,
			final Set<Object> visited, final Object entity,
			final PreloadAccessor[] preloads,
			final Map<Object, Object> alreadyLoadedEntities) {
		if (entity == null || preloads == null) {
			return;
		}

		if (visited.contains(entity)) {
			return;
		}
		visited.add(entity);

		if (entity instanceof Collection) {

			for (Object resultEntity : (Collection) entity) {
				collectPreloadEntities(bulkPreloadMap, visited, resultEntity,
						preloads, alreadyLoadedEntities);
			}

		} else {

			if (entity instanceof IdentifiedObject) {
				// todo Jetzt werden die Entities auch korrekt beim Preloading
				// gesetzt
				// falls irgend ein anderes (prezuloadendes) Proxy-Entity darauf
				// "stößt"... Funktioniert leider nur für RPlanObject-Entities -
				// andere Möglichkeit wäre (analog) zu den Proxy-Obj.
				// "id-getter-Funktionsname" zu übergeben :-(
				IdentifiedObject rpo = (IdentifiedObject) entity;
				alreadyLoadedEntities.put(rpo.getId(), rpo);
			}

			// crucial part: Search for preloads matching entity's class and
			// build up a map
			// preload -> matching entities in order to do a bulk preload later
			// on
			for (PreloadAccessor preload : preloads) {
				if (preload.getEntityClass().isInstance(entity)) {
					add2BulkPreloadEntityMap(bulkPreloadMap, preload, entity);
				}
			}

		}
	}

	/**
	 * Add entity to preload map.
	 * 
	 * @param bulkPreloadEntityMap
	 *            preload rule -> collection of entities
	 * @param preload
	 *            resolved preload rule
	 * @param entity
	 *            top level entity
	 */
	private void add2BulkPreloadEntityMap(
			final Map<PreloadAccessor, Collection<Object>> bulkPreloadEntityMap,
			final PreloadAccessor preload, final Object entity) {
		if (bulkPreloadEntityMap == null || entity == null) {
			return;
		}
		Collection<Object> entityList = bulkPreloadEntityMap.get(preload);
		if (entityList == null) {
			entityList = new HashSet<Object>(1);
			bulkPreloadEntityMap.put(preload, entityList);
		}
		entityList.add(entity);
	}

	/**
	 * Iterate preloads and replace proxies for corresponding entities using
	 * bulk operations
	 * 
	 * @param bulkPreloadMap
	 *            preload -> entities activating preload
	 * @param preloads
	 *            preload rules
	 * @param alreadyLoadedEntities
	 *            ID -> entity
	 * @return collection of preloaded entities
	 */
	private Collection<Object> invokeBulkPreloads(
			final Map<PreloadAccessor, Collection<Object>> bulkPreloadMap,
			final PreloadAccessor[] preloads,
			final Map<Object, Object> alreadyLoadedEntities) {
		Set<Object> retCollection = new HashSet<Object>();
		for (Map.Entry<PreloadAccessor, Collection<Object>> mapEntry : bulkPreloadMap
				.entrySet()) {
			PreloadAccessor preload = mapEntry.getKey();
			Collection<Object> entityList = mapEntry.getValue();

			if (entityList.size() > 0) {
				retCollection.addAll(preload.replaceProxies(entityList,
						alreadyLoadedEntities));
			}
		}
		return retCollection;
	}

	/**
	 * Resolve services and finders of given preload rules.
	 * 
	 * @param preloads
	 *            preload rules
	 * @return resolved preload rules
	 */
	private PreloadAccessor[] createPreloadObjects(final Preload[] preloads) {
		if (preloads == null) {
			return null;
		}
		PreloadAccessor[] ret = new PreloadAccessor[preloads.length];
		for (int i = 0; i < preloads.length; i++) {
			Preload p = preloads[i];
			ret[i] = new PreloadAccessor(p,
					serviceProvider.getServiceInstance(p.getPreloadFindMethod()
							.getFinderServiceName()));
		}
		return ret;
	}
}
