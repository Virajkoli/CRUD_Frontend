import React, { createContext, useContext, useState, useEffect } from "react";
import { isAuthenticated, getAuthToken, logout } from "../utils/auth";

const AuthContext = createContext();

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};

export const AuthProvider = ({ children }) => {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [userEmail, setUserEmail] = useState("");
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Check authentication status on app load
    checkAuthStatus();
  }, []);

  const checkAuthStatus = () => {
    const authenticated = isAuthenticated();
    setIsLoggedIn(authenticated);

    if (authenticated) {
      const email = localStorage.getItem("userEmail");
      setUserEmail(email || "");
    }

    setLoading(false);
  };

  const login = (token, email) => {
    localStorage.setItem("authToken", token);
    localStorage.setItem("userEmail", email);
    setIsLoggedIn(true);
    setUserEmail(email);
  };

  const logoutUser = () => {
    logout();
    setIsLoggedIn(false);
    setUserEmail("");
  };

  const value = {
    isLoggedIn,
    userEmail,
    loading,
    login,
    logout: logoutUser,
    checkAuthStatus,
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
