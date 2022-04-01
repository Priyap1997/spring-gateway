FROM artfact-prd-vip.vmware.com:5001/bitnami/java:jdk11_05
EXPOSE 8080
RUN yum install maven -y
RUN yum install unzip -y
WORKDIR /app
RUN mkdir -p /opt/dynatrace/oneagent
RUN chown -R 1001:1001 /opt/dynatrace/oneagent
RUN wget "https://artfact-prd-vip.vmware.com/artifactory/saas-it-utils/entrypoint.sh"
RUN chmod 0755 entrypoint.sh
COPY . /app
RUN chown -R 1001:1001 /app
RUN  mvn install
RUN cp /app/target/user-api-gateway.jar /app/user-api-gateway.jar
ENTRYPOINT ["sh", "-c","./entrypoint.sh 'user-api-gateway' '-Djdk.tls.client.protocols=TLSv1.2 /app/user-api-gateway.jar'"]
