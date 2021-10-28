package com.vmware.user.apigateway.advice;

import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import javax.security.auth.login.LoginException;

import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;

import com.vmware.user.apigateway.UserApiGatewayApplication;

@SpringBootTest
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = { "classpath:application.properties", "classpath:application-test.properties"})
@ContextConfiguration(classes = UserApiGatewayApplication.class)
public class JsonExceptionHandlerTest {

	private static final Logger logger = LoggerFactory.getLogger(JsonExceptionHandlerTest.class);

	@Autowired
	private JsonExceptionHandler jsonExceptionHandler;
	
	@Test
	public void test_handle_NotFoundException() {
		
	//	ServerWebExchange exchange = mock(ServerWebExchange.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/dummy/test").build();
		ServerWebExchange exchange = MockServerWebExchange.from(request);
		
		Throwable throwable = new NotFoundException("NotFoundException");
		jsonExceptionHandler.handle(exchange, throwable);
	}
	
	@Test
	public void test_handle_ResponseStatusException() {
		
	//	ServerWebExchange exchange = mock(ServerWebExchange.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/dummy/test").build();
		ServerWebExchange exchange = MockServerWebExchange.from(request);
		
		Throwable throwable = new ResponseStatusException(HttpStatus.BAD_REQUEST);
		jsonExceptionHandler.handle(exchange, throwable);
	}
	
	@Test
	public void test_handle_LoginException() {
		
	//	ServerWebExchange exchange = mock(ServerWebExchange.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/dummy/test").build();
		ServerWebExchange exchange = MockServerWebExchange.from(request);
		
		Throwable throwable = new LoginException("LoginException");
		jsonExceptionHandler.handle(exchange, throwable);
	}
	
	@Test
	public void test_handle_LoginExceptionCorrectUrl() {
		
	//	ServerWebExchange exchange = mock(ServerWebExchange.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/ping").build();
		ServerWebExchange exchange = MockServerWebExchange.from(request);
		
		Throwable throwable = new LoginException("LoginException");
		jsonExceptionHandler.handle(exchange, throwable);
	}
	
	@Test
	public void test_handle_InternalServerError() {
		
	//	ServerWebExchange exchange = mock(ServerWebExchange.class);
		MockServerHttpRequest request = MockServerHttpRequest.get("/ping1").build();
		ServerWebExchange exchange = MockServerWebExchange.from(request);
		
		Throwable throwable = new Exception("INTERNAL_SERVER_ERROR");
		jsonExceptionHandler.handle(exchange, throwable);
	}
	

}
