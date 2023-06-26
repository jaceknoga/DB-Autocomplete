FROM eclipse-temurin:20-jdk-jammy AS build
LABEL authors="Jan Stettner"
WORKDIR /app

COPY ../.mvn/ .mvn
COPY ../mvnw ../pom.xml ./
RUN ./mvnw dependency:resolve
#RUN mvn package -DskipTests

COPY ../src ./src

CMD ["./mvnw", "spring-boot:run"]

#FROM eclipse-temurin:20-jre-jammy
#WORKDIR /app
#COPY --from=build /app/target/myapp.jar ./myapp.jar
#CMD ["java", "-jar", "myapp.jar"]