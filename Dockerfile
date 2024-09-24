FROM alpine/java:21-jdk
LABEL authors="toms223"
COPY "build/libs/Contas-1.0-SNAPSHOT.jar" "app.jar"
ENTRYPOINT ["java", "-jar", "app.jar"]