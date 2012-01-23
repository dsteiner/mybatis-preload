package de.dst.mybatis.preload;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * Convenience class for retrieving Spring beans by name.
 */
@Component("serviceProvider")
public class ServiceProviderImpl implements ServiceProvider, BeanFactoryAware {

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
}
