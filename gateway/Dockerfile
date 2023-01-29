# The context for this Docker file must be the root project directory ../
FROM amazoncorretto:11-alpine
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
COPY gateway/target/*.jar shareit-gateway.jar
COPY common/target/*.jar shareit-common.jar
ENTRYPOINT ["java","-jar","/shareit-gateway.jar", "-cp", "/shareit-common.jar"]