import React, { useState } from "react";
import "./App.css";

const LoginPage = ({ onLogin, goToRegister }) => {
  const [phone, setPhone] = useState("");
  const [pin, setPin] = useState("");
  const [loading, setLoading] = useState(false);
  const [popup, setPopup] = useState("");

  const handleLogin = async () => {
    if (!phone || !pin) {
      setPopup("❌ Please enter phone number and PIN");
      return;
    }

    if (phone.length !== 10) {
      setPopup("❌ Phone number must be 10 digits");
      return;
    }

    setLoading(true);

    try {
      const response = await fetch("http://localhost:8080/api/users/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          accept: "*/*",
        },
        body: JSON.stringify({
          phoneNumber: phone,
          pin: pin,
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || "Login failed");
      }

      setPopup("✅ Login successful!");
      
      // Save user data from backend response
      const userData = result.data || result;
      localStorage.setItem("user", JSON.stringify(userData));

      setTimeout(() => {
        onLogin({
          name: userData.name,
          phoneNumber: userData.phoneNumber,
          upiId: userData.upiId,
        });
      }, 1000);
    } catch (err) {
      setPopup(`❌ ${err.message}`);
      console.error("Login error:", err);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <div className="container">
      <div className="card">
        <h2 className="title">Mru Pay</h2>
        <p className="subtitle">Malla Reddy College of Engineering</p>

        <h3>Login</h3>

        <input
          className="input"
          type="text"
          placeholder="Phone Number"
          value={phone}
          onChange={(e) => setPhone(e.target.value)}
          onKeyPress={handleKeyPress}
          disabled={loading}
        />

        <input
          className="input"
          type="password"
          placeholder="PIN"
          value={pin}
          onChange={(e) => setPin(e.target.value)}
          onKeyPress={handleKeyPress}
          disabled={loading}
        />

        <button className="btn" onClick={handleLogin} disabled={loading}>
          {loading ? "Logging in..." : "Login"}
        </button>

        <p className="link">
          Don't have an account?{" "}
          <span onClick={goToRegister} style={{ cursor: "pointer" }}>
            Register here
          </span>
        </p>
      </div>

      {popup && (
        <div className="popup">
          <div className="popup-box">
            <p>{popup}</p>
            <button onClick={() => setPopup("")}>OK</button>
          </div>
        </div>
      )}
    </div>
  );
};

export default LoginPage;