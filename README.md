# 🍽️ DineReserve

[![Android](https://img.shields.io/badge/Platform-Android-green.svg?style=flat&logo=android)](https://developer.android.com/)
[![Kotlin](https://img.shields.io/badge/Language-Kotlin%202.0-purple.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Jetpack Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-blue.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![NestJS](https://img.shields.io/badge/Backend-NestJS-red.svg?style=flat&logo=nestjs)](https://nestjs.com/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-blue.svg?style=flat&logo=postgresql)](https://www.postgresql.org/)
[![Prisma](https://img.shields.io/badge/ORM-Prisma-darkblue.svg?style=flat&logo=prisma)](https://www.prisma.io/)
[![Socket.IO](https://img.shields.io/badge/RealTime-Socket.IO-black.svg?style=flat&logo=socketdotio)](https://socket.io/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=flat)](LICENSE)

**DineReserve** is a comprehensive, enterprise-grade restaurant reservation, QR in-dining, floor management, and real-time staff task synchronization system. Engineered for high-frequency restaurant operations, DineReserve seamlessly connects diners, floor waiters, kitchen managers, and restaurant owners through an intuitive Android mobile client and a high-performance NestJS backend microservice.

Whether diners are booking table reservations ahead of time, scanning QR codes at physical tables to dispatch instant service requests, or floor managers are auto-assigning waiter tasks, DineReserve ensures zero-latency real-time synchronization, offline data persistence, and fine-grained Role-Based Access Control (RBAC).

---

## ✨ Features

### 👤 Customer Features
- **User Authentication**: Secure Sign-up, Email/Password Login, Google OAuth, and JWT session persistence.
- **Restaurant Discovery & Search**: Interactive search with cuisine filters, price tier tags, location sorting, and ratings.
- **Table Booking Engine**: Instant reservation modal with time-slot selection, party size customization, and instant confirmation passes.
- **Interactive QR In-Dining Portal**: In-table QR code scanner with laser beam target alignment animations and instant table verification.
- **Instant Service Requests**: Dispatch waiter requests ("Call Waiter", "Refill Water", "Request Bill", "Clean Table") directly from the table.
- **Digital Menu & Ordering**: Browse categorised live menus, customize dietary preferences, and track active table orders.
- **Live Waitlist System**: Join digital queues remotely with dynamic estimated wait time calculation and queue position tracking.
- **Digital Wallet & Rewards**: Manage loyalty points, store promotional vouchers, and apply discount codes to active bills.
- **VIP Membership Portal**: Unlock exclusive tier perks, priority booking slots, and chef's special invitation passes.
- **Favorites & Reviews**: Rate dining experiences, attach review feedback, and save favorite culinary spots.
- **Offline Data Persistence**: Full Room database cache allowing access to saved bookings and history without network connection.
- **Material 3 Dark Theme**: High-contrast, premium twilight aesthetic optimized for OLED displays and ambient lighting.

### 👔 Staff Features
- **Staff Authentication**: Dedicated staff authentication and shift session state.
- **'My Tasks' Taskboard**: Dynamic taskboard broken down by **All**, **Pending**, **Active**, and **Done** tabs.
- **Interactive Task Cards**: State-morphed UI cards featuring glowing status borders, pulse animations for urgent requests, and single-tap task transitions (**Accept** → **Start Task** → **Complete Task**).
- **Real-time Dispatch Alerts**: Instant Socket.IO push updates whenever a customer places a table service request.
- **Table Service Management**: View table number, request type, timestamp, and dining notes in real-time.
- **Staff Availability Toggle**: Dynamic Online/Offline and Busy/Available status updates shared with managers.

### 📊 Manager & Owner Features
- **Operations Dashboard**: Real-time overview of active dining sessions, open table requests, and waitlist congestion.
- **Menu Builder Studio**: Interactive tool for managing menu items, pricing, allergens, and seasonal availability.
- **Staff Task & Floor Oversight**: Monitor waiter task resolution speed, reassign stuck tasks, and review historical response metrics.
- **Waitlist & Floor Allocator**: Seat waitlist parties, assign tables, and release cleared tables instantly.
- **Customer CRM & VIP Profiles**: Access customer visit frequency, dietary preferences, and spending analytics.

---

## 🛠️ Tech Stack

| Domain | Technology | Description |
| :--- | :--- | :--- |
| **Frontend Framework** | Jetpack Compose (M3) | Native declarative Android UI with Material Design 3 guidelines |
| **Language (App)** | Kotlin 2.0 | Strongly-typed coroutines-enabled mobile development |
| **Backend Framework** | NestJS | Modular, scalable Node.js server framework |
| **Language (Server)** | TypeScript | End-to-end typed API layer |
| **Database** | PostgreSQL | Relational ACID-compliant database |
| **ORM** | Prisma ORM | Type-safe database queries, migrations, and model relations |
| **Authentication** | JWT + Passport.js | Access & Refresh Token rotation with Role-Based Access Control (RBAC) |
| **Real-Time Engine** | Socket.IO | WebSockets gateway for instant bi-directional floor event synchronization |
| **Architecture** | MVVM + Clean Arch | Layered Repository pattern with Uni-directional Data Flow (UDF) |
| **State Management** | StateFlow / ViewModel | Reactive state hoisting using Kotlin Coroutines Flow |
| **Offline Storage** | Room Database (SQLite) | Local Android SQLite cache generated via KSP |
| **Networking** | Retrofit 2 + OkHttp 3 | REST API client with logging interceptors and authorization headers |
| **Image Loading** | Coil Compose | Asynchronous image loading and memory caching |
| **Dependency Injection**| Constructor Injection | Lightweight, predictable dependency management |
| **Build Tools** | Gradle (Kotlin DSL) / npm | Android build pipeline and Node package management |
| **Testing** | JUnit / Jest | JVM Unit testing and NestJS E2E integration testing |
| **Security** | Helmet / Rate Limiting | Request throttling, CORS policy, and secure HTTP headers |

---

## 🏗️ Architecture

DineReserve employs a modern **Offline-First MVVM Architecture** on Android, paired with a modular **Domain-Driven NestJS Microservice** backend.

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                 DineReserve Mobile Suite                               │
│  ┌───────────────────────┐   ┌──────────────────────────┐   ┌──────────────────────┐   │
│  │   Jetpack Compose UI  │ ◄─┼──    DineReserveViewModel│ ◄─┼──  Room Database DAO │   │
│  └───────────────────────┘   └──────────────────────────┘   └──────────────────────┘   │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
                     HTTPS REST Requests   │   WebSocket Events (Socket.IO)
                     (Retrofit + OkHttp)   │   (service_request_created, etc.)
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                 NestJS Microservice Core                               │
│  ┌───────────────────────┐   ┌──────────────────────────┐   ┌──────────────────────┐   │
│  │   Auth & RBAC Guard   │ ──┼──  Controllers & Services│ ──┼──   EventsGateway    │   │
│  └───────────────────────┘   └──────────────────────────┘   └──────────────────────┘   │
└──────────────────────────────────────────┬─────────────────────────────────────────────┘
                                           │
                                           │ Prisma Client
                                           ▼
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                                   PostgreSQL Database                                  │
│   [User] ─── [Booking] ─── [DiningSession] ─── [ServiceRequest] ─── [StaffTask]        │
└────────────────────────────────────────────────────────────────────────────────────────┘
```

### Key Architectural Concepts:
- **Repository Pattern**: Abstracts local Room DB caching and remote REST/WebSocket data sources into unified data streams for UI consumption.
- **Unidirectional Data Flow (UDF)**: ViewModels emit immutable `StateFlow` states; Compose UI renders state deterministically.
- **Real-Time Event Bus**: NestJS `@WebSocketGateway()` broadcasts socket events to specific restaurant rooms (`restaurant_{id}`).
- **Offline Synchronization**: Network calls fall back to local Room cache during connectivity drops.

---

## 📁 Folder Structure

```
DineReserve/
├── app/                                  # Android Mobile App Root
│   └── src/main/java/com/example/
│       ├── data/                         # Data Layer
│       │   ├── local/                    # Room Database, DAOs & TypeConverters
│       │   ├── model/                    # Data Entities & Models
│       │   └── repository/               # Repositories (DineReserveRepository)
│       ├── ui/                           # Presentation Layer (Compose M3)
│       │   ├── screens/                  # Application Screens (20+ Screens)
│       │   ├── theme/                    # Material 3 Design Tokens & Colors
│       │   └── viewmodel/                # DineReserveViewModel & UI State
│       └── MainActivity.kt               # Main Navigation Entry Point
├── backend/                              # NestJS Server Root
│   ├── prisma/                           # Database Schema & Migrations
│   │   └── schema.prisma                 # Prisma Models & Foreign Keys
│   └── src/                              # Backend Source Code
│       ├── auth/                         # JWT Auth, Signup, Login & RBAC Guards
│       ├── bookings/                     # Reservation Management Engine
│       ├── qr-dining/                    # In-Dining Portal & QR Logic
│       ├── service-requests/             # Table Service Request Handlers
│       ├── sockets/                      # Socket.IO Real-Time Gateway
│       └── staff-tasks/                  # Staff Task Dispatch Engine
├── gradle/                               # Gradle Wrapper & Version Catalog
├── build.gradle.kts                      # Root Gradle Configuration
├── settings.gradle.kts                   # Project & Dependency Settings
└── metadata.json                         # Platform Metadata Configuration
```

---

## 📋 Prerequisites

Before running DineReserve, ensure you have the following installed:

- **Android Studio**: Ladybug (2024.2.1+) or newer
- **Java Development Kit (JDK)**: Version 17 or higher
- **Node.js**: v18.0.0 or higher
- **npm**: v9.0.0 or higher
- **PostgreSQL**: v14.0 or higher
- **Android SDK**: API Level 34 (Android 14)

---

## 🚀 Installation

### 1. Clone Repository
```bash
git clone https://github.com/sharmalalit1800/DineReserve.git
cd DineReserve
```

### 2. Backend Setup
```bash
cd backend

# Install dependencies
npm install

# Configure environment variables
cp .env.example .env

# Generate Prisma Client
npx prisma generate

# Execute database migrations
npx prisma migrate dev --name init

# Start backend server
npm run start:dev
```

### 3. Android App Setup
1. Launch **Android Studio**.
2. Select **Open** and choose the `DineReserve` root directory.
3. Allow Gradle to sync dependencies automatically.
4. Launch an Android Virtual Device (AVD) running API 34+ or connect a physical Android device via USB debugging.
5. Click **Run 'app'** (`Shift + F10`) to build and launch the application.

---

## 🔑 Environment Variables

The backend relies on the following environment variables defined in `backend/.env`:

| Variable | Description | Example / Default Value |
| :--- | :--- | :--- |
| `DATABASE_URL` | PostgreSQL connection string | `postgresql://postgres:password@localhost:5432/dinereserve_db` |
| `PORT` | NestJS API port | `3000` |
| `SOCKET_PORT` | Socket.IO server port | `3000` |
| `JWT_SECRET` | Secret key for signing JWT Access Tokens | `super_secret_dinereserve_jwt_key_2026` |
| `JWT_REFRESH_SECRET` | Secret key for signing Refresh Tokens | `super_secret_dinereserve_refresh_key_2026` |
| `JWT_EXPIRES_IN` | Access token duration | `15m` |
| `JWT_REFRESH_EXPIRES_IN` | Refresh token duration | `7d` |
| `GEMINI_API_KEY` | Google Gemini AI key (Optional) | `AIzaSyD...` |

---

## 🗄️ Database Setup

DineReserve uses Prisma ORM paired with PostgreSQL.

```bash
# 1. Create PostgreSQL Database
createdb dinereserve_db

# 2. Push Schema to Database
npx prisma db push

# 3. Seed Database with Initial Data
npx prisma db seed

# 4. Launch Prisma Studio (Database GUI)
npx prisma studio
```

---

## 🏃 Running the Application

### Start NestJS Backend & Socket.IO Gateway
```bash
cd backend
npm run start:dev
```
> Server will start on `http://localhost:3000` with WebSockets active on `ws://localhost:3000`.

### Build & Run Android App
```bash
# Build Debug APK via Gradle
./gradlew assembleDebug

# Run Unit Tests
./gradlew testDebugUnitTest
```

---

## 📖 API Documentation

The NestJS backend includes built-in Swagger OpenAPI documentation.

- **Swagger UI**: `http://localhost:3000/api/docs`
- **JSON Spec**: `http://localhost:3000/api/docs-json`

### Core REST Endpoints:
- `POST /auth/signup` — Create customer or staff account
- `POST /auth/login` — Authenticate and receive JWT token pair
- `GET /restaurants` — Retrieve restaurant catalog and search results
- `POST /bookings` — Create a table reservation
- `POST /service-requests` — Dispatch a table service request
- `GET /staff-tasks/my-tasks` — Fetch assigned waiter tasks

---

## 🔐 Authentication & Security

- **JSON Web Tokens (JWT)**: Short-lived access tokens (15m) paired with long-lived HTTP-only refresh tokens (7d).
- **Password Hashing**: Passwords are securely hashed using `Argon2` / `Bcrypt` prior to persistence.
- **Role-Based Access Control (RBAC)**: Enforced via NestJS Guards (`@Roles('CUSTOMER', 'STAFF', 'MANAGER', 'ADMIN')`).
- **HTTP Security Headers**: Express apps secured using `Helmet`, `CORS` restrictions, and `ThrottlerModule` rate limiting.

---

## ⚡ Real-Time Events (Socket.IO)

Bi-directional communication powered by NestJS `EventsGateway`:

| Event Name | Direction | Description |
| :--- | :--- | :--- |
| `service_request_created` | Client ➔ Server ➔ Staff | Dispatched when a diner calls a waiter or requests a bill |
| `service_request_updated` | Server ➔ Client | Broadcasts service request status updates |
| `staff_task_assigned` | Server ➔ Staff App | Alerts assigned floor staff member of new task |
| `staff_task_accepted` | Staff ➔ Server ➔ Manager | Triggered when staff accepts a pending task |
| `staff_task_started` | Staff ➔ Server ➔ Manager | Triggered when staff starts serving table |
| `staff_task_completed` | Staff ➔ Server ➔ Manager | Triggered when task is resolved |
| `booking_updated` | Server ➔ Customer App | Pushes booking confirmation or status change alerts |

---

## 📱 Offline Support

DineReserve delivers a smooth offline experience via **Room Database**:

1. **Local Data Mirror**: Bookings, user profiles, active table sessions, and menu favorites are cached locally in SQLite.
2. **Read-Through Cache**: When offline, the app reads directly from Room storage.
3. **Optimistic UI**: UI state updates immediately, queuing network sync requests until connectivity is re-established.

---

## 📸 Screenshots

*(Placeholder slots for high-resolution app screenshots)*

| Home Discovery | Table Booking | QR Scanner | Staff Tasks |
| :---: | :---: | :---: | :---: |
| ![Home](https://via.placeholder.com/300x600?text=Home+Screen) | ![Booking](https://via.placeholder.com/300x600?text=Table+Booking) | ![QR Scanner](https://via.placeholder.com/300x600?text=QR+In-Dining) | ![Staff Tasks](https://via.placeholder.com/300x600?text=Staff+Taskboard) |

---

## 🧪 Testing

```bash
# Run Android JVM Unit Tests
./gradlew testDebugUnitTest

# Run Compose Lint Analysis
./gradlew lint

# Run Backend Jest Unit Tests
cd backend && npm run test

# Run Backend End-to-End Tests
cd backend && npm run test:e2e
```

---

## 🛡️ Security Audit & Hardening

- **Data Sanitization**: All incoming DTOs validated via `class-validator` and `class-transformer`.
- **SQL Injection Prevention**: Prisma ORM executes parameterized SQL queries natively.
- **XSS & CSRF Protection**: Protected headers injected by Helmet; stateful cookies secured with `SameSite=Strict`.

---

## ⚡ Performance Optimizations

- **Compose Recomposition Optimization**: Leveraged `remember`, `derivedStateOf`, and immutable data holders.
- **Animation Performance**: Hardware-accelerated infinite pulse and laser transition specs (`tween`, `animateColorAsState`).
- **Database Indexing**: Prisma models indexed by `restaurantId`, `bookingId`, `sessionId`, and `assignedStaffId`.
- **Lazy Loading**: `LazyColumn` and `LazyRow` components with memory-efficient item key reuse.

---

## ✅ Production Readiness Checklist

- [x] **Authentication & RBAC**: Fully implemented and tested.
- [x] **Real-Time Gateway**: Socket.IO events verified for table requests and task dispatch.
- [x] **Offline Cache**: Room database schema and fallback migrations verified.
- [x] **Build Status**: Clean Gradle compilation (`BUILD SUCCESSFUL`).
- [x] **Code Quality**: Zero Android Lint errors or missing import warnings.
- [x] **Material 3 Design**: Native theme tokens, dark mode palette, and accessibility touch targets (48dp+).

---

## 🔮 Future Enhancements

- [ ] **AI Recommendation Engine**: Personalized meal recommendations powered by Google Gemini API.
- [ ] **Firebase Cloud Messaging (FCM)**: Native push notification alerts for background queue updates.
- [ ] **Payment Gateway Integration**: Stripe & Apple Pay / Google Pay SDK integrations.
- [ ] **Redis Caching Layer**: Distributed Redis cache for high-throughput menu lookups.

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/amazing-feature`).
3. Commit your changes (`git commit -m 'Add amazing feature'`).
4. Push to the branch (`git push origin feature/amazing-feature`).
5. Open a Pull Request.

---

## 📄 License

Distributed under the **MIT License**. See `LICENSE` for details.

---

## ✍️ Author

**Lalit Sharma** & **DineReserve Team**  
- Email: sharmalalit1800@gmail.com  
- Project Workspace: [Google AI Studio Build](https://ai.studio/build)
