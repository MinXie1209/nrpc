package org.example.nrpc.server.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务管理
 *
 * @author 江南小俊
 * @since 2021/7/7
 **/
@Slf4j
public class ServiceManager {
    /**
     * key 接口类全限定名
     * value 实现类全限定名
     **/

    private static ConcurrentHashMap<String, String> classMap = new ConcurrentHashMap<>();

    public static String getClassImpl(String className) throws ClassNotFoundException {
        if (!classMap.containsKey(className)) {
            throw new ClassNotFoundException(className + "实现类");
        }
        return classMap.get(className);
    }

    /**
     * 添加服务
     *
     * @param classApi
     * @param classImpl
     * @return void
     * @author Jim
     * @since 2021/7/7 下午1:35
     **/

    public static void register(Class<?> classApi, Class<?> classImpl) {
        log.debug("注册服务-api-{},-impl-{}", classApi.getName(), classImpl.getName());
        classMap.put(classApi.getName(), classImpl.getName());
    }
}
