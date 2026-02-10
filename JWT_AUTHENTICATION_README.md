# JWT Authentication Implementation

This document describes the JWT authentication implementation added to the Student Registration application.

## What's Been Implemented

### Backend Changes

1. **Dependencies Added (pom.xml)**

   - Spring Security
   - JWT (io.jsonwebtoken)
   - BCrypt for password hashing

2. **Security Configuration**

   - `SecurityConfig`: Configures Spring Security with JWT
   - `JwtRequestFilter`: Validates JWT tokens on each request
   - `JwtAuthenticationEntryPoint`: Handles unauthorized access
   - `StudentUserDetailsService`: Implements UserDetailsService

3. **JWT Utilities**

   - `JwtUtil`: Handles token generation, validation, and extraction

4. **Entity Updates**

   - `Student`: Added @JsonIgnore to password field
   - Password hashing with BCrypt in `StudentService`

5. **Controller Updates**
   - Login endpoint now returns JWT token
   - Register endpoint returns JWT token for immediate login
   - Protected endpoints require authentication

### Frontend Changes

1. **Authentication Context**

   - `AuthContext`: Global authentication state management
   - `ProtectedRoute`: Component for protecting routes

2. **API Utilities**

   - `auth.js`: Functions for authenticated API calls
   - Automatic token management and logout on 401

3. **Component Updates**
   - `Login`: Stores JWT token and redirects authenticated users
   - `Home`: Uses authenticated API calls, displays user info, logout button
   - `App`: Wrapped with AuthProvider and protected routes

## Testing the Authentication

### 1. Start the Backend

```bash
cd "StudentRegistration"
./mvnw spring-boot:run
```

### 2. Start the Frontend

```bash
cd "StudentRegistrationTailwind"
npm run dev
```

### 3. Test Flow

1. **Register a new user**:

   - Go to home page
   - Click "Add Student"
   - Fill in student details
   - JWT token is automatically generated and stored

2. **Login**:

   - Go to login page
   - Enter registered email and password
   - JWT token is stored and user is redirected to home

3. **Protected Operations**:

   - View all students (requires authentication)
   - Update student (requires authentication)
   - Delete student (requires authentication)

4. **Logout**:
   - Click logout button
   - Token is removed and user is redirected to login

### 4. API Endpoints

#### Public Endpoints

- `POST /api/students/register` - Register new student
- `POST /api/students/login` - Login

#### Protected Endpoints (require JWT token)

- `GET /api/students` - Get all students
- `GET /api/students/{id}` - Get student by ID
- `PUT /api/students/{id}` - Update student
- `DELETE /api/students/{id}` - Delete student

### 5. Token Usage

Include JWT token in request headers:

```
Authorization: Bearer <your-jwt-token>
```

Token is automatically included by the frontend auth utility functions.

## Security Features

1. **Password Encryption**: BCrypt hashing
2. **JWT Token Expiration**: 5 hours
3. **Automatic Logout**: On token expiration or invalid token
4. **CORS Configuration**: Allows frontend requests
5. **Protected Routes**: Authentication required for CRUD operations

## Error Handling

- Invalid credentials return 401 status
- Expired/invalid tokens redirect to login
- Server errors return appropriate error messages
- User-friendly error messages in frontend
