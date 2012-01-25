package de.dst.mybatis.preload;

import java.io.Serializable;

/**
 * Encapsulates information about the "post-loading" method needed for a certain
 * preload definition.
 */
public class PreloadFindMethod implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Spring-Bean name of service on which to call finder
	 */
	private final String finderServiceName;

	/**
	 * Name of finder method
	 */
	private final String finderMethodName;

	/**
	 * Optional attribute specifying the parameters of the desired finder
	 * method. Only necessary if "finderMethodName" is overloaded.
	 */
	private final Class<?> finderMethodArgClass;

	public PreloadFindMethod(final String finderServiceName,
			final String finderMethodName, final Class<?> finderMethodArgClass) {
		this.finderServiceName = finderServiceName;
		this.finderMethodName = finderMethodName;
		this.finderMethodArgClass = finderMethodArgClass;
	}

	@Override
	public String toString() {
		return "PreloadFindMethod: finderServiceName=" + finderServiceName
				+ " finderMethodName=" + finderMethodName;
	}

	public String getFinderServiceName() {
		return finderServiceName;
	}

	@SuppressWarnings("unchecked")
	public Class getFinderMethodArgClass() {
		return finderMethodArgClass;
	}

	public String getFinderMethodName() {
		return finderMethodName;
	}
}