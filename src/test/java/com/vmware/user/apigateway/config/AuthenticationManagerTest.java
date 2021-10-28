package com.vmware.user.apigateway.config;

import javax.security.auth.login.LoginException;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.trace.http.HttpTrace.Principal;
import org.springframework.boot.autoconfigure.ldap.embedded.EmbeddedLdapProperties.Credential;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vmware.user.apigateway.UserApiGatewayApplication;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = { "classpath:application.properties", "classpath:application-test.properties"})
@ContextConfiguration(classes = UserApiGatewayApplication.class)
public class AuthenticationManagerTest {

	private static final Logger logger = LoggerFactory.getLogger(AuthenticationManagerTest.class);
	
	 @Autowired
	 ObjectMapper mapper;
	    
	 @Before
	 public void init() {
	 }
	 
	 @Autowired
	 private AuthenticationManager authenticationManager;
	 
	 
	 @Test
	 public void testAuthenticate() {
		 Credential creds= new Credential();
		 creds.setUsername("gatewayuser");
		 creds.setPassword("g@13w@2us3r");
		 Principal principal = new Principal("g@13w@2us3r");
		 Authentication auth = new UsernamePasswordAuthenticationToken(principal, creds);
		 authenticationManager.authenticate(auth);
		 
	 }
	 
	 @Test
	 public void testAuthenticateException() {
		 Credential creds= new Credential();
		 creds.setUsername("gatewayuser");
		 creds.setPassword("null");
		 Principal principal = new Principal("null");
		 Authentication auth = new UsernamePasswordAuthenticationToken(principal, creds);
		 authenticationManager.authenticate(auth);
		 
	 }
}
