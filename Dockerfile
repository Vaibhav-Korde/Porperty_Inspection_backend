FROM eclipse-temurin:17-jdk

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests

EXPOSE 8080

CMD ["sh", "-c", "java -jar target/property-inspection-backend-0.0.2-SNAPSHOT.jar --server.port=${PORT:-8080}"]
