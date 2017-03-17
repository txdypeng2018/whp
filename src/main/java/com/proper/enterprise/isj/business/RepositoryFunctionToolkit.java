package com.proper.enterprise.isj.business;

public interface RepositoryFunctionToolkit extends FunctionToolkit {

    @SuppressWarnings("rawtypes")
    <T> T executeRepositoryFunction(Class repositoryType, String methodName, Object ... params);
    
    <T> T executeRepositoryFunction(Object repository, String methodName, Object ... params);
}
