# MediSync - Doctor Appointment System

MediSync is a comprehensive Doctor Appointment System that bridges the gap between healthcare providers and patients. It provides dedicated portals for Admins, Doctors, and Patients, facilitating seamless appointment booking, schedule management, and administrative control.

## Features

### Patient Portal
- User Authentication (Signup/Login)
- Search doctors by specialty
- Book appointments based on available slots
- View appointment history and status

### Doctor Portal
- Manage availability slots
- View scheduled appointments
- Update appointment status (e.g., completed, cancelled)

### Admin Portal
- Comprehensive dashboard with system statistics
- Manage doctors (Add, Update, Delete)
- Manage specialties
- View system-wide reports and appointments

## Technology Stack

### Backend
- **Java 21**
- **Spring Boot** (REST API)
- **Spring Security & JWT** (Authentication & Authorization)
- **Spring Data JPA** (ORM)
- **MySQL** (Database)
- **OpenAPI / Swagger** (API Documentation)
- **Maven** (Dependency Management)

### Frontend
- **React.js** 
- **React Router DOM** (Routing)
- **Axios** (API requests)
- **Vanilla CSS** (Styling)

## Prerequisites
- **Java 21** or higher
- **Node.js** and **npm**
- **MySQL** server

## Getting Started

### 1. Clone the repository - git clone https://github.com/KavyaSakthivel18/DoctorAppointmentSystem.git cd DoctorAppointmentSystem

### 2. Backend Setup
1. Navigate to the `backend` directory: cd backend
2. Make sure you have your MySQL server running. Update the `src/main/resources/application.properties` with your database credentials if necessary.
3. Build and run the Spring Boot application:

   mvn clean install
   mvn spring-boot:run
   
   The backend API will run at `http://localhost:8080`.

### 3. Frontend Setup
1. Open a new terminal and navigate to the `frontend` directory: cd frontend
2. Install dependencies: npm install
3. Start the React development server: npm start
   The application will run at `http://localhost:3000`.
