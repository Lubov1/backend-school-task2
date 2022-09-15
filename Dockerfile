FROM openjdk:17
ADD ./disk-1.0-SNAPSHOT.jar disk-1.0-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","disk-1.0-SNAPSHOT.jar"]
EXPOSE 80

