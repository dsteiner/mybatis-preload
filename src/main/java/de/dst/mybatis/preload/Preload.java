package de.dst.mybatis.preload;

import java.io.Serializable;

/**
 * Makes up a preload rule: Every time you load an instance of "entityClass",
 * also fully load property "entityProperty".
 */
public class Preload implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Class of surrounding entity
	 */
	private final Class<?> entityClass;

	/**
	 * Name of property which should be preloaded
	 */
	private final String entityProperty;

	/**
	 * Meta-data of appropriate finder used to load "entityProperty"
	 */
	private final PreloadFindMethod preloadFindMethod;

	public Preload(final Class<?> entityClass, final String entityProperty,
			final PreloadFindMethod findMethod) {
		this.entityClass = entityClass;
		this.entityProperty = entityProperty;
		this.preloadFindMethod = findMethod;
	}

	public Class<?> getEntityClass() {
		return entityClass;
	}

	public String getEntityProperty() {
		return entityProperty;
	}

	public PreloadFindMethod getPreloadFindMethod() {
		return preloadFindMethod;
	}

	@Override
	public String toString() {
		return "Preload: entityClass=" + entityClass + " entityProperty="
				+ entityProperty + " preloadFindMethod="
				+ getPreloadFindMethod();
	}
}
