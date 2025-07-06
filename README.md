# LexiAI - Legal Research Backend

LexiAI is a comprehensive Java Spring Boot backend designed to intelligently search and fetch case details from Indian High Courts and District Courts. The system provides intelligent case search with database caching and web scraping capabilities.

## Features

### Core Functionality
- **Smart Case Search**: Database-first search with web scraping fallback
- **Multi-Court Support**: Supreme Court, High Courts, and District Courts
- **Authentication**: JWT-based user authentication and authorization
- **User Management**: Lawyer and firm management system
- **Search Analytics**: Track search history and popular cases
- **Web Scraping**: Advanced scraping with Selenium and Jsoup

### Technical Features
- Clean modular architecture (Controller → Service → Repository → Entity)
- RESTful API design
- MySQL database with JPA/Hibernate
- Global exception handling
- Input validation
- CORS support
- Comprehensive logging

## Tech Stack

- **Framework**: Spring Boot 3.0.0
- **Language**: Java 17
- **Database**: MySQL 8.0+
- **Security**: Spring Security with JWT
- **Web Scraping**: Selenium WebDriver, Jsoup
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger ready

## Prerequisites

- Java 17 or higher
- MySQL 8.0 or higher
- Maven 3.6+
- Chrome browser (for Selenium)

## Quick Start

### 1. Database Setup
```sql
CREATE DATABASE lexiai_db;
CREATE USER 'root'@'localhost' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON lexiai_db.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 2. Configuration
Update `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lexiai_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Run Application
```bash
cd lexiai-backend
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Documentation

### Public Endpoints

#### Health Check
```http
GET /api/public/health
```

#### Application Info
```http
GET /api/public/info
```

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "password": "password123",
  "phoneNumber": "+91 9876543210",
  "specialization": "Corporate Law",
  "barNumber": "BAR12345",
  "yearsOfExperience": 5,
  "firmName": "Doe & Associates",
  "firmEmail": "info@doelaw.com",
  "firmPhone": "+91 11 12345678",
  "firmAddress": "123 Legal Street",
  "firmCity": "New Delhi",
  "firmState": "Delhi",
  "firmCountry": "India"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "password123"
}
```

Response:
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "john.doe@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "firmName": "Doe & Associates",
  "success": true
}
```

#### Check Email Availability
```http
GET /api/auth/check-email?email=test@example.com
```

### Case Search Endpoints (Requires Authentication)

#### Search Cases (POST)
```http
POST /api/search/cases
Authorization: Bearer <token>
Content-Type: application/json

{
  "query": "contract dispute",
  "caseType": "Civil",
  "courtName": "Delhi High Court",
  "jurisdiction": "State",
  "searchType": "keyword",
  "page": 0,
  "size": 10
}
```

#### Search Cases (GET)
```http
GET /api/search/cases?q=contract%20dispute&caseType=Civil&courtName=Delhi%20High%20Court&page=0&size=10
Authorization: Bearer <token>
```

Response:
```json
{
  "cases": [
    {
      "id": 1,
      "caseNumber": "CS(OS) 123/2023",
      "title": "ABC Corp vs XYZ Ltd",
      "description": "Contract dispute case...",
      "courtName": "Delhi High Court",
      "caseType": "Civil",
      "caseStatus": "Pending",
      "filingDate": "2023-01-15",
      "judgeName": "Justice A.K. Sharma",
      "plaintiff": "ABC Corp",
      "defendant": "XYZ Ltd",
      "jurisdiction": "State",
      "sourceType": "Web Scraping",
      "searchCount": 5
    }
  ],
  "totalResults": 1,
  "currentPage": 0,
  "totalPages": 1,
  "searchQuery": "contract dispute",
  "dataSource": "database",
  "responseTimeMs": 150,
  "hasMoreResults": false
}
```

#### Get Case by ID
```http
GET /api/search/cases/{id}
Authorization: Bearer <token>
```

#### Get Case by Case Number
```http
GET /api/search/cases/number/{caseNumber}
Authorization: Bearer <token>
```

#### Get Popular Cases
```http
GET /api/search/cases/popular?limit=10
Authorization: Bearer <token>
```

#### Get Recent Cases
```http
GET /api/search/cases/recent?limit=10
Authorization: Bearer <token>
```

### User Management Endpoints (Requires Authentication)

#### Get User Profile
```http
GET /api/user/profile
Authorization: Bearer <token>
```

#### Update User Profile
```http
PUT /api/user/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+91 9876543210",
  "specialization": "Corporate Law",
  "barNumber": "BAR12345",
  "yearsOfExperience": 6
}
```

#### Get Search History
```http
GET /api/user/search-history?limit=50
Authorization: Bearer <token>
```

#### Get Search Statistics
```http
GET /api/user/search-stats
Authorization: Bearer <token>
```

## Search Types

- **`keyword`**: General keyword search across title, description, case summary
- **`case_number`**: Exact case number search
- **`title`**: Search in case titles
- **`party`**: Search by plaintiff or defendant names

## Web Scraping Sources

### Supported Courts
- **Supreme Court of India**: `https://main.sci.gov.in/`
- **Delhi High Court**: `http://delhihighcourt.nic.in/`
- **Bombay High Court**: `http://bombayhighcourt.nic.in/`
- **Madras High Court**: `http://hcmadras.tn.nic.in/`
- **Calcutta High Court**: `http://calcuttahighcourt.nic.in/`
- **eCourts**: `https://ecourts.gov.in/ecourts_home/`

