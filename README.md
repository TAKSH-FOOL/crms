# 🚔 Crime Records Management System (CRMS)

A robust console-based Crime Records Management System built with **Core Java** and **MySQL**. Designed for police stations to manage FIRs, criminals, evidence, and investigations with role-based access control.

## ✨ Features

- **Role-Based Access**: Separate menus for **Admin**, **Officer**, and **Staff**.
- **FIR Management**: Register, assign, update status, and search FIRs (with BST indexing for speed).
- **Crime Tracking**: Link criminals to crimes, manage evidence (with image upload), and track investigation progress.
- **Victim & Witness Management**: Add and update victim/witness details linked to specific FIRs.
- **Reporting**: Generate FIR summaries, officer performance, and station-wise reports.
- **Audit Logging**: Automatic tracking of every insert/update/delete action.
- **Data Isolation**: Users only see data belonging to their assigned police station.

## 🛠️ Tech Stack

- **Language**: Java (JDK 8+)
- **Database**: MySQL (MariaDB)
- **Connectivity**: JDBC
- **Data Structures**: Custom Binary Search Trees (BST) for optimized searching
- **Design Pattern**: DAO (Data Access Object) Pattern

## 📂 Database Schema

- `users` (Authentication & Roles)
- `police_stations`
- `admins` / `officers` / `staff`
- `fir` (First Information Reports)
- `criminals`, `crime_records`, `crime_criminal_mapping`
- `victims`, `witnesses`, `evidence`
- `investigations`
- `audit_logs`, `login_history`

> Triggers handle automatic audit logging, FIR number generation, and status synchronization between FIRs and Investigations.

## 🚀 Getting Started

### Prerequisites
- Java 8 or higher
- MySQL (or MariaDB) Server
- MySQL Connector/J (JDBC Driver)

### Installation

1. **Clone the repository**:
   ```bash
   git clone https://github.com/yourusername/crime-records-system.git