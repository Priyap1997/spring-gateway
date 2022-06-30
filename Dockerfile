FROM artfact-prd-vip.vmware.com:5001/bitnami/java:jdk11_05
ENV MAVEN_VERSION 3.8.5
EXPOSE 8080
RUN yum install curl -y
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz | tar xzf - -C /usr/share \
  && mv /usr/share/apache-maven-$MAVEN_VERSION /usr/share/maven \
  && ln -s /usr/share/maven/bin/mvn /usr/bin/mvn
ENV MAVEN_HOME /usr/share/maven
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
ENTRYPOINT ["sh", "-c","./entrypoint.sh 'SaasUserServiceV2' '-Djdk.tls.client.protocols=TLSv1.2 /app/user-api-gateway.jar'"]
