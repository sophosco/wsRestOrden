FROM openjdk:8-jdk-alpine
ADD target/wsRestPedido.jar wsRestPedido.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "wsRestPedido.jar"]  