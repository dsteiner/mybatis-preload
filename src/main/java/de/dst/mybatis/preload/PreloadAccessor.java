package de.dst.mybatis.preload;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents resolved preload rule and holds references to preload rule,
 * service instance, finder method, finder parameter class and a flag which
 * indicates if the finder is capable of doing bulk loading.
 */
class PreloadAccessor {

	final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * Preload rule
	 */
	private final Preload preloadDefinition;

	/**
	 * Service singleton containing finder.
	 */
	private final Object serviceInstance;

	/**
	 * Reflective reference to finder method which must be called
	 */
	private final Method finderMethod;

	/**
	 * Class of only finder parameter. Usually the id-type of the proxied
	 * entity. todo ?? Also class of entity or proxy is supported. Query by
	 * example ?
	 */
	private final Class<?> finderMethodArgClass;

	/**
	 * Indicates if the finder is capable of bulk loading, i.e. it takes a
	 * collection of IDs and it returns a collection of entities.
	 */
	private final boolean finderMethodIsBulkLoading;

	private final PreloadService preloadService;

	public PreloadAccessor(final Preload preload, PreloadService serviceProvider) {
		this.preloadService = serviceProvider;

		PreloadFindMethod preloadFindMethod = preload.getPreloadFindMethod();
		Object serviceInstance = serviceProvider
				.getServiceInstance(preloadFindMethod.getFinderServiceName());

		this.preloadDefinition = preload;
		this.serviceInstance = serviceInstance;
		this.finderMethod = searchFinderMethod(
				preloadFindMethod.getFinderMethodName(),
				preloadFindMethod.getFinderMethodArgClass());
		this.finderMethodIsBulkLoading = initFinderMethodIsBulkLoading(finderMethod);
		this.finderMethodArgClass = finderMethod.getParameterTypes()[0];
	}

	/**
	 * Check if finder has exactly one parameter and if it returns a collection
	 * in case of bulk loading.
	 * 
	 * @param m
	 *            finder method
	 * @return supports bulk loading
	 */
	private boolean initFinderMethodIsBulkLoading(final Method m) {
		Class<?>[] paramTypes = m.getParameterTypes();
		if (paramTypes.length > 2) {
			throw new RuntimeException(
					"Finder method must have one parameter plus optional array of Preload classes");
		}

		boolean b = Collection.class.isAssignableFrom(paramTypes[0]);
		if (b && !Collection.class.isAssignableFrom(m.getReturnType())) {
			throw new RuntimeException(
					"Bulk loading finder method must return Collection");
		}
		return b;
	}

	@SuppressWarnings("unchecked")
	private Class getFinderMethodArgClass() {
		return finderMethodArgClass;
	}

	private Object getServiceInstance() {
		return serviceInstance;
	}

	@SuppressWarnings("unchecked")
	public Class getEntityClass() {
		return preloadDefinition.getEntityClass();
	}

	private String getEntityProperty() {
		return preloadDefinition.getEntityProperty();
	}

	private Method getFinderMethod() {
		return finderMethod;
	}

	/**
	 * Try to locate finder.
	 * 
	 * @param finderMethodName
	 *            name of finder method
	 * @param finderMethodArgs
	 *            Class of only parameter of finder; may be null
	 * @return reflective reference to finder method
	 */
	@SuppressWarnings("unchecked")
	private Method searchFinderMethod(final String finderMethodName,
			final Class finderMethodArgs) {
		if (finderMethodArgs != null) {
			try {
				return getServiceInstance().getClass().getMethod(
						finderMethodName, finderMethodArgs, Preload[].class);
			} catch (NoSuchMethodException e) {
				//
			}
			try {
				return getServiceInstance().getClass().getMethod(
						finderMethodName, finderMethodArgs);
			} catch (NoSuchMethodException e) {
				//
			}
		} else {
			Method[] methods = getServiceInstance().getClass().getMethods();
			logger.debug("searching method " + finderMethodName);
			for (Method m : methods) {
				logger.debug("method=" + m.getName());
				if (m.getName().equals(finderMethodName)) {
					return m;
				}
			}
		}

		throw new RuntimeException("No method found for "
				+ getServiceInstance().getClass().getCanonicalName() + "."
				+ finderMethodName + "(" + finderMethodArgs + ")");
	}

	/**
	 * Set post-loaded property on entity.
	 * 
	 * @param resultProperty
	 *            post-loaded property
	 * @param entity
	 *            Entity for which to set property
	 */
	public void invokeSetter(final Object resultProperty, final Object entity) {
		try {
			PropertyUtils.setProperty(entity, getEntityProperty(),
					resultProperty);
		} catch (Exception ex) {
			throw new RuntimeException(this.toString(), ex);
		}
	}

	/**
	 * Invoke finder method on service using appropriate argument.
	 * 
	 * @param proxyEntity
	 * @param entity
	 * @return finder result; single entity or collection of entities
	 */
	private Object invokeFinder(final Object proxyEntity, final Object entity) {
		try {
			Method finderMethod = getFinderMethod();
			Object finderArg = getFinderArg(entity, proxyEntity);
			if (finderMethod.getParameterTypes().length == 1) {
				return finderMethod.invoke(getServiceInstance(), finderArg);
			} else {
				return finderMethod.invoke(getServiceInstance(), finderArg,
						null);
			}
		} catch (Exception ex) {
			throw new RuntimeException(this.toString(), ex);
		}
	}

	/**
	 * todo ??? Return argument for finder
	 * 
	 * @param entity
	 * @param proxyEntity
	 * @return argument of finder
	 */
	protected Object getFinderArg(final Object entity, final Object proxyEntity) {

		// CAN be either a 1:n relation or Id of the
		// proxy was not set
		if (proxyEntity == null) {
			// this MUST be an error!!!
			if (entity == null) {
				throw new RuntimeException(
						"internal error: proxyEntity is null AND entity is null");
			}

			if (entity.getClass().equals(getFinderMethodArgClass())) {
				return entity;
			}
			Object entityId = getEntityId(entity);
			if (entityId != null) {
				return entityId;
			}

		} else if (proxyEntity.getClass().equals(getFinderMethodArgClass())) {
			return proxyEntity;
		} else {
			Object proxyEntityId = getEntityId(proxyEntity);
			if (proxyEntityId != null) {
				return proxyEntityId;
			}
		}
		throw new RuntimeException("Couldn't resolve argument for finder");
	}

	/**
	 * Return ID value of proxied entity.
	 * 
	 * @param anyEntity
	 *            proxied entity, i.e. only populated with ID.
	 * @return ID value of proxied entity
	 */
	protected Object getEntityId(final Object anyEntity) {
		if (anyEntity == null)
			return null;
		String anyEntityIdProperty = preloadService
				.getIdPropertyForEntityClass(anyEntity.getClass());
		if (anyEntityIdProperty == null) {
			return null;
		}
		return getPropertyFromEntity(anyEntity, anyEntityIdProperty);
	}

	public static Object getPropertyFromEntity(final Object proxiedEntity,
			String propertiyName) {
		try {
			return PropertyUtils.getProperty(proxiedEntity, propertiyName);
		} catch (Exception e) {
			throw new RuntimeException("Property '" + propertiyName
					+ "' not found in entity: " + proxiedEntity, e);
		}
	}

	/**
	 * Invoke property getter on entity returning proxied entity
	 * 
	 * @param entity
	 *            enclosing entity
	 * @return property of entity, i.e. the proxied dependent entity
	 */
	protected Object invokeGetter(final Object entity) {

		try {
			return PropertyUtils.getProperty(entity, getEntityProperty());
		} catch (Exception ex) {
			throw new RuntimeException("Can't invoke getter for property: "
					+ getEntityProperty(), ex);
		}
	}

	/**
	 * Replace proxied entities with fully loaded entities for given list of top
	 * level entities.
	 * 
	 * @param entities
	 *            top level entities
	 * @param alreadyLoadedEntities
	 *            collection of already loaded entities (cache)
	 * @return collection of preloaded entities
	 */
	public Collection<Object> replaceProxies(final Collection<Object> entities,
			final Map<Object, Object> alreadyLoadedEntities) {
		if (isBulkLoadingFinderMethod()) {
			Collection<Object> idListForFinder = makeIdsForFinder(entities,
					alreadyLoadedEntities);
			Collection<Object> preloadEntities = invokeBulkFinder(idListForFinder);
			return invokeBulkSetter(preloadEntities, entities,
					alreadyLoadedEntities);
		} else {
			Collection<Object> c = new HashSet<Object>();
			for (Object entity : entities) {
				Object proxyEntity = invokeGetter(entity);
				Object proxyId = proxyEntity == null ? null
						: getEntityId(proxyEntity);
				Object preloadedEntity;
				if (alreadyLoadedEntities.containsKey(proxyId)) {
					preloadedEntity = alreadyLoadedEntities.get(proxyId);
				} else {
					preloadedEntity = invokeFinder(proxyEntity, entity);
					if (proxyId != null) {
						alreadyLoadedEntities.put(proxyId, preloadedEntity);
					}
					c.add(preloadedEntity);
				}
				invokeSetter(preloadedEntity, entity);
			}
			return c;
		}
	}

	/**
	 * Iterate top level entities and set dependent preloaded entities.
	 * 
	 * @param preloadEntities
	 *            new preloaded entities
	 * @param entities
	 *            top level entities
	 * @param alreadyLoadedEntities
	 *            dependent entities preloaded at an earlier stage (cache)
	 * @return preloaded entities
	 */
	private Collection<Object> invokeBulkSetter(
			final Collection<Object> preloadEntities,
			final Collection<Object> entities,
			final Map<Object, Object> alreadyLoadedEntities) {
		Map<Object, Object> entityRepository = makeEntityRepository(preloadEntities);
		for (Object entity : entities) {
			Object proxyEntity = invokeGetter(entity);
			Object proxyId = getEntityId(proxyEntity);
			Object loadedEntity;
			if (alreadyLoadedEntities.containsKey(proxyId)) {
				loadedEntity = alreadyLoadedEntities.get(proxyId);
			} else {
				loadedEntity = entityRepository.get(proxyId);
				alreadyLoadedEntities.put(proxyId, loadedEntity);
			}
			invokeSetter(loadedEntity, entity);
		}
		return entityRepository.values();
	}

	/**
	 * Return mapping from ID to entity.
	 * 
	 * @param preloadEntities
	 *            collection of entities
	 * @return map: ID -> Entity
	 */
	private Map<Object, Object> makeEntityRepository(
			final Collection<Object> preloadEntities) {
		Map<Object, Object> retMap = new HashMap<Object, Object>(
				preloadEntities.size());
		for (Object preloadedEntity : preloadEntities) {
			Object id = getEntityId(preloadedEntity);
			retMap.put(id, preloadedEntity);
		}
		return retMap;
	}

	private boolean isBulkLoadingFinderMethod() {
		return finderMethodIsBulkLoading;
	}

	/**
	 * Invoke bulk finder using given IDs as argument and return resulting
	 * collection.
	 * 
	 * @param iDsForFinder
	 *            collection of IDs
	 * @return collection of entities
	 */
	@SuppressWarnings("unchecked")
	private Collection<Object> invokeBulkFinder(Collection<Object> iDsForFinder) {
		try {
			if (iDsForFinder.size() == 0) {
				return new ArrayList();
			}

			if (List.class.isAssignableFrom(getFinderMethodArgClass())
					&& !getFinderMethodArgClass().isInstance(iDsForFinder)) {
				iDsForFinder = new ArrayList(iDsForFinder);
			}
			Method finderMethod = getFinderMethod();
			if (finderMethod.getParameterTypes().length == 1) {
				return (Collection<Object>) finderMethod.invoke(
						getServiceInstance(), iDsForFinder);
			} else {
				return (Collection<Object>) finderMethod.invoke(
						getServiceInstance(), iDsForFinder, null);
			}
		} catch (Exception ex) {
			throw new RuntimeException(this.toString(), ex);
		}
	}

	/**
	 * Collect IDs of proxied entities for given collection of top level
	 * entities corresponding to this preload rule. Skip already loaded
	 * entities.
	 * 
	 * @param entities
	 *            collection of top level entities
	 * @param alreadyLoadedEntities
	 *            ID -> Entity
	 * @return IDs of to be loaded proxies
	 */
	private Collection<Object> makeIdsForFinder(
			final Collection<Object> entities,
			final Map<Object, Object> alreadyLoadedEntities) {
		Collection<Object> iDs = new HashSet<Object>(entities.size());

		for (Object entity : entities) {
			Object proxyEntity = invokeGetter(entity);
			if (preloadService.isGhost(proxyEntity)) {
				Object id = getEntityId(proxyEntity);
				if (id != null && !alreadyLoadedEntities.containsKey(id)) {
					iDs.add(id);
				}
			}
		}
		return iDs;
	}

	@Override
	public String toString() {
		return "PreloadAccessor: [" + preloadDefinition
				+ "], isFinderBulkLoading=" + finderMethodIsBulkLoading;
	}
}