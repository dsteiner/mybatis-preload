package de.dst.mybatis.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

import de.dst.mybatis.domain.Ghost;
import de.dst.mybatis.preload.PreloadService;

/**
 * Convenience class for retrieving Spring beans by name.
 */
@Component("preloadService")
public class PreloadServiceImpl implements PreloadService, BeanFactoryAware {

	private BeanFactory beanFactory;
	private final Map<String, Object> services = new HashMap<String, Object>();

	/**
	 * 
	 * @param serviceName
	 *            Spring bean name
	 * @return service singleton
	 */
	public Object getServiceInstance(String serviceName) {
		Object srv = services.get(serviceName);
		if (srv == null) {
			srv = beanFactory.getBean(serviceName);
			services.put(serviceName, srv);
		}
		return srv;
	}

	public void setBeanFactory(BeanFactory bf) throws BeansException {
		beanFactory = bf;
	}

	public String getIdPropertyForEntityClass(Class forEntityClass) {
		return "id";
	}

	public boolean isGhost(Object ghostOrNot) {
		return ghostOrNot instanceof Ghost;
	}
}
