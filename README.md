# 🏨 Hostel Grievance Redressal & SLA Management System

**Course:** CSE2001 - Object Oriented Programming with Java
**Degree:** B.Tech CSE AI & ML Engineering (2nd Year - 3rd Semester)
**Institute:** VIT Bhopal University
**Project Type:** VITyarthi - Java Course Project

---

## 📌 About the Project

Hostel-related problems are something students face quite often. For example, a fan or geyser may stop working, there may be a water leakage, or a student may have a problem related to cleanliness or security.

In the traditional process, students usually have to visit the hostel reception or warden and enter their complaint in a register. This can make it difficult to track whether the complaint has actually been resolved or not.

To solve this problem, we developed the **Hostel Grievance Redressal & SLA Management System** using **Core Java**.

The system allows students to register complaints, wardens to manage and update them, and administrators to view overall complaint information. For critical categories such as fire safety and security, the system also starts a background SLA timer so that these complaints can be monitored separately.

The student also has to confirm that the issue has actually been fixed before the complaint can be considered completed. After that, the student can give a rating from 1 to 5 stars.

---

## ✨ Main Features

### 1. 🎓 Student Portal

* **Register a Complaint:** Students can submit a complaint by entering their name, roll number, room number and contact number.

* **Complaint Categories:** The system currently supports seven categories:
  `ELECTRICAL`, `WATER`, `FIRE_SAFETY`, `SECURITY`, `HYGIENE`, `DEPOSIT_DISPUTE`, and `MAINTENANCE`.

* **Emergency Complaints:** Complaints under `FIRE_SAFETY` and `SECURITY` are treated as critical complaints. A 24-hour SLA timer is started for these tickets.

* **Check Complaint Status:** Students can search for their complaint using their Student Roll Number, Room Number or Complaint ID.

* **Confirm and Rate:** After a warden marks a complaint as `RESOLVED`, the student can confirm whether the issue was actually fixed and provide a rating from **1 to 5 stars**.

* **Reopen Complaint:** If the problem is not properly fixed or happens again, the student can reopen the complaint.

---

### 2. 👨‍💼 Warden Portal

* **Block-wise Complaints:** Wardens can view complaints related to their assigned hostel block.

* **Automatic Assignment:** When a student submits a complaint, the system automatically assigns it to the warden responsible for that hostel block.

* **Update Status:** Wardens can change a complaint to `IN_PROGRESS` and add notes about the work being done.

* **Resolve Complaint:** After the issue has been fixed, the warden can mark the complaint as `RESOLVED` and add resolution details.

---

### 3. 🏛️ Admin Portal

The Admin/Chief Warden section provides an overall view of the grievance system.

* **Analytics Dashboard:** Shows total complaints, active and resolved complaints, average student rating and category-wise complaint information.

* **SLA Breach Report:** Shows emergency complaints where the SLA time was exceeded.

* **Warden Workload:** Allows the administrator to check the active workload of wardens and add new wardens when required.

---

### 4. 💾 CSV File Storage

For this project, we decided to use a simple CSV file instead of setting up a separate database.

All complaint data is stored in:

`data/complaints.csv`

This makes the project easier to run and test without requiring MySQL, XAMPP or any additional database setup.

When the program starts, existing complaint data is loaded from the CSV file. Any changes made during the program execution are saved when the application exits.

---

### 5. 🧪 Automated Tests

We also created a `GrievanceSystemTest` class for testing the main parts of the application.

The test suite contains **34 test cases** covering areas such as:

* Complaint creation
* Automatic complaint assignment
* Status updates
* Exception handling
* Threading
* CSV file persistence

---

## 📚 OOP Concepts Used

Since this project was developed for the **CSE2001 Object Oriented Programming with Java** course, we have used several OOP concepts directly in the implementation.

| OOP Concept               | How We Used It                                                                                                           |
| ------------------------- | ------------------------------------------------------------------------------------------------------------------------ |
| **Classes & Objects**     | Classes such as `Student`, `Warden`, `Admin` and `Complaint` represent different entities in the system.                 |
| **Encapsulation**         | Entity properties are kept inside their respective classes and accessed using getter/setter methods with validations.    |
| **Inheritance**           | `EscalationMonitor` extends Java's `Thread` class. The custom exception classes also extend `Exception`.                 |
| **Polymorphism**          | Method overloading is used for different complaint search/filter operations and constructors.                            |
| **Multithreading**        | The `EscalationMonitor` runs as a background thread to handle the SLA countdown for emergency complaints.                |
| **Exception Handling**    | Custom exceptions are used for cases such as a missing complaint, an already resolved complaint and an invalid category. |
| **Collections Framework** | `ConcurrentHashMap`, `ArrayList` and `List` are used for storing and managing complaint data in memory.                  |
| **File I/O**              | `Files`, `BufferedReader` and `BufferedWriter` from Java are used for reading and writing the CSV file.                  |

---

## 📁 Project Structure

```text
GrievanceSystem_testing/

├── README.md
├── statement.md
├── PROJECT_REPORT.md
│
├── data/
│   └── complaints.csv
│
└── GrievanceSystem/
    └── src/
        └── grievance/
            ├── Main.java
            │
            ├── model/
            │   ├── Admin.java
            │   ├── Complaint.java
            │   ├── ComplaintCategory.java
            │   ├── ComplaintStatus.java
            │   ├── Student.java
            │   └── Warden.java
            │
            ├── service/
            │   ├── GrievanceSystem.java
            │   ├── EscalationMonitor.java
            │   └── StorageService.java
            │
            ├── exception/
            │   ├── ComplaintAlreadyResolvedException.java
            │   ├── ComplaintNotFoundException.java
            │   └── InvalidCategoryException.java
            │
            └── test/
                └── GrievanceSystemTest.java
```

---

## 🚀 How to Run the Project

### Prerequisites

You need **Java JDK 11 or newer** installed on your system.

You can check your Java installation using:

```bash
javac -version
java -version
```

---

### Approach A: Using VS Code or IntelliJ IDEA

1. Open the `GrievanceSystem_testing` folder in **VS Code** or **IntelliJ IDEA**.

2. Open:

```text
GrievanceSystem/src/grievance/Main.java
```

3. Run the `Main.java` file using the **Run** option.

4. The application menu will appear in the terminal.

---

### Approach B: Using Terminal / Command Prompt

#### Windows PowerShell / CMD

First, open the terminal inside the project folder:

```powershell
cd GrievanceSystem_testing
```

Compile the Java files:

```powershell
javac -d bin GrievanceSystem/src/grievance/*.java GrievanceSystem/src/grievance/model/*.java GrievanceSystem/src/grievance/service/*.java GrievanceSystem/src/grievance/exception/*.java GrievanceSystem/src/grievance/test/*.java
```

Run the application:

```powershell
java -cp bin grievance.Main
```

To run the automated tests:

```powershell
java -cp bin grievance.test.GrievanceSystemTest
```

---

## 🔑 Demo Data

Some sample users are already available so that the project can be tested without creating everything from the beginning.

### Pre-registered Wardens

* `W1` - **Mr. Verma** (Block 1)
  Phone: `+91-98765-00001`

* `W2` - **Mrs. Rao** (Block 5)
  Phone: `+91-98765-00002`

* `W3` - **Mr. Khan** (Block 3)
  Phone: `+91-98765-00003`

### Pre-registered Students

* `S101` - **Aarav Patel** (Room C-204, Block C)
* `S102` - **Priya Nair** (Room A-108, Block A)
* `S103` - **Karan Singh** (Room B-312, Block B)

### Chief Administrator

* `A1` - **Dr. AnantKant Shukla** (Dean of Student Welfare)

---

## 🖥️ Example Output

### Main Menu

```text
================================================================================
          VIT HOSTEL GRIEVANCE REDRESSAL & SLA MANAGEMENT SYSTEM
================================================================================

  1. Student Portal  - Lodge Complaints, Track Status, Confirm & Rate
  2. Warden Portal   - View Assigned Tickets, Update Progress, Resolve
  3. Admin Portal    - Analytics Dashboard, SLA Breaches, Wardens
  4. View All Complaints (Global Explorer & Filters)
  5. Run Automated Self-Diagnostics & Tests
  6. Exit & Save

--------------------------------------------------------------------------------
Enter your choice (1-6):
```

### Complaint Record Example

```text
+--------------------------------------------------------------------------------+
| Complaint ID   : CMP1002                                                      |
| Status         : CONFIRMED                                                    |
| Category       : WATER                                                        |
| Description    : Low water pressure and leak in bathroom tap                  |
| Student        : Priya Nair (ID: S102, Room: A-108, Block: Block A)            |
| Contact        : 9822233344                                                   |
| Assigned Warden: Mrs. Rao (Block A)                                           |
| Submitted At   : 2026-09-12T22:00:49.339                                     |
| Resolved At    : 2026-09-12T22:00:49.341                                     |
| Resolution     : Replaced washer in tap fixture. Water pressure restored.     |
| Rating         : ★★★★★ (5/5)                                                  |
| Feedback       : Fixed very promptly. Tap works great now!                    |
+--------------------------------------------------------------------------------+
```

---

## 🔮 Future Improvements

This is a 2nd-year semester project, so there are still several things that can be added in future versions.

* [ ] **Desktop GUI:** Create a JavaFX or Swing interface instead of using only the console.

* [ ] **Database Support:** Replace the CSV storage with MySQL or PostgreSQL using JDBC.

* [ ] **Notifications:** Add SMS or email notifications for emergency complaints using services such as Twilio or JavaMail.

* [ ] **Web/Mobile Version:** Convert the backend into a REST API using Spring Boot and create a frontend using React or Flutter.

---

## 👥 Contributor

**UJJAVAL GUPTA**
2nd Year B.Tech CSE, VIT Bhopal University
Reg No: `25BAI11102`

### Acknowledgment

I would like to thank our Java instructor **Dr. Sanat Jain Sir** for teaching us Java OOP concepts and guiding us through the lab work during the semester.
