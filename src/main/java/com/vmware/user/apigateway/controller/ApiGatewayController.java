package com.vmware.user.apigateway.controller;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.vmware.user.apigateway.model.StatusVO;

/** 
 * <code>ApiGatewayController<code> exposes Gateway APIs. for eg. health check API
 * 
 * @author Hitesh Wagh
 */

@RestController
public class ApiGatewayController {

	 private final Logger logger = LoggerFactory.getLogger(ApiGatewayController.class);

	    public Long startTimestampInMilli;

	    @Value("${status.date.format}")
	    private String dateFormat;

	    @PostConstruct
	    private void init() {
	        startTimestampInMilli = System.currentTimeMillis();
	    }

	    /**
	     * This API is used to check the status of the Gateway application.
	     * 
	     * @param locale         Locale to specify language and country params.
	     * 
	     * @return               Returns the status for Gateway application.
	     */
	    @RequestMapping(value = "/ping", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	    public StatusVO ping(Locale locale) {

	        logger.info("Request recieved to check the status for Gateway application");

	        StatusVO statusVO = new StatusVO();

	        Date startDate = new Date(startTimestampInMilli);
	        String startDateString = new SimpleDateFormat(dateFormat).format(startDate);

	        String activeTime = String.valueOf(System.currentTimeMillis() - startTimestampInMilli);

	        statusVO.setStatus("UP");
	        statusVO.setStartTime(startDateString);
	        statusVO.setActiveTime(activeTime);

	        return statusVO;

	    }

}
