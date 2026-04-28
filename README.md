# 🏥 Doctor Appointment System

A full-stack **Doctor Appointment Booking System** built using **Spring Boot (Backend)** and **React (Frontend)**. This application enables patients to browse doctors by specialty, check availability, and book appointments in **Online (Teleconsultation)** or **Offline (In-Clinic)** modes.

---

## 🚀 Features

### 👤 Patient Features

* Browse medical specialties
* Filter doctors by **Online / Offline mode**
* View doctor availability (time slots)
* Book appointments
* Receive confirmation with:

  * 📹 Video link (Online)
  * 📍 Clinic details (Offline)
* Track appointment status:

  * `CONFIRMED`, `COMPLETED`, `CANCELLED`, `NO_SHOW`

---

### 🛠️ Admin Features

* Manage doctors and specialties
* Configure doctor availability (slots)
* View daily reports:

  * Appointments by mode
  * Revenue by specialty
* Monitor appointment trends

---

### 🔐 Security

* JWT-based Authentication & Authorization
* Role-based access (Admin / Patient)
* API rate limiting (optional)
* Input validation

---

### ⚙️ Business Rules

* ❗ Online and Offline appointments use **different doctors**
* 🔒 Slot locking prevents double booking
* 📊 Daily summary generation (appointments & revenue)
* 📌 Mode-specific artifacts:

  * Online → Video consultation link
  * Offline → Clinic address & instructions

---

## 🏗️ Tech Stack

### Backend (Spring Boot)

* Java 17+
* Spring Boot
* Spring Data JPA (Hibernate)
* Spring Security (JWT)
* H2 / MySQL
* Lombok
* Swagger (API Docs)

### Frontend (React)

* React JS
* Axios
* React Router
* Context API / Redux (optional)
* Tailwind CSS / Bootstrap

---

## 📂 Project Structure

```
doctor-appointment-system/
│
├── backend/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── config/
│   └── security/
│
├── frontend/
│   ├── components/
│   ├── pages/
│   ├── services/
│   ├── context/
│   └── routes/
│
└── README.md
```

---

## 🔌 API Endpoints (Sample)

### 🔑 Auth

```
POST /api/auth/register
POST /api/auth/login
```

### 👨‍⚕️ Doctor

```
GET /api/doctors?specialty=&mode=
GET /api/doctors/{id}/slots
```

### 📅 Appointment

```
POST /api/appointments
GET /api/appointments/{id}
PUT /api/appointments/{id}/status
```

### 📊 Reports

```
GET /api/reports/daily-summary
```

---

## 🗄️ Database Design (Core Tables)

* `User` (Patient/Admin)
* `Doctor` (mode: ONLINE/OFFLINE)
* `Specialty`
* `Appointment`
* `Slot`

---

## ▶️ How to Run

### 🖥️ Backend (Spring Boot)

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

* Runs on: `http://localhost:8080`
* Swagger: `http://localhost:8080/swagger-ui`

---

### 🌐 Frontend (React)

```bash
cd frontend
npm install
npm start
```

* Runs on: `http://localhost:3000`

---

## 🧪 Testing

* Use **Postman / Swagger** to test APIs
* Validate:

  * Slot booking
  * Mode-based doctor restriction
  * Status updates

---

## 📈 Stretch Features

* 📧 Email confirmation on booking
* ⏰ Automated reminders to reduce no-shows
* 📊 Admin dashboard with analytics
* 💳 Payment integration (optional)

---

## 🧠 Non-Functional Considerations

* **Reliability** → Transactional booking system
* **Performance** → Optimized doctor & slot queries
* **Security** → Encrypted data, JWT auth
* **Auditability** → Track appointment lifecycle

---

## 📌 Future Enhancements

* Real-time slot updates (WebSockets)
* Multi-language support
* Mobile app integration

---

## 👩‍💻 Contributors

* Team Members (Add names here)

---

## 📜 License

This project is for educational purposes.




