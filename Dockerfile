FROM artfact-prd-vip.vmware.com:5001/bitnami/java:jdk11_05
EXPOSE 8080
COPY target/user-api-gateway.jar user-api-gateway.jar
ENTRYPOINT ["java","-Djdk.tls.client.protocols=TLSv1.2", "-jar", "user-api-gateway.jar"]
