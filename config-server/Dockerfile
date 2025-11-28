FROM eclipse-temurin:17-jdk

RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean install -Dmaven.test.skip=true

EXPOSE 8084

ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]