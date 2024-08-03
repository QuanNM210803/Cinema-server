#build

# alpine giam kich thuoc image
FROM maven:3.9.8-amazoncorretto-21 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn package -DskipTests

#run
FROM openjdk:19-alpine

WORKDIR /app
COPY --from=build  /app/target/cinema-server-0.0.1-SNAPSHOT.jar cinema-server-0.0.1-SNAPSHOT.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cinema-server-0.0.1-SNAPSHOT.jar"]