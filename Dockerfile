#FROM gradle:7.4-jdk17-alpine AS builder
#WORKDIR /app
#
#COPY . .
#RUN gradle build --no-daemon --stacktrace
#
#FROM amazoncorretto:17.0.6-alpine3.17
#WORKDIR /app
#
#EXPOSE 8080
#ENV PORT 8080
#
#COPY --from=builder build/libs/app-universidad-1.0-SNAPSHOT.war app-universidad.jar
#COPY waitingDatabase.sh waitingDatabase.sh
#RUN chmod +x /app/waitingDatabase.sh
#CMD ["sh", "-c", "/app/waitingDatabase.sh"]

FROM gradle:7.4-jdk17-alpine AS builder
COPY --chown=gradle:gradle . /home/gradle/src/producer
WORKDIR /home/gradle/src/producer
RUN gradle build --no-daemon --stacktrace


FROM amazoncorretto:17.0.6-alpine3.17
ARG JAR_FILE=build/libs/*.jar

EXPOSE 8080
ENV PORT 8080
COPY --from=builder /home/gradle/src/producer/build/libs/app-universidad-1.0-SNAPSHOT.war app-universidad.jar
COPY waitingDatabase.sh waitingDatabase.sh
RUN chmod +x waitingDatabase.sh
CMD ["sh", "-c", "/waitingDatabase.sh"]