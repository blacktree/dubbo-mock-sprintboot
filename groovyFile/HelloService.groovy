import lz.dubbo.utils.*;

import com.alibaba.dubbo.rpc.service.GenericException
import com.alibaba.dubbo.rpc.service.GenericService

class HelloService implements GenericService {

	@Override
	public Object $invoke(String method, String[] parameterTypes, Object[] args) throws GenericException {
		// TODO Auto-generated method stub
		
		if(method.equals("sayHello")) {
			return "hello!";
		}
		
		//如果想获取spring 中其他bean 来执行，可以利用 SpringContextUtil.getBean("xxbeanName");
        //SpringContextUtil.getBean("xxbeanName");
	}
}
