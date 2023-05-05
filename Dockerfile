#FROM gradle:7.4-jdk17-alpine AS builder
#WORKDIR /artifact

#COPY . .
#RUN gradle build --no-daemon --stacktrace

FROM amazoncorretto:17.0.6-alpine3.17
WORKDIR /app

EXPOSE 8080
ENV PORT 8080

COPY build/libs/app-universidad-1.0-SNAPSHOT.war app-universidad.jar
CMD ["java", "-jar", "/app/app-universidad.jar", "--server.port=${PORT}"]