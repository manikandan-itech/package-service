FROM openjdk:11
WORKDIR /opt
ENV PORT 8080
EXPOSE 8080
COPY target/*.jar /opt/package-service.jar
ENTRYPOINT exec java $JAVA_OPTS -jar package-service.jar