package vip.efactory.embp.base.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Description: 手动控制时机,向Spring容器中注册Bean的工具
 *
 * @author dbdu
 * @date 19-2-21 下午2:07
 */
@Service
public class SpringObjectFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public void register(String beanName, Class<?> beanClass) {
        BeanDefinitionRegistry beanRegistry = (BeanDefinitionRegistry) applicationContext.getAutowireCapableBeanFactory();
        try {
            beanRegistry.getBeanDefinition(beanName);
            return;
        } catch (Exception e) {
            GenericBeanDefinition definition = new GenericBeanDefinition();
            definition.setBeanClass(beanClass);
            definition.setScope(BeanDefinition.SCOPE_SINGLETON);
            beanRegistry.registerBeanDefinition(beanName, definition);
        }
    }

    @Autowired
    public void setApplicationContext(ApplicationContext appCtx) {
        applicationContext = appCtx;
        Assert.isTrue(applicationContext.getAutowireCapableBeanFactory() instanceof BeanDefinitionRegistry, "autowireCapableBeanFactory should be BeanDefinitionRegistry");
    }

}
