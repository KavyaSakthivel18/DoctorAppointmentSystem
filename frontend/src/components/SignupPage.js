import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./LoginPage.css";
import API from "../apiConfig";

function SignupPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: "",
    dob: "",
    gender: "",
    email: "",
    password: "",
    confirmPassword: "",
    mobile: ""
  });

  const handleSubmit = async () => {
    if (!formData.name || !formData.email || !formData.password || !formData.dob) {
      alert("Name, Email, Password, and DOB are required");
      return;
    }

    const selectedDate = new Date(formData.dob);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (selectedDate > today) {
      alert("Date of Birth cannot be in the future");
      return;
    }

    if (formData.password !== formData.confirmPassword) {
      alert("Passwords do not match");
      return;
    }

    try {
      const response = await API.post("/auth/register", {
        name: formData.name,
        email: formData.email,
        password: formData.password,
        dob: formData.dob,
        gender: formData.gender,
        mobile: formData.mobile,
        role: "PATIENT"
      });

      if (response.status === 200 || response.status === 201) {
        alert("Signup Successful. Please Login.");
        navigate("/login");
      }
    } catch (error) {
      console.error("Signup Error:", error);
      if (error.response) {
        alert(error.response.data || "Signup failed");
      } else {
        alert("Network error. Is the backend running?");
      }
    }
  };

  const todayStr = new Date().toISOString().split("T")[0];

  return (
    <div className="auth-page">
      <div className="mesh-gradient"></div>
      <div className="auth-container" style={{ maxWidth: "600px" }}>
        <div className="auth-brand" onClick={() => navigate("/")}>
          Medi<span>Sync</span>
        </div>
        <div className="auth-card">
          <h2>Create Account</h2>
          <p className="auth-subtitle">Join MediSync for better health management</p>

          <div className="auth-form-grid">
            <div className="input-group">
              <label>Full Name</label>
              <input
                type="text"
                placeholder="John Doe"
                value={formData.name}
                onChange={(e) => setFormData({ ...formData, name: e.target.value })}
              />
            </div>

            <div className="input-group">
              <label>Date of Birth</label>
              <input
                type="date"
                max={todayStr}
                value={formData.dob}
                onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
              />
            </div>

            <div className="input-group">
              <label>Gender</label>
              <select
                value={formData.gender}
                onChange={(e) => setFormData({ ...formData, gender: e.target.value })}
                className="auth-select"
              >
                <option value="">Select Gender</option>
                <option value="Male">Male</option>
                <option value="Female">Female</option>
                <option value="Other">Other</option>
              </select>
            </div>

            <div className="input-group">
              <label>Mobile Number</label>
              <input
                type="text"
                placeholder="10-digit number"
                value={formData.mobile}
                onChange={(e) => setFormData({ ...formData, mobile: e.target.value })}
              />
            </div>

            <div className="input-group full-width">
              <label>Email Address</label>
              <input
                type="email"
                placeholder="name@example.com"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
              />
            </div>

            <div className="input-group">
              <label>Password</label>
              <input
                type="password"
                placeholder="Create password"
                value={formData.password}
                onChange={(e) => setFormData({ ...formData, password: e.target.value })}
              />
            </div>

            <div className="input-group">
              <label>Confirm Password</label>
              <input
                type="password"
                placeholder="Repeat password"
                value={formData.confirmPassword}
                onChange={(e) => setFormData({ ...formData, confirmPassword: e.target.value })}
              />
            </div>
          </div>

          <button className="auth-btn" onClick={handleSubmit} style={{ width: "100%", marginTop: "24px" }}>
            Create Account
          </button>

          <div className="auth-footer">
            Already have an account?{" "}
            <span className="auth-link" onClick={() => navigate("/login")}>
              Sign In
            </span>
          </div>
        </div>
      </div>
    </div>
  );
}

export default SignupPage;