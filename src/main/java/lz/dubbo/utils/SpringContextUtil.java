package lz.dubbo.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@Service
public class SpringContextUtil implements ApplicationContextAware {  

	private static ApplicationContext applicationContext;  

	public void setApplicationContext(ApplicationContext ctx) {  
		applicationContext = ctx;  
	}  

	public static ApplicationContext getApplicationContext() {  
		return applicationContext;  
	}  

	public static Object getBean(String name) {  
		try{
			return applicationContext.getBean(name);  
		}catch(Exception e){
			return null;
		}
	}  

	public static void registerBean(String name,BeanDefinition beanDefinition) throws BeansException {  
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) getApplicationContext();
		DefaultListableBeanFactory beanFactory=  (DefaultListableBeanFactory) context.getBeanFactory();
		beanFactory.registerBeanDefinition(name, beanDefinition);
	}  

	public static void registerBean(String name,Class clazz) throws BeansException {  
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) getApplicationContext();
		DefaultListableBeanFactory beanFactory=  (DefaultListableBeanFactory) context.getBeanFactory();

		BeanDefinitionBuilder beanDefBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
 
		beanFactory.registerBeanDefinition(name, beanDefBuilder.getBeanDefinition());
	} 

	public static void removeBean(String name){  
		try{
		ConfigurableApplicationContext context = (ConfigurableApplicationContext) getApplicationContext();
		DefaultListableBeanFactory beanFactory=  (DefaultListableBeanFactory) context.getBeanFactory();
		beanFactory.removeBeanDefinition(name);
		}catch (Exception e){
			
		}
	} 
}