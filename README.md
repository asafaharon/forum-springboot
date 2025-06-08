# Forum - Spring Boot Web Application

This project is a full-featured web-based forum built with **Java Spring Boot**, enabling users to register, create and manage discussion topics, send private and public messages, and perform administrative actions.

## 🌐 Features

- ✅ User Registration & Login (with encrypted passwords via BCrypt)
- ✅ Role-based Authorization using Spring Security (admin vs regular users)
- ✅ Forum Topics (CRUD operations)
- ✅ Public Messages within topics
- ✅ Private Messaging between users
- ✅ Admin Dashboard for managing users and topics
- ✅ Import and Export Topics via XML (JAXB)
- ✅ Scheduled Cleanup Tasks using Spring's `@Scheduled`
- ✅ Thymeleaf-based dynamic HTML templates
- ✅ Fully integrated with **MySQL**

---

## 🛠️ Technologies Used

- Java 21
- Spring Boot 3.1.x
- Spring Security
- Spring Data JPA (Hibernate)
- MySQL Database
- Thymeleaf
- Maven
- JAXB (for XML)
- IntelliJ IDEA

---

## 📦 Installation & Running

### 1. Clone the repository:

```bash
git clone https://github.com/asafaharon/forum-springboot.git
cd forum-springboot
2. Configure MySQL Database:
Edit your src/main/resources/application.properties file:

properties
Copy
Edit
spring.datasource.url=jdbc:mysql://localhost:3306/forumdb
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
3. Run the app:
bash
Copy
Edit
mvn spring-boot:run
The app will be accessible at: http://localhost:8080

🧪 Default Admin Account
Username: admin

Password: admin123

You can change or remove this in data.sql or the seed logic.

📁 Project Structure
css
Copy
Edit
src/
├── main/java/com/example/forum/
│   ├── controllers/
│   ├── entities/
│   ├── repositories/
│   ├── services/
│   ├── scheduler/
│   ├── config/
│   └── ForumApplication.java
├── main/resources/
│   ├── templates/        <- Thymeleaf HTML pages
│   └── application.properties
📄 License
This project is for educational use only. You may adapt and modify it as needed.

🙋‍♂️ Author
Asaf Aharon
