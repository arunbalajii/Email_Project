FROM openjdk:17-jdk-alpine


COPY target/Email-0.0.1-SNAPSHOT.jar Email.jar
ENTRYPOINT ["java","-jar","Email.jar"]