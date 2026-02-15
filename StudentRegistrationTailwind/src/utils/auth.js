// API utility functions for handling authentication

export const API_BASE_URL = "http://3.109.185.55:8080/api/students";

// Get token from localStorage
export const getAuthToken = () => {
  return localStorage.getItem("authToken");
};

// Check if user is authenticated
export const isAuthenticated = () => {
  const token = getAuthToken();
  if (!token) return false;

  try {
    // Simple token expiration check (you can enhance this)
    const payload = JSON.parse(atob(token.split(".")[1]));
    return payload.exp > Date.now() / 1000;
  } catch (error) {
    return false;
  }
};

// Logout function
export const logout = () => {
  localStorage.removeItem("authToken");
  localStorage.removeItem("userEmail");
  window.location.href = "/";
};

// API call with authentication
export const authenticatedFetch = async (url, options = {}) => {
  const token = getAuthToken();

  const defaultHeaders = {
    "Content-Type": "application/json",
  };

  if (token) {
    defaultHeaders["Authorization"] = `Bearer ${token}`;
  }

  const config = {
    ...options,
    headers: {
      ...defaultHeaders,
      ...options.headers,
    },
  };

  const response = await fetch(url, config);

  // If token is expired or invalid, redirect to login
  if (response.status === 401) {
    logout();
    return response;
  }

  return response;
};

// Register student with immediate login
export const registerStudent = async (studentData) => {
  const response = await fetch(`${API_BASE_URL}/register`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(studentData),
  });

  const result = await response.json();

  if (response.ok && result.token) {
    // Store token immediately after registration
    localStorage.setItem("authToken", result.token);
    localStorage.setItem("userEmail", result.student.email);
  }

  return { response, result };
};

// Get all students (protected)
export const getAllStudents = async () => {
  return await authenticatedFetch(API_BASE_URL);
};

// Update student (protected)
export const updateStudent = async (id, studentData) => {
  return await authenticatedFetch(`${API_BASE_URL}/${id}`, {
    method: "PUT",
    body: JSON.stringify(studentData),
  });
};

// Delete student (protected)
export const deleteStudent = async (id) => {
  return await authenticatedFetch(`${API_BASE_URL}/${id}`, {
    method: "DELETE",
  });
};
