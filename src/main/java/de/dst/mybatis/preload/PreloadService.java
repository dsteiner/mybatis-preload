package de.dst.mybatis.preload;

public interface PreloadService {

	Object getServiceInstance(String serviceName);

	String getIdPropertyForEntityClass(Class entityClass);

}
