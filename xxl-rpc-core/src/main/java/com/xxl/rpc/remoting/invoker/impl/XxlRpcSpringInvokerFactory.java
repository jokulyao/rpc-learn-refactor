package com.xxl.rpc.remoting.invoker.impl;

import com.xxl.rpc.registry.ServiceRegistry;
import com.xxl.rpc.remoting.invoker.XxlRpcInvokerFactory;
import com.xxl.rpc.remoting.invoker.annotation.XxlRpcReference;
import com.xxl.rpc.remoting.invoker.reference.XxlRpcReferenceBean;
import com.xxl.rpc.remoting.provider.XxlRpcProviderFactory;
import com.xxl.rpc.util.XxlRpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * xxl-rpc invoker factory, init service-registry and spring-bean by annotation (for spring)
 *
 * @author xuxueli 2018-10-19
 */
public class XxlRpcSpringInvokerFactory extends InstantiationAwareBeanPostProcessorAdapter implements InitializingBean,DisposableBean, BeanFactoryAware {
    private Logger logger = LoggerFactory.getLogger(XxlRpcSpringInvokerFactory.class);

    // ---------------------- config ----------------------

    private ServiceRegistry serviceRegistry;          // class.forname


    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }


    // ---------------------- util ----------------------

    private XxlRpcInvokerFactory xxlRpcInvokerFactory;

    @Override
    public void afterPropertiesSet() throws Exception {
        xxlRpcInvokerFactory = new XxlRpcInvokerFactory();
        xxlRpcInvokerFactory.setServiceRegistry(serviceRegistry);
        xxlRpcInvokerFactory.start();
    }

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {

        ReflectionUtils.doWithFields(bean.getClass(), new ReflectionUtils.FieldCallback() {
            @Override
            public void doWith(Field field) throws IllegalArgumentException, IllegalAccessException {
                if (field.isAnnotationPresent(XxlRpcReference.class)) {
                    // valid
                    Class iface = field.getType();
                    if (!iface.isInterface()) {
                        throw new XxlRpcException("xxl-rpc, reference(XxlRpcReference) must be interface.");
                    }

                    XxlRpcReference rpcReference = field.getAnnotation(XxlRpcReference.class);

                    // init reference bean
                    XxlRpcReferenceBean referenceBean = new XxlRpcReferenceBean(
                            rpcReference.netType(),
                            rpcReference.serializer().getSerializer(),
                            rpcReference.callType(),
                            iface,
                            rpcReference.version(),
                            rpcReference.timeout(),
                            rpcReference.address(),
                            rpcReference.accessToken(),
                            null
                    );

                    Object serviceProxy = referenceBean.getObject();

                    // set bean
                    field.setAccessible(true);
                    field.set(bean, serviceProxy);

                    logger.info(">>>>>>>>>>> xxl-rpc, invoker factory init reference bean success. serviceKey = {}, bean.field = {}.{}",
                            XxlRpcProviderFactory.makeServiceKey(iface.getName(), rpcReference.version()), beanName, field.getName());
                }
            }
        });

        return super.postProcessAfterInstantiation(bean, beanName);
    }


    @Override
    public void destroy() throws Exception {

        // stop invoker factory
        xxlRpcInvokerFactory.stop();
    }

    private BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
}
