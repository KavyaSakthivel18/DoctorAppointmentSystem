
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";
import API from "../apiConfig";

function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const handleLogin = async () => {
    const enteredEmail = email.trim();
    const enteredPassword = password.trim();


    try {
      const response = await API.post("/auth/login", { 
        email: enteredEmail, 
        password: enteredPassword 
      });

      if (response.status === 200) {
        const user = response.data;
        localStorage.setItem("token", user.token);
        localStorage.setItem("role", user.role);
        localStorage.setItem("email", user.email);
        localStorage.setItem("userId", user.id);
        localStorage.setItem("name", user.name);

        if (user.role === "ADMIN") navigate("/admin");
        else if (user.role === "DOCTOR") navigate("/doctor");
        else if (user.role === "PATIENT") navigate("/patient");
      }
    } catch (error) {
      console.error("Login Error:", error);
      if (error.response) {
        let errorMsg = "Invalid credentials";
        if (error.response.data) {
           errorMsg = error.response.data.message || error.response.data || errorMsg;
        }
        alert(errorMsg);
      } else {
        alert("Network error. Is the backend running?");
      }
    }
  };

  return (
    <div className="auth-page">
      <div className="mesh-gradient"></div>
      <div className="auth-container">
        <div className="auth-brand" onClick={() => navigate("/")}>
          Medi<span>Sync</span>
        </div>
        <div className="auth-card">
          <h2>Welcome Back</h2>
          <p className="auth-subtitle">Sign in to continue to MediSync</p>

          <div className="auth-form">
            <div className="input-group">
              <label>Email Address</label>
              <input
                type="email"
                placeholder="name@example.com"
                onChange={(e) => setEmail(e.target.value)}
              />
            </div>

            <div className="input-group">
              <label>Password</label>
              <input
                type="password"
                placeholder="Enter your password"
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>

            <button className="auth-btn" onClick={handleLogin}>
              Sign In
            </button>
          </div>

          <div className="auth-footer">
            Don't have an account?{" "}
            <span className="auth-link" onClick={() => navigate("/signup")}>
              Create now
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;
