# 🏨 Hostel Grievance Redressal & SLA Management System

> **Course:** CSE2001 - Object Oriented Programming with Java  
> **Degree:** B.Tech CSE AI & ML Engineering (2nd Year-3rd Semester)  
> **Institute:** VIT Bhopal University  
> **Project Type:** VITyarthi-Java Course Project


---

## 📌 Why Did We Build This? (The Problem)

Have you ever been in a college hostel?
- Your room's geyser fails during peak winters, or the ceiling fan regulator is set on maximum.
- In order to lodge your complaint, you need to go all the way down to the hostel gate/reception and enter it in the register.
- There is always a risk of tearing apart pages of the register.
- Sometimes it takes several days before wardens even notice those complaints.
- But in most cases, workers simply mark the tickets as "Done" in the book without actually resolving your issues in the room.
- And there is no way to escalate critical cases like bursting of water line or beeping of smoke detector.

To help resolve such a common issue faced by students in hostel life, we designed this **Hostel Grievance Redressal and SLA Management System** using **Core Java**. It helps in managing the entire process from filing the complaint, assigning it to respective hostel block warden, executing background SLA countdown timer for critical safety-related tickets, and ensuring that the issue gets fixed in the student room before closing the ticket with his/her confirmation and rating (1 to 5 stars).

---

## ✨ Features We Implemented

### 1. 🎓 Student Portal
- **Lodge Complaints:** Complaints can be lodged with full name, roll number, room number and contact number.
- **7 Categories:** `ELECTRICAL`, `WATER`, `FIRE_SAFETY`, `SECURITY`, `HYGIENE`, `DEPOSIT_DISPUTE`, and `MAINTENANCE`.
- **Emergency Marking:** If the category chosen by the student is `FIRE_SAFETY` or `SECURITY`, it gets flagged as critical and a 24-hour background SLA timer is started.
- **Check Status:** You can check status at any point by entering your Student Roll Number, Room Number, or Complaint ID.
- **Confirm & Rate:** Once the warden marks the job as `RESOLVED`, it needs to be verified by the student and given a **1-5 star rating**.
- **Reopen the Ticket:** If the fix is not satisfactory and re-occurs the next day, you can simply click to reopen the ticket.

### 2. 👨‍💼 Warden Portal
- **Block-wise Filtering:** Filter out complaints based on the specific hostel block (Block A, Block B, or Block C).
- **Auto-Routing:** As soon as the student registers a complaint, it is automatically routed to the concerned warden for the particular hostel block.
- **Update Complaint Status:** Update complaint status to `IN_PROGRESS` and write your notes about it (for example, *"Plumber has been sent with new valve"*).
- **Close the Ticket:** Once the problem is solved, update its status to `RESOLVED` along with relevant resolution remarks.

### 3. 🏛️ Admin Portal (Chief Warden / Dean)
- **Analytics Dashboard:** Get a glance over total complaints registered, active/resolved ticket counts, average student satisfaction rating and breakdown by categories.
- **SLA Breaches Report:** Direct access to all high-priority emergency tickets that breached SLA turnaround time.
- **Warden Workload:** View active workload of each warden and register new wardens if required.

### 4. 💾 Simple CSV File Storage (No DB Setup Needed)
- Since we did not want our friends/family/faculty to struggle with MySQL / XAMPP setup and login credentials, we have used simple **flat CSV file** for storing all complaints and other information. It gets stored in the `data/complaints.csv` file.
- When the program starts, it reads the file contents and loads all existing data automatically.
- All changes to the data are safely saved once the menu is exited.

### 5. 🧪 In-Built Automated Test Suite
- We developed **GrievanceSystemTest** class with **34 test cases** testing complaint creation, auto-routing, status updates, exceptions, threading, and file persistence.

---

## 📚 OOPs Concepts We Applied (For Viva / Syllabus Reference)

Since this project has been developed as part of our **CSE2001 Object Oriented Programming with Java** course, we directly related our codebase with the OOP concepts:

| OOP Concept | Implementation in the Code |
| :--- | :--- |
| **Classes & Objects** | Real world entities implemented as classes: `Student`, `Warden`, `Admin`, `Complaint`. |
| **Encapsulation** | All entity properties are encapsulated with getter/setter methods and appropriate validations. |
| **Inheritance** | `EscalationMonitor` class extends built-in `Thread` class to provide functionality of background SLA monitor; custom exceptions extend built-in `Exception`. |
| **Polymorphism** | Overloaded methods are used for complaint search/filter operations and constructors. |
| **Multithreading** | Background daemon `EscalationMonitor` thread executes a countdown timer for emergency tickets while not blocking input in the console. |
| **Exception Handling** | Three custom checked exceptions: `ComplaintNotFoundException`, `ComplaintAlreadyResolvedException` and `InvalidCategoryException`. |
| **Collections Framework** | Utilized `ConcurrentHashMap`, `ArrayList`, and `List` for in-memory tickets storage and thread-safety. |
| **File I/O** | `java.nio.file.Files`, `BufferedReader` and `BufferedWriter` classes are utilized for reading/writing complaint records in CSV format. |

---

## 📁 Project Structure

```
GrievanceSystem_testing/
├── README.md                          # This file
├── statement.md                       # Problem statement & scope
├── PROJECT_REPORT.md                  # Full academic project report
├── data/
│   └── complaints.csv                # CSV file where complaints are saved
└── GrievanceSystem/
    └── src/
        └── grievance/
            ├── Main.java             # Main console application (CLI menu)
            ├── model/
            │   ├── Admin.java       # Chief admin / dean model
            │   ├── Complaint.java   # Complaint entity & card formatter
            │   ├── ComplaintCategory.java   # Enum: ELECTRICAL, WATER, etc.
            │   ├── ComplaintStatus.java     # Enum: SUBMITTED, RESOLVED, etc.
            │   ├── Student.java     # Student entity & block detection
            │   └── Warden.java     # Warden entity & assigned block
            ├── service/
            │   ├── GrievanceSystem.java     # Core system logic & auto assignment
            │   ├── EscalationMonitor.java   # Background thread for 24h SLA alarms
            │   └── StorageService.java      # Reads & writes CSV data
            ├── exception/
            │   ├── ComplaintAlreadyResolvedException.java
            │   ├── ComplaintNotFoundException.java
            │   └── InvalidCategoryException.java
            └── test/
                └── GrievanceSystemTest.java  # 34 automated test cases
```

---

## 🚀 Running the Project

### Prerequisites
- Make sure you have **Java (JDK 11 or newer)** installed on your machine.
- Check the version in terminal:
  ```bash
  javac -version
  java -version
  ```

---

### Approach A: With VS Code or IntelliJ IDEA (Recommended)

1. Open the `GrievanceSystem_testing` folder in **VS Code** or **IntelliJ IDEA**.
2. Navigate to `GrievanceSystem/src/grievance/Main.java`.
3. Click the **Run** ▶️ button (or press `Shift + F10` in IntelliJ / click `Run Java` above `main` in VS Code).
4. Console menu appears in the built-in terminal window!

---

### Approach B: Using Terminal / Command Prompt

#### Windows (PowerShell or CMD)
1. Open terminal in `GrievanceSystem_testing` folder:
   ```powershell
   cd GrievanceSystem_testing
   ```
2. Compile all Java files into `bin` folder:
   ```powershell
   javac -d bin GrievanceSystem/src/grievance/*.java GrievanceSystem/src/grievance/model/*.java GrievanceSystem/src/grievance/service/*.java GrievanceSystem/src/grievance/exception/*.java GrievanceSystem/src/grievance/test/*.java
   ```
3. Start the program:
   ```powershell
   java -cp bin grievance.Main
   ```
4. *(Optional)* Start the automated test suite:
   ```powershell
   java -cp bin grievance.test.GrievanceSystemTest
   ```

## 🔑 Demo Credentials (Quick Test)

In order to allow faculty or friends to test the system quickly without having to register from scratch, we preloaded some demo data.

### Pre-registered Wardens
- `W1` - **Mr. Verma** (Block 1) | Phone: `+91-98765-00001`
- `W2` - **Mrs. Rao** (Block 5) | Phone: `+91-98765-00002`
- `W3` - **Mr. Khan** (Block 3) | Phone: `+91-98765-00003`

### Pre-registered Students
- `S101` - **Aarav Patel** (Room C-204, Block C)
- `S102` - **Priya Nair** (Room A-108, Block A)
- `S103` - **Karan Singh** (Room B-312, Block B)

### Chief Administrator
- `A1` - **Dr. AnantKant Shukla** (Dean of Student Welfare)

---

## 🖥️ Example Output Screenshots:

### Menu:
**For Option 1:**


<img width="797" height="295" alt="output1" src="https://github.com/user-attachments/assets/a4230c1e-8823-4161-a5a7-109a41ece696" />



**For Option 2:**


<img width="980" height="487" alt="output2" src="https://github.com/user-attachments/assets/5442fe7c-cdc0-4b8f-9207-ba6d0184e5fb" />



**For Option 3:**

<img width="890" height="507" alt="output3" src="https://github.com/user-attachments/assets/7d5e63a5-8ad9-4dd1-9c79-cda2b9127c78" />




## 🔮 Future Improvements / Next Steps

Since this project was developed as a 2nd year mini-project during our semester, there are some improvements we plan to implement next:
- [ ] **Desktop GUI:** Design a JavaFX or Swing desktop front-end (current is console).
- [ ] **Database:** Connect to MySQL or PostgreSQL database using JDBC instead of CSV file.
- [ ] **Instant Notification:** Integrate Twilio or JavaMail API to send SMS alerts to wardens when emergency tickets are filed.
- [ ] **Mobile Web App:** Convert back-end to RESTful Spring Boot API with React/Flutter front-end.

---

## 👥 Contributors

- **UJJAVAL GUPTA** - *2nd Year B.Tech CSE, VIT Bhopal University* - Reg No: `25BAI11102`

### Acknowledgments
Big thank you to our Java instructor DR. Sanat Jain Sir  for teaching Java OOP concepts and conducting lab work during this semester!
