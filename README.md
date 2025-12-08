# HairStudio - Spring Boot Application

#### **Overview**
This repository contains the backend for the **HairStudio** application, built using **Spring Boot**. It provides APIs for user authentication, appointment booking, product management, order processing, and more. The backend is structured to support three user roles: **Administrator**, **Employee**, and **User**.

#### **Project Structure**
The project follows a typical Spring Boot architecture with the following structure:

- **com.hairstudio.api** — Main application package
    - **audit/**: Contains classes related to auditing, such as logging and tracking changes.
    - **common/**: Shared utility classes used across the application (e.g., constants, utility methods).
    - **controller/**: REST controllers that define API endpoints.
    - **dto/**: Data Transfer Objects, used to transfer data between layers.
    - **errors/**: Contains error definitions for various business logic cases (e.g., `BrandErrors`). These errors are used across the application for consistency in error messaging.
    - **exception/**: Custom exceptions for handling application-specific issues.
    - **model/**: The domain model, which contains the entities mapped to the database.
    - **repository/**: Data access layer using Spring Data JPA to interact with the database.
    - **security/**: Configuration for authentication and authorization (e.g., JWT-based authentication).
    - **service/**: Business logic layer, where the main functionality resides.

- **resources/**: Contains application properties (`application.properties`) and other configuration files, including:
    - **DatabaseCreationScripts/**: Folder containing SQL scripts for database setup.
    - **PostmanCollections/**: Folder containing predefined Postman collections for testing the API.

#### **Database Setup**
To set up the database, a script is provided under `resources/DatabaseCreationScripts/`.  
You can run the provided SQL script to create the necessary tables and schema for the HairStudio application.

- **DatabaseCreationScripts/**: Folder containing SQL scripts for database setup.

#### **Postman Collections**
Test the API using the predefined Postman collections. These collections are available in the `resources/PostmanCollections/` folder.  
You can import these collections directly into Postman to start testing the backend endpoints.

- **PostmanCollections/**: Folder containing predefined Postman collections for testing the API.

#### **Key Features**
- **Authentication**: Users can log in and register. A confirmation code is sent to the user's email for email verification during registration. Additionally, users can reset their password via email if they forget it.
- **Appointment Booking**: Users can book appointments based on each employee's defined working hours.
- **Web-Shop**: Browse and purchase products directly through the application.
- **Admin Management**: Administrators can manage services, product types, products, brand types, and users.
- **Work Hours**: Admins define employees' working hours; the system only allows booking during those hours.
- **Order Management**: Employees can update the status of orders and view their details.
- **Admin History View**: Administrators can track significant system changes through a table showing historical activities.

#### **User Roles**
- **Administrator**: Full access to manage services, products, brands, users, and employee work hours. Can also view a history of system changes.
- **Employee**: Can view and update the status of orders, as well as view their own scheduled appointments.
- **User**: Can register, log in, book appointments, and purchase products from the web-shop.

#### **Run Locally**
To run the backend locally, follow these steps:

1. **Clone the repository**:
    ```bash
    git clone https://github.com/dejan-codes/hair-studio-springboot-api.git
    ```

2. **Install Dependencies**:
   Make sure you have **Java 21 or later** and **Maven** installed on your local machine.

3. **Run the Application**:
   Navigate to the project folder and use Maven to run the application:
   ```bash
   mvn spring-boot:run