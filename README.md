# ⚖️ LexiAI - Legal Research Backend

**LexiAI** is a powerful Java Spring Boot backend designed for intelligent legal case search and retrieval from Indian **High Courts** and **District Courts**. It uses smart web scraping and MySQL caching to deliver real-time, structured case data via secure REST APIs. LexiAI is built with scalability in mind, supporting authentication, analytics, and court-specific scraping strategies.

---

## 🚀 Features

### 🎯 Core Functionality
- 🔍 **Smart Case Search** – Database-first search with intelligent web scraping fallback
- 🏛️ **Multi-Court Support** – Supports Supreme Court, High Courts, and District Courts
- 🔐 **Authentication System** – Secure JWT-based user login and access control
- 👤 **User Management** – Lawyers and firms with profile and search tracking
- 📊 **Search Analytics** – Monitor recent and popular case queries
- 🕸️ **Web Scraping Engine** – Uses Selenium and Jsoup for reliable court data extraction

### ⚙️ Technical Features
- ✅ Clean layered architecture (Controller → Service → Repository → Entity)
- ✅ RESTful API design with Swagger/OpenAPI docs
- ✅ MySQL 8.0+ integration with JPA/Hibernate
- ✅ Global exception handling and input validation
- ✅ CORS and security configurations
- ✅ Comprehensive request/response logging
