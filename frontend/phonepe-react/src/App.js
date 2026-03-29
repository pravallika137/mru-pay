import React, { useState } from "react";
import LoginPage from "./LoginPage";
import RegistrationPage from "./RegistrationPage";
import Dashboard from "./Dashboard";

function App() {
  const [page, setPage] = useState("login");
  const [user, setUser] = useState(null);

  return (
    <div>
      {page === "login" && (
        <LoginPage
          onLogin={(userData) => {
            setUser(userData);
            setPage("dashboard");
          }}
          goToRegister={() => setPage("register")}
        />
      )}

      {page === "register" && (
        <RegistrationPage
          onRegister={(userData) => {
            setUser(userData);
            setPage("dashboard");
          }}
          goToLogin={() => setPage("login")}
        />
      )}

      {page === "dashboard" && (
        <Dashboard user={user} onLogout={() => setPage("login")} />
      )}
    </div>
  );
}

export default App;
