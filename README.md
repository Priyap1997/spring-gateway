# spring-gateway
A simple Spring Gateway app

## How to Run Application
Start the application using any of the commands mentioned below

Note: First two commands need to run inside the root folder of this project i.e inside the spring-gateway folder

###### Using maven
mvn spring-boot:run

###### From jar file Create a jar file using 'mvn clean install' command and then execute
java -jar target/spring-api-gateway.jar

Directly from IDE
Right click on UserApiGatewayApplication.java and click on 'Run' option


Note: This spring boot application will starts on port number 8090.

Send an HTTP GET request to '/api/hello' endpoint using any of the two methods

###### **Postman**

http://localhost:8090/api/hello
header Authorization Basic dXNlcjp1c2VycGFzcw==

###### **cURL**

curl --location --request GET 'http://localhost:8090/api/hello' --header 'Authorization: Basic dXNlcjp1c2VycGFzcw=='
