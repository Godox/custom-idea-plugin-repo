FROM gradle:7.5.1-jdk11-alpine as builder
USER root
WORKDIR /builder
ADD . /builder
RUN gradle build --stacktrace

FROM openjdk:17-alpine
WORKDIR /app
EXPOSE 8080
COPY --from=builder /builder/build/api/cjpr.jar .
CMD ["java", "-jar", "cjpr.jar"]