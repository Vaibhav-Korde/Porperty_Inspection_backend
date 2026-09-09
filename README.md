# Property Inspection Spring Boot Backend

Requirements:
- Java 17+
- Maven 3.9+

Run:
mvn spring-boot:run

Health:
GET http://localhost:8080/api/reports/health

PDF:
POST http://localhost:8080/api/reports/generate

No database or permanent storage is used. The API receives multipart form data, generates the PDF in memory, and returns it to Angular.

The Angular client must send:
- propertyName
- address
- unitNumber
- ownerName
- inspectorName
- inspectionDate
- problems (JSON)
- photos (repeated multipart files)

Start Angular at http://localhost:4200.
