package lz.dubbo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import lz.dubbo.service.MockService;
 

@Controller
@RequestMapping("/mock")
public class BaffleController {

	private static final Logger logger = LoggerFactory.getLogger(BaffleController.class);

	@Autowired
	private MockService mockService;
	
	@RequestMapping(value = "/export", method = RequestMethod.GET)
	@ResponseBody
	public String export(String serviceName,String type) {
		mockService.exportService(serviceName,type);
		return "export ok";
	}
	
	@RequestMapping(value = "/unexport", method = RequestMethod.GET)
	@ResponseBody
	public String upexport(String serviceName) {
		mockService.unExportService(serviceName);
		return "unexport ok";
	}
	
	
}
