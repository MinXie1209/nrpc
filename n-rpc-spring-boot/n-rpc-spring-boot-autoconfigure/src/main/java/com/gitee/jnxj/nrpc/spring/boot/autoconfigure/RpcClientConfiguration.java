package com.gitee.jnxj.nrpc.spring.boot.autoconfigure;

import cn.hutool.core.util.ServiceLoaderUtil;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcService;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.annotation.NRpcServiceScan;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config.RpcClientConfig;
import com.gitee.jnxj.nrpc.spring.boot.autoconfigure.config.RpcRegisterConfig;
import org.example.nrpc.client.RpcClient;
import org.example.nrpc.client.proxy.BeanFactory;
import org.example.nrpc.register.api.RpcRegister;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author 江南小俊
 * @since 2021/7/9
 **/
@Configuration
@EnableConfigurationProperties({RpcRegisterConfig.class, RpcClientConfig.class})
@ConditionalOnExpression("'${nrpc.register.connectString:false}' ne 'false' && '${nrpc.client.enabled:false}' " +
                                 "ne 'false'")
public class RpcClientConfiguration implements ApplicationRunner, ResourceLoaderAware {
    private MetadataReaderFactory metadataReaderFactory;
    private ResourcePatternResolver resourcePatternResolver;
    private static final String DEFAULT_RESOURCE_PATTERN = "/**/*.class";
    @Resource
    private ApplicationContext context;
    @Resource
    private RpcRegisterConfig rpcRegisterConfig;

    @Bean(initMethod = "init", destroyMethod = "destroy")
    public RpcClient rpcClient() {
        return new RpcClient();
    }

    @Bean(destroyMethod = "destroy", name = "clientRegister")
    RpcRegister rpcRegister() {
        RpcRegister rpcRegister = ServiceLoaderUtil.loadFirstAvailable(RpcRegister.class);
        rpcRegister.init(rpcRegisterConfig.getConnectString());
        return rpcRegister;
    }

    @Override
    public void run(ApplicationArguments args) {
        BeanFactory.setRpcClient(rpcClient());
        BeanFactory.setRpcRegister(rpcRegister());
        Map<String, Object> beansWithAnnotation = context.getBeansWithAnnotation(NRpcServiceScan.class);
        List<Class<?>> classList = new LinkedList<>();
        for (Object item : beansWithAnnotation.values()) {
            NRpcServiceScan annotation = item.getClass().getAnnotation(NRpcServiceScan.class);
            if (annotation == null) {
            } else {
                String[] value = annotation.value();
                if (value != null && value.length > 0) {
                    classList.addAll(scanNRpcService(value));
                }
            }

        }
        //扫描NRpcService接口 并进行服务监听
        classList.forEach(item -> BeanFactory.addListener(item.getName()));
    }

    private List<Class<?>> scanNRpcService(String[] basePackages) {

        List<Class<?>> list = new LinkedList<>();
        for (String basePackage : basePackages) {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackage) + DEFAULT_RESOURCE_PATTERN;
            try {
                org.springframework.core.io.Resource[] resources =
                        this.resourcePatternResolver.getResources(packageSearchPath);
                for (org.springframework.core.io.Resource resource : resources) {
                    if (resource.isReadable()) {
                        MetadataReader metadataReader = this.metadataReaderFactory.getMetadataReader(resource);
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz;
                        try {
                            clazz = Class.forName(className);
                            NRpcService nRpcService = clazz.getAnnotation(NRpcService.class);
                            if (nRpcService != null) {
                                list.add(clazz);
                            }
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;

    }

    protected String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(context.getEnvironment().resolveRequiredPlaceholders(basePackage));
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        this.metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
    }
}
