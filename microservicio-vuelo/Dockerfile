FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY .mvn/ .mvn
COPY mvnw pom.xml ./

RUN ./mvnw dependency:go-offline

COPY src ./src

RUN ./mvnw clean install -Dmaven.test.skip=true

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java -jar target/*.jar"]