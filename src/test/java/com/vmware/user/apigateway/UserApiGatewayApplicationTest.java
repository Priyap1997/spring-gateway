/* ***************************************************************************
 * Copyright 2017 VMware, Inc.  All rights reserved.
 * -- VMware Confidential
 * ***************************************************************************/

package com.vmware.user.apigateway;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.user.apigateway.controller.ApiGatewayController;
import com.vmware.user.apigateway.model.StatusVO;

/**
 * Test class for API gateway service
 * 
 * @author Hitesh Wagh
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = { "classpath:application.properties", "classpath:application-test.properties"})
@ContextConfiguration(classes = UserApiGatewayApplication.class)
@WithMockUser(username = "admin", password = "admin", roles = "ACTRADMIN")
public class UserApiGatewayApplicationTest {

    private static final Logger logger = LoggerFactory.getLogger(UserApiGatewayApplicationTest.class);

    @Autowired
    ObjectMapper mapper;
    
    @Before
    public void init() {
    }
    
    @Autowired
    private ApiGatewayController apiGatewayController;

    @Test
    public void testingPing() {
    	
    	Locale locale = new Locale("English");
    	apiGatewayController.ping(locale);	
    }
    
    /**
     * Testing context load
     * 
     */
    @Test
    public void contextLoads() {
    }

}
