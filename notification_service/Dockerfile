FROM openjdk:11.0.7-jdk-slim

COPY target/notification_service-2.7.16.jar /demo.jar
EXPOSE 8083
EXPOSE 5005

ENTRYPOINT ["java", "-jar", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005", "/demo.jar"]
