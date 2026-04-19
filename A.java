# 🚀 Real-Time Market Tracking System

A **real-time crypto & stock tracking system** built using **Spring Boot, Kafka, and WebSockets**, following a **microservices architecture**.

---

## 📌 Project Overview

This project streams live market data from external sources and delivers it to the UI in real-time.

### 🔄 Flow

External APIs → Producer Service → Kafka → Consumer Service → WebSocket → UI

---

## 🏗️ Architecture

```
[Binance WebSocket]      [Finnhub WebSocket]
         │                         │
         ▼                         ▼
     Producer Service (Spring Boot)
                 │
                 ▼
             Apache Kafka
                 │
                 ▼
     Consumer Service (Spring Boot)
                 │
                 ▼
          WebSocket (/topic/market)
                 │
                 ▼
              Frontend UI
```

---

## ⚙️ Tech Stack

* **Backend**: Spring Boot
* **Messaging**: Apache Kafka
* **Real-time Communication**: WebSockets (STOMP + SockJS)
* **APIs**:

  * Binance (Crypto)
  * Finnhub (Stocks)
* **Frontend**: HTML, CSS, JavaScript

---

## 🔧 Microservices

### 1️⃣ Producer Service

* Connects to:

  * Binance WebSocket (crypto)
  * Finnhub WebSocket (stocks)
* Parses incoming data
* Publishes data to Kafka topics

---

### 2️⃣ Consumer Service

* Consumes data from Kafka topics
* Converts messages into DTOs
* Sends data to UI using WebSocket (`/topic/market`)

---

## 📊 Data Format

### Kafka Message (JSON)

```json
{
  "symbol": "BTCUSDT",
  "price": "75878.39",
  "type": "crypto"
}
```

---

## 🔌 WebSocket Endpoint

```
http://localhost:8080/ws
```

### Subscription

```
/topic/market
```

---

## ▶️ How to Run Locally

### 1. Start Kafka (KRaft Mode)

```bash
kafka-server-start.bat config\kraft\server.properties
```

---

### 2. Create Topic

```bash
kafka-topics.bat --create \
--topic live-crypto-tracking \
--bootstrap-server localhost:9092 \
--partitions 1 \
--replication-factor 1
```

---

### 3. Run Services

* Start **Producer Service**
* Start **Consumer Service**

---

### 4. Open UI

```
http://localhost:8080/cryptoTracker
```

---

## 🔐 Future Enhancements

* 🔑 Add authentication (JWT for WebSocket)
* ☁️ Deploy Kafka on cloud (Confluent / AWS MSK)
* 📊 Add charts (TradingView-like UI)
* ⭐ Dynamic watchlist (add/remove symbols)
* 🗄️ Store historical data (DB/Redis)

---

## ⚠️ Notes

* Ensure Kafka is running before starting services
* Topic name must match in both producer & consumer
* DTO structure should be consistent across services

---

## 🙌 Author

**Nidhish Pokala**

---

## ⭐ If you like this project

Give it a ⭐ on GitHub and feel free to contribute!
