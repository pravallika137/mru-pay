import React, { useState } from "react";
import "./App.css";

function RegistrationPage({ onRegister, goToLogin }) {
  const [data, setData] = useState({
    name: "",
    phoneNumber: "",
    upiId: "",
    pin: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleChange = (e) => {
    setData({ ...data, [e.target.name]: e.target.value });
    setError("");
  };

  const isValidUPI = (upi) => {
    const upiRegex = /^[\w.-]+@[\w.-]+$/;
    return upiRegex.test(upi);
  };

  const handleRegister = async (e) => {
    e.preventDefault();
    setError("");

    if (!data.name || !data.phoneNumber || !data.pin || !data.upiId) {
      setError("❌ Fill all fields");
      return;
    }

    if (data.phoneNumber.length !== 10) {
      setError("❌ Phone number must be 10 digits");
      return;
    }

    if (data.pin.length !== 4) {
      setError("❌ PIN must be 4 digits");
      return;
    }

    if (!isValidUPI(data.upiId)) {
      setError("❌ Invalid UPI ID");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/users/register", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          accept: "*/*",
        },
        body: JSON.stringify({
          name: data.name,
          phoneNumber: data.phoneNumber,
          upiId: data.upiId,
          pin: data.pin,
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || "Registration failed");
      }

      // Save user data locally
      localStorage.setItem("user", JSON.stringify(data));

      alert("✅ Registration successful!");
      onRegister({ name: data.name, phoneNumber: data.phoneNumber });
    } catch (err) {
      setError(`❌ ${err.message}`);
      console.error("Registration error:", err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h3>Register</h3>

        {error && <p style={{ color: "red", marginBottom: "10px" }}>{error}</p>}

        <input
          className="input"
          name="name"
          placeholder="Full Name"
          value={data.name}
          onChange={handleChange}
          disabled={loading}
        />

        <input
          className="input"
          name="phoneNumber"
          placeholder="Phone Number"
          value={data.phoneNumber}
          onChange={handleChange}
          disabled={loading}
        />

        <input
          className="input"
          name="upiId"
          placeholder="UPI ID (example@upi)"
          value={data.upiId}
          onChange={handleChange}
          disabled={loading}
        />

        <input
          className="input"
          type="password"
          name="pin"
          placeholder="Create PIN (4 digits)"
          value={data.pin}
          onChange={handleChange}
          disabled={loading}
        />

        <button className="btn" onClick={handleRegister} disabled={loading}>
          {loading ? "Registering..." : "Register"}
        </button>

        <p className="link">
          Already have an account?{" "}
          <span onClick={goToLogin} style={{ cursor: "pointer" }}>
            Login here
          </span>
        </p>
      </div>
    </div>
  );
}

export default RegistrationPage;