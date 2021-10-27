package com.github.dc.print.helper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * springboot applicationContext 操作帮助类
 * @author wpyuan
 */
@Component("dcPrintApplicationContextHelper")
public class ApplicationContextHelper implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) throws BeansException {
        try {
            return applicationContext.getBean(name);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    public static <T> T getBean(Class<T> c){
        try {
            return applicationContext.getBean(c);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }
}
