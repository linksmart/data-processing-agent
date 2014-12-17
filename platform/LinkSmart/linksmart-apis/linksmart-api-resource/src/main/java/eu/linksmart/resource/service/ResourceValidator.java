package eu.linksmart.resource.service;

public interface ResourceValidator<T> {

	boolean isValid(T resource);

}