### Scraping Strategy
1. **Database First**: Always check local database first
2. **Intelligent Fallback**: Use web scraping when database results are insufficient
3. **Smart Caching**: Automatically cache scraped results
4. **Court-Specific**: Different scraping strategies for different courts

## Error Handling

The API uses standard HTTP status codes and returns detailed error messages:

```json
{
  "status": 404,
  "message": "Legal Case not found with id : '123'",
  "timestamp": "2023-12-06T10:30:00",
  "validationErrors": {
    "field": "error message"
  }
}
```

## Security

- **JWT Authentication**: Secure token-based authentication
- **Role-Based Access**: LAWYER role for protected endpoints
- **Password Encryption**: BCrypt password hashing
- **CORS Configuration**: Configurable cross-origin resource sharing

## Development

### Project Structure
```
src/main/java/com/lexiai/
├── controller/          # REST controllers
├── service/            # Business logic
├── repository/         # Data access layer
├── model/              # Entity classes
├── dto/                # Data transfer objects
├── security/           # Security configuration
└── exception/          # Exception handling
```

### Building
```bash
mvn clean compile
mvn test
mvn package
```

### Running Tests
```bash
mvn test
```

## Configuration Options

### Application Properties
```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/lexiai_db
spring.datasource.username=root
spring.datasource.password=root

# JWT
lexiai.jwt.secret=your-secret-key
lexiai.jwt.expiration=86400

# Web Scraping
lexiai.scraping.timeout=30000
lexiai.scraping.max-retries=3
lexiai.scraping.delay-between-requests=1000
```

## Deployment

### Production Checklist
- [ ] Update database credentials
- [ ] Configure JWT secret
- [ ] Set up SSL/TLS
- [ ] Configure logging levels
- [ ] Set up monitoring
- [ ] Configure rate limiting

### Docker Deployment
```dockerfile
FROM openjdk:17-jdk-slim
COPY target/lexiai-backend-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## License

This project is licensed under the MIT License.

## Support

For support and questions:
- Create an issue on GitHub
- Contact: support@lexiai.com

## Roadmap

### Phase 1 (Current)
- [x] Basic case search
- [x] User authentication
- [x] Web scraping foundation

### Phase 2 (Planned)
- [ ] Advanced AI insights
- [ ] Full judgment text extraction
- [ ] Personalized dashboards
- [ ] Mobile app support

### Phase 3 (Future)
- [ ] Machine learning recommendations
- [ ] Legal citation analysis
- [ ] Court filing integration
- [ ] Multi-language support


        ### Made By Dipanshu(Pandey Ji)#   L e x i A i  
 