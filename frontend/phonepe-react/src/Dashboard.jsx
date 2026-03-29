import React, { useState, useEffect } from "react";
import "./App.css";

const Dashboard = ({ user, onLogout }) => {
  const [balance, setBalance] = useState(0);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // Fetch wallet balance on component mount
  useEffect(() => {
    if (user?.upiId) {
      fetchWalletBalance(user.upiId);
    }
  }, [user?.upiId]);

  const fetchWalletBalance = async (upiId) => {
    try {
      const response = await fetch(`http://localhost:8080/api/wallet/${upiId}`);
      const result = await response.json();
      if (response.ok) {
        setBalance(result.data?.balance || 0);
      }
    } catch (err) {
      console.error("Error fetching balance:", err);
    }
  };

  const addMoney = async () => {
    const amount = prompt("Enter amount to add:");
    
    if (!amount || isNaN(amount) || Number(amount) <= 0) {
      setMessage("❌ Please enter a valid amount");
      setTimeout(() => setMessage(""), 3000);
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("http://localhost:8080/api/wallet/add", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          accept: "*/*",
        },
        body: JSON.stringify({
          upiId: user.upiId,
          amount: parseFloat(amount),
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || "Failed to add money");
      }

      setBalance(result.data?.balance || balance + parseFloat(amount));
      setMessage(`✅ ₹${amount} added successfully!`);
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      setMessage(`❌ ${err.message}`);
      setTimeout(() => setMessage(""), 3000);
      console.error("Add money error:", err);
    } finally {
      setLoading(false);
    }
  };

  const sendMoney = async () => {
    const receiverUpi = prompt("Enter receiver UPI ID:");
    
    if (!receiverUpi) {
      setMessage("❌ Receiver UPI ID is required");
      setTimeout(() => setMessage(""), 3000);
      return;
    }

    const amount = prompt("Enter amount to send:");
    
    if (!amount || isNaN(amount) || Number(amount) <= 0) {
      setMessage("❌ Please enter a valid amount");
      setTimeout(() => setMessage(""), 3000);
      return;
    }

    if (Number(amount) > balance) {
      setMessage("❌ Insufficient balance");
      setTimeout(() => setMessage(""), 3000);
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      const response = await fetch("http://localhost:8080/api/transaction/send", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          accept: "*/*",
        },
        body: JSON.stringify({
          senderUpi: user.upiId,
          receiverUpi: receiverUpi,
          amount: parseFloat(amount),
        }),
      });

      const result = await response.json();

      if (!response.ok) {
        throw new Error(result.message || "Failed to send money");
      }

      setBalance(balance - parseFloat(amount));
      setMessage(`✅ ₹${amount} sent to ${receiverUpi} successfully!`);
      setTimeout(() => setMessage(""), 3000);
    } catch (err) {
      setMessage(`❌ ${err.message}`);
      setTimeout(() => setMessage(""), 3000);
      console.error("Send money error:", err);
    } finally {
      setLoading(false);
    }
  };

  const viewTransactionHistory = async () => {
    if (!user?.upiId) {
      setMessage("❌ User UPI ID not found");
      return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/transaction/history/${user.upiId}`
      );
      const result = await response.json();

      if (response.ok) {
        const transactions = result.data || [];
        if (transactions.length === 0) {
          alert("📊 No transactions yet");
        } else {
          let historyText = "📊 Transaction History:\n\n";
          transactions.forEach((t) => {
            historyText += `${t.type}: ₹${t.amount} ${t.date}\n`;
          });
          alert(historyText);
        }
      } else {
        setMessage(`❌ ${result.message}`);
      }
    } catch (err) {
      setMessage(`❌ Error fetching history: ${err.message}`);
      console.error("History error:", err);
    }
  };

  return (
    <div className="dashboard">
      <div className="navbar">
        <h3>Mru Pay</h3>
        <div>
          <span>Welcome, {user?.name}</span>
          <button className="logout-btn" onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>

      {message && <div className="message">{message}</div>}

      <div className="wallet">
        <h2>₹{balance.toFixed(2)}</h2>
        <button className="btn" onClick={addMoney} disabled={loading}>
          {loading ? "Processing..." : "Add Money"}
        </button>
      </div>

      <div className="send-box">
        <h3>Send Money</h3>
        <button className="btn green" onClick={sendMoney} disabled={loading}>
          {loading ? "Processing..." : "Send Money"}
        </button>
      </div>

      <div className="send-box">
        <h3>History</h3>
        <button className="btn blue" onClick={viewTransactionHistory}>
          View Transactions
        </button>
      </div>
    </div>
  );
};

export default Dashboard;