FROM openjdk:11 as builder 

COPY . .

RUN ./gradlew jar

FROM openjdk:11 

COPY --from=builder /build/libs/pipeline-server.jar ./pipeline-server.jar

CMD ["java", "-jar", "pipeline-server.jar"]
