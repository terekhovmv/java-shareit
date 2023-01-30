# The context for this Docker file must be the root project directory ../
FROM amazoncorretto:11-alpine
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY server/target/shareit-server*.jar shareit-server.jar
COPY common/target/shareit-common*.jar shareit-common.jar
ENTRYPOINT ["java","-jar","/shareit-server.jar", "-cp", "/shareit-common.jar"]
