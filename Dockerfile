FROM artfact-prd-vip.vmware.com:5001/bitnami/java:jdk11_05
EXPOSE 8080
RUN yum  install maven -y
WORKDIR /app
COPY . /app
RUN chown -R 1001:1001 /app
RUN  mvn install
RUN cp /app/target/user-api-gateway.jar /app/user-api-gateway.jar
ENTRYPOINT ["java","-Djdk.tls.client.protocols=TLSv1.2", "-jar", "/app/user-api-gateway.jar"]
