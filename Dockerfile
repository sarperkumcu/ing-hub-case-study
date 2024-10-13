FROM openjdk:21-jdk

WORKDIR /app

COPY target/brokerage-0.0.1-SNAPSHOT.jar /app/brokerage-0.0.1-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "brokerage-0.0.1-SNAPSHOT.jar", "-web -webAllowOthers -tcp -tcpAllowOthers -browser"]
