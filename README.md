# Brokerage Application

This project is a brokerage system that allows customer and brokers to manage and trade assets. It includes features such as order creation, asset management, order matching, and secure authentication.

## Features

- **Order Management**: Users can create buy and sell orders for various assets with real-time tracking and execution.
- **Order Matching**: Admins can manually match pending orders, updating the asset's size and usable size accordingly.
- **Asset Management**: Users can manage their portfolio of assets, including tracking available balances and asset values.
- **Order Cancellation**: Users can cancel orders, and refunds are processed automatically based on the order type (buy/sell).
- **Secure Authentication**: JWT-based authentication ensures secure user access and session management.
- **Payment and Withdrawal**: Users can deposit or withdraw funds, with validation for sufficient balances and secure payment processing.
- **Scalability**: Kafka is used for messaging and event-driven architecture, ensuring reliable communication between services.


## Prerequisites

To run this project, you will need to have the following installed:

- **Java 21**
- **Docker**

## Getting Started

Follow these steps to get the application up and running.

### 1. Clone the repository

```bash
git clone https://github.com/sarperkumcu/ing-hub-case-study.git
cd ing-hub-case-study
```

### 2. Run application

Ensure Kafka is running in port 29092.
Run the application:

```bash
./mvnw spring-boot:run
```


