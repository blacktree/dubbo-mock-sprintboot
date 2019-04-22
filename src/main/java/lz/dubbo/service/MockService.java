package lz.dubbo.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;
import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.alibaba.dubbo.rpc.service.GenericService;

import groovy.lang.GroovyClassLoader;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.NotFoundException;
import lz.dubbo.classloader.MockClassLoader;
import lz.dubbo.constant.Const;
import lz.dubbo.controller.BaffleController;
import lz.dubbo.utils.SpringContextUtil;
import lz.dubbo.demo.Demo;

@Service
public class MockService {

	private static final Logger logger = LoggerFactory.getLogger(BaffleController.class);

	@Autowired
	private RegistryConfig dubboRegistry;
	
	private final static String GroovyDirectory="d:\\dubbo-mock-sprintboot\\groovyFiles\\";

	private final Map<String, ServiceConfig<GenericService>> skeletonMap = new ConcurrentHashMap<String, ServiceConfig<GenericService>>();

	public boolean exportService(String serviceName,String type) {

		try {
			GenericService genericObject=null;

			if("javasist".equals(type)) {
				genericObject=this.createDubboMockClassByJavasist(serviceName);
			}else if("groovy".equals(type)){
				genericObject=this.createDubboMockClassByGroovy(serviceName);

			}
			if(genericObject!=null) {
				exportService(serviceName,genericObject);
				return true;
			}
		} catch (Exception e) {
			logger.error("export service error,serviceName:"+serviceName,e);
		}
		logger.info("export service false,serviceName:"+serviceName);
		return false;
	}

	public boolean unExportService(String serviceName) {
		try {			 
			this.unexportService(serviceName);
			SpringContextUtil.removeBean(serviceName);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("unExportService service error,serviceName:"+serviceName,e);
		}
		return false;
	}

	// 发布mock服务
	private void exportService(String serviceName, GenericService genericObject) {
		// 发布mock服务
		ServiceConfig<GenericService> genericService =  createMockService(serviceName, genericObject);
		genericService.export();
		// 将服务添加到Context
		addGenericServiceToContext(serviceName, genericService);
	}

	private void addGenericServiceToContext(String serviceName, final ServiceConfig<GenericService> genericService) {
		skeletonMap.put(serviceName, genericService);
	}


	private void unexportService(String serviceName) throws Exception{
		ServiceConfig<GenericService> genericService =   skeletonMap.get(serviceName);;
		if(genericService!=null){
			genericService.unexport();
		}
	}

	private ServiceConfig<GenericService> createMockService(String serviceName, GenericService genericObject) {

		ServiceConfig<GenericService> serviceCfg = new ServiceConfig<GenericService>();
		serviceCfg.setRegistry(dubboRegistry);
		serviceCfg.setInterface(serviceName);
		//serviceCfg.setVersion
		//serviceCfg.setGroup
		serviceCfg.setRef(genericObject);

		return serviceCfg;
	}

	public GenericService  createDubboMockClassByJavasist(String serviceName) throws CannotCompileException, IOException, BeansException, ClassNotFoundException, NotFoundException, InstantiationException, IllegalAccessException{

		String interfaceName=serviceName;
		ClassPool pool =new ClassPool(true);
		pool.importPackage(Demo.PackageName);
		pool.insertClassPath(new ClassClassPath(this.getClass()));
		CtClass interClass = pool.get(Const.DUBBO_GENERIC_SERVICE);
		CtClass proxyClass = null;
		String className=interfaceName+Const.GenericService;	
		proxyClass=	 pool.makeClass(className);
		proxyClass.addInterface(interClass);
		CtMethod genericMethod = CtNewMethod.make(Const.GenericMethod,proxyClass);
		genericMethod.setBody(Demo.DubboMethodBody);
		proxyClass.addMethod(genericMethod);
		proxyClass.writeFile(this.getClass().getResource("/").getPath());
		proxyClass.detach();
		try {
			MockClassLoader classloader=new MockClassLoader(this.getBeanClassLoader());
			SpringContextUtil.registerBean(interfaceName,classloader.findClass(className));
			Object bean=SpringContextUtil.getBean(interfaceName);
			return (GenericService) bean;
		} catch (Error e) {
			throw new InstantiationException();
		}
	}

	public GenericService  createDubboMockClassByGroovy(String serviceName) throws CannotCompileException, IOException, BeansException, ClassNotFoundException, NotFoundException, InstantiationException, IllegalAccessException{

		String fileName=serviceName+".groovy";
		File groovyFile=new File(GroovyDirectory+fileName);

		try {
			GroovyClassLoader loader = new GroovyClassLoader();
			Class groovyClass = loader.parseClass(groovyFile);
			SpringContextUtil.registerBean(serviceName,groovyClass);
			Object bean=SpringContextUtil.getBean(serviceName);
			return (GenericService) bean;
		} catch (Error e) {
			throw new InstantiationException();
		}
	}


	private ClassLoader getBeanClassLoader() {
		// TODO Auto-generated method stub
		return  ClassUtils.getDefaultClassLoader();
	}

}
