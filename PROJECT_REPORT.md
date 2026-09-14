# VIT BHOPAL UNIVERSITY
## SCHOOL OF COMPUTING SCIENCE AND ENGINEERING

---

# A Project Report on
# AUTOMATED HOSTEL GRIEVANCE REDRESSAL AND REAL-TIME SLA MANAGEMENT SYSTEM

**Course:** Object Oriented Programming with Java (CSE2001)  
**Branch:** B.Tech Computer Science and Engineering (Specialization in AI & ML)  
**Academic Term:** Fall Semester 2026 (3rd Semester)  
**Institution:** VIT Bhopal University, Bhopal-Indore Highway, Kothrikalan, Sehore, Madhya Pradesh – 466114  

---

## Candidate Submission Information

| Parameter | Particulars |
| :--- | :--- |
| **Project Title** | Automated Hostel Grievance Redressal and Real-Time SLA Management System |
| **Course Code & Name** | CSE2001 - Object Oriented Programming with Java |
| **Student Name** | Ujjaval Gupta |
| **Registration Number** | 25BAI11102 |
| **Faculty Supervisor** | Prof. Dr. Sanat Jain, SCSE, VIT Bhopal University |
| **development Core** | Java Standard Edition (JDK 17 / JDK 21 / JDK 26) |
| **Data Persistence Engine** | Custom Flat-File Storage Engine (`data/complaints.csv`) |
| **Concurrency Layer** | Java Concurrency Utilities (`ConcurrentHashMap`, `AtomicInteger`, Daemon Threads) |
| **Verification Suite** | Built-in 34-Assertion Automated Test Harness (100% Pass Rate) |

---


## ABSTRACT

At VIT Bhopal University, many students stay in different hostel blocks. Because so many students use the hostel facilities every day, maintenance problems are common. Problems such as geyser issues, low water pressure, sparking sockets, jammed doors, and broken LAN ports can happen at any time. At present, many complaints are recorded in handwritten registers kept at the security or reception desk. This manual method has several problems. Registers can be lost or damaged, students cannot easily check the status of a complaint, and a complaint may sometimes be marked as completed before the problem is actually fixed. Emergency issues also do not get any automatic priority.

To resolve these critical operational deficiencies, we designed and developed the **Automated Hostel Grievance Redressal and Real-Time SLA Management System** entirely in Core Java (JDK 17/21/26). The project follows OOP concepts and does not require heavy external libraries, the system automates the complaint lifecycle across three dedicated portals: **Student**, **Block Warden**, and **Chief Administrator**.

### Main Technical Features
1. **Room Parsing and Warden Load Balancing:** The system reads block identities from unstructured room strings (e.g., `C-204`, `Block A 108`) and assigns complaints to the block warden with the lowest number of active complaints.
2. **Background SLA Monitoring:** For safety-critical hazards (`FIRE_SAFETY` and `SECURITY`), an Java background daemon thread (`EscalationMonitor`) checks the 24-hour SLA period, automatically sending overdue issues to the Chief Warden if unresolved, or stopping the thread when the issue is resolved early.
3. **Closed-Loop Student Verification:** Wardens cannot fully close a ticket on their own; resolved complaints require student confirmation with a 1-to-5 star evaluation and feedback, and the complaint can be reopened if faults persist.
4. **Zero-Dependency RFC-4180 CSV Persistence:** Custom finite-state tokenizer keeps the complaint data after restarting the program, safely parsing commas inside quotes and multiline descriptions without needing a separate database setup.
5. **Automated 34-Point Verification Harness:** The built-in test program checks status changes, thread safety with 20 parallel threads, and custom exceptions and all tests pass.

---

## TABLE OF CONTENTS

- [Chapter 1: Introduction & Problem Context](#chapter-1-introduction--problem-context)
  - [1.1 Background: Residential Life at VIT Bhopal](#11-background-residential-life-at-vit-bhopal-university)
  - [1.2 The Traditional Paper Register Workflow](#12-the-traditional-paper-register-workflow)
  - [1.3 Core Deficiencies of the Manual Process](#13-core-deficiencies-of-the-manual-process)
  - [1.4 Project Objectives](#14-project-objectives)
  - [1.5 System Scope](#15-system-scope)
- [Chapter 2: Literature Review & Comparative Analysis](#chapter-2-literature-review--comparative-analysis)
  - [2.1 Existing Campus Complaint Systems](#21-existing-campus-complaint-systems)
  - [2.2 Common Problems in Existing Systems](#22-critical-gaps-in-contemporary-implementations)
  - [2.3 Comparative Feature Matrix](#23-comparative-feature-matrix)
- [Chapter 3: System Requirements & Technical details](#chapter-3-system-requirements--technical-details)
  - [3.1 Users and Their Permissions](#31-user-personas--permissions)
  - [3.2 System Requirements](#32-operating-environment--prerequisites)
  - [3.3 Functional Requirements (FR-01 to FR-12)](#33-functional-requirements-details-fr-01-to-fr-12)
  - [3.4 Non-Functional Requirements](#34-non-functional-requirements-details)
- [Chapter 4: System design & Structural Design](#chapter-4-system-design--structural-design)
  - [4.1 System design](#41-layered-modular-design)
  - [4.2 Codebase Directory Hierarchy](#42-codebase-directory-hierarchy)
  - [4.3 Structural design Diagram](#43-structural-design-diagram)
- [Chapter 5: Design Diagrams & System Modeling](#chapter-5-design-diagrams--system-modeling)
  - [5.1 Use Case Diagram](#51-use-case-diagram)
  - [5.2 Workflow Diagram (State Transition Machine)](#52-workflow-diagram-grievance-state-transition-machine)
  - [5.3 Sequence Diagram (Critical Grievances & SLA Lifecycle)](#53-sequence-diagram-for-safety-critical-grievances)
  - [5.4 Class & Component design Diagram](#54-class--component-design-diagram)
  - [5.5 Entity-Relationship (ER) Diagram & Storage Schema](#55-entity-relationship-er-diagram--storage-schema)
- [Chapter 6: Design Decisions and Reasons](#chapter-6-design-decisions--architectural-rationale)
- [Chapter 7: Object-Oriented development & Core Mechanics](#chapter-7-object-oriented-development--core-mechanics)
  - [7.1 Rigorous Application of the Four OOP Pillars](#71-rigorous-application-of-the-four-oop-pillars)
  - [7.2 Multi-Threading & Asynchronous SLA Management](#72-multi-threading--asynchronous-sla-management)
  - [7.3 Thread-Safe Concurrency design](#73-thread-safe-concurrency-design)
  - [7.4 Checked Exception Hierarchy](#74-checked-exception-hierarchy)
  - [7.5 RFC-4180 CSV Storage Engine](#75-rfc-4180-csv-storage-engine)
  - [7.6 Heuristic Room Parsing & Stream-Based Load Balancing](#76-heuristic-room-parsing--stream-based-load-balancing)
- [Chapter 8: Screenshots, Terminal Transcripts & Results](#chapter-8-screenshots-terminal-transcripts--results)
- [Chapter 9: Testing Approach & Empirical Verification](#chapter-9-testing-approach--empirical-verification)
  - [9.1 Testing method](#91-testing-method)
  - [9.2 34-Assertion Verification Matrix](#92-34-assertion-verification-matrix)
  - [9.3 Concurrency & Stress Testing](#93-concurrency--stress-testing)
  - [9.4 Automated Test Suite Execution Transcript](#94-automated-test-suite-execution-transcript)
- [Chapter 10: Problems Faced During Development and Their Solutions](#chapter-10-practical-development-hurdles--solutions)
- [Chapter 11: What We Learned](#chapter-11-learnings--key-takeaways)
- [Chapter 12: Limitations & Future Enhancements](#chapter-12-limitations--future-enhancements)
- [Chapter 13: Conclusion](#chapter-13-conclusion)
- [References](#references)

---

# CHAPTER 1: INTRODUCTION & PROBLEM CONTEXT

### 1.1 Background: Residential Life at VIT Bhopal University
Residential campus living is an important part of student life at **VIT Bhopal University**. With many engineering students living in multi-story hostel complexes—including Boys Hostel Blocks (Block 1, Block 2, Block 3) and Girls Hostel Blocks—the hostel facilities are used continuously throughout the semester. Facilities such as high-capacity geysers, washroom plumbing fixtures, overhead ceiling fans, safety window latches, LAN ports, and electrical socket clusters are used throughout the semester.

Because these facilities are used so often, breakdowns can happen. A hot water geyser coil fails during the winter exam rush; an electrical socket produces sparks when an engineering workstation is plugged in; a washroom flush valve gets stuck, flooding a corridor; or a room latch malfunctions, locking students out shortly before morning lectures. How quickly and properly these maintenance issues are handled has a direct effect on student safety, academic focus, and overall quality of residential life.

### 1.2 The Traditional Paper Register Workflow
In traditional hostel administrative setups, maintenance requests are recorded manually. The usual process has five main steps:
1. **Going to the Office:** When an issue arises, the student must walk down multiple flights of stairs to the hostel security desk or the warden's office.
2. **Writing the Complaint:** The security guard hands over a thick paper notebook. The resident scribbles their room number, date, phone number, and a brief description across a single narrow line.
3. **Warden Checks the Register:** Sometime during the morning, the estate supervisor or block warden flips through pages, noting room numbers onto loose paper slips to distribute to visiting technicians (electricians, plumbers, carpenters).
4. **Technician Assignment:** Technicians receive verbal instructions and visit wings as time permits, without structured prioritisation.
5. **Manual Closure:** Technicians verbally inform the supervisor upon completing tasks, who then draws a rough pen stroke across the register row.

### 1.3 Core Deficiencies of the Manual Process
Although this process is simple, it has some common problems:
* **Lost or Damaged Registers:** Paper registers get misplaced, pages get torn or soiled from rain and beverage spills near entry desks, and hurried handwriting is frequently illegible, leading to missed repair visits.
* **No Real-Time Status:** Once logged, students have no visibility into ticket status. Is a technician assigned? Are parts ordered? Will the repair occur today or next week? Students are forced into repeated, stressful visits to security guards.
* **The "Fake Closure" Loophole:** Maintenance personnel frequently strike through log entries to fulfill daily closure quotas without completing physical repairs. When students return from classes to find fixtures still malfunctioning, they must restart the entire manual process from scratch.
* **Emergency Complaints Are Not Prioritized:** Safety-critical emergencies (e.g., beeping smoke detectors, burnt socket clusters, broken ground-floor locks) are logged on the exact same page as a creaking cupboard door. There is no automated mechanism to flag hazardous emergencies or escalate unattended safety issues to senior management.

### 1.4 Project Objectives
The main aim of this project was to build a Java console application that makes the complaint process easier to manage instead of using paper registers:
1. **Automated Ticket Routing:** Read different room number formats (e.g., `C-204`, `Block A 108`) and assign grievances to block wardens based on active workloads.
2. **Automatic SLA Checking:** Deploy background daemon threads to monitor critical emergency tickets and auto-escalate breached issues to senior administration if unaddressed within 24 hours.
3. **Student Verification:** Prevent unilateral ticket closures by requiring residents to confirm repairs with a 1-to-5 star rating and feedback, backed by a reopening mechanism if issues persist.
4. **CSV File Storage:** Serialize system state to an RFC-4180 compliant CSV repository, making sure zero database installation friction during academic evaluation.
5. **Thread Safety:** Guarantee thread safety using atomic counters and concurrent collections, supporting concurrent submissions without race conditions or identifier collisions.

### 1.5 System Scope
Developed as part of the CSE2001 (Object Oriented Programming with Java) curriculum, the software is an interactive console application built using standard Java (JDK 17+). It models three operational roles—**Student**, **Block Warden**, and **Chief Administrator**—and includes a built-in 34-assertion automated test harness.

---

# CHAPTER 2: LITERATURE REVIEW & COMPARATIVE ANALYSIS

### 2.1 Existing Campus Complaint Systems
College complaint systems usually follow a few common approaches:
1. **Physical Paper Logbooks:** This is the traditional approach. Requires zero technical infrastructure, but provides zero accountability, no analytical metrics, and no emergency response guarantees.
2. **Enterprise ERP Modules (e.g., SAP, Oracle PeopleSoft):** Large institutional platforms that include generic helpdesk modules. These systems are often heavyweight, complex to navigate on mobile devices, and decoupled from block-specific hostel dynamics. They typically treat all tickets uniformly and lack resident verification loops.
3. **Commercial Cloud Helpdesks (e.g., Zendesk, Freshdesk):** These platforms provide many features that require active cloud subscriptions, continuous internet access, complex administrative configuration, and external database servers, making them impractical for local laboratory demonstrations and offline deployment.

### 2.2 Common Problems in Existing Systems
* **No Automatic SLA Timer:** Standard portals keep tickets open until an administrator manually updates a dropdown. They lack autonomous background threads that count down and force escalations when safety thresholds are breached.
* **Technician Can Close a Ticket Without Student Confirmation:** In most systems, once a technician marks a ticket "Resolved", it is permanently closed without student verification.
* **Setup Problems:** Academic student projects frequently depend on MySQL or PostgreSQL. During faculty demonstrations, these often fail due to connection timeouts, incorrect passwords, missing schemas, or incompatible JDBC driver paths.

### 2.3 Comparative Feature Matrix

| Feature Parameter | Paper Register Books | Generic ERP Portals | Our Core Java System |
| :--- | :--- | :--- | :--- |
| **Submission Channel** | Physical guard desk only | Web portal login | **Interactive CLI with Block Parsing** |
| **ID Uniqueness** | None (handwritten dates) | Database Auto-Increment | **Thread-Safe CAS `AtomicInteger`** |
| **Warden Allocation** | Manual guesswork | Static single queue | **Stream-Based Least Workload Routing** |
| **Emergency Prioritisation** | None (identical rows) | Static dropdown label | **Autonomous 24h SLA Daemon Watcher** |
| **Auto-Escalation Engine** | Impossible | Batch cron job scripts | **Real-Time Java Background Thread** |
| **Resident Verification** | None | Rare / Optional | **Enforced 1-5★ Rating or Reopening** |
| **Runtime Portability** | N/A | High setup overhead | **100% Pure Java & Flat CSV (Zero Setup)** |
| **Built-in Verification** | None | Manual QA | **Built-in 34-Assertion Test Engine** |

---

# CHAPTER 3: SYSTEM REQUIREMENTS & TECHNICAL details

### 3.1 Users and Their Permissions
* **Hostel Resident (Student):** Can lodge grievances across 7 categories, view real-time ticket status, inspect assigned warden details, provide 1-to-5 star ratings with feedback, or reopen incomplete repairs.
* **Block Warden:** Authenticates by block (e.g., Block A, Block B, Block C), reviews assigned tickets, logs progress updates, and submits formal resolution notes.
* **Chief Administrator (Dean / Chief Warden):** Has system-wide visibility. Monitors campus analytics (active vs resolved counts, average satisfaction rating, category distributions), tracks escalated SLA breaches, inspects warden workloads, and registers new wardens.

### 3.2 System Requirements
* **Operating System:** Platform-independent (Windows 10/11, macOS, Ubuntu/Debian Linux).
* **Java Runtime Environment:** Standard Java Development Kit (JDK 17, JDK 21, JDK 26).
* **Hardware Requirements:** Minimum 512 MB available RAM, 50 MB disk space.
* **External Dependencies:** Absolutely none. Built purely on the standard Java Class Library (`java.util`, `java.time`, `java.io`, `java.util.concurrent`).

### 3.3 Functional Requirements (FR-01 to FR-12)

| ID | Feature Name | Operational Specification & Business Rules | Associated Actor |
| :--- | :--- | :--- | :--- |
| **FR-01** | **Room Block Parsing** | Inspects student room string inputs (e.g., `C-204`, `Block A 108`) and normalizes them into standard block designations. | Student / System |
| **FR-02** | **Domain Categorization** | Categorizes grievances into 7 operational domains (`ELECTRICAL`, `WATER`, `FIRE_SAFETY`, `SECURITY`, `HYGIENE`, `DEPOSIT_DISPUTE`, `MAINTENANCE`). | Student |
| **FR-03** | **Critical SLA Flagging** | Automatically classifies `FIRE_SAFETY` and `SECURITY` issues as safety-critical with a strict 24-hour SLA window. | System |
| **FR-04** | **Asynchronous SLA Daemon** | Spawns a dedicated Java daemon thread (`EscalationMonitor`) to track SLA deadlines in the background. | System |
| **FR-05** | **Autonomous Escalation** | Auto-escalates tickets to the Chief Administrator if unresolved upon SLA timer expiration. | System / Admin |
| **FR-06** | **Least-Workload Routing** | Assigns tickets to the block warden with the lowest count of active, unresolved grievances. | System / Warden |
| **FR-07** | **Warden Progress Logging** | Records maintenance progress remarks and transitions ticket state from `ASSIGNED` to `IN_PROGRESS`. | Block Warden |
| **FR-08** | **Warden Resolution** | Records repair remarks, captures timestamps, and transitions state to `RESOLVED`. | Block Warden |
| **FR-09** | **Daemon Interruption** | Safely interrupts and shuts down the SLA monitor thread upon early ticket resolution. | System |
| **FR-10** | **Resident Verification** | Requires students to verify completed repairs with a 1-5 star rating and feedback, transitioning state to `CONFIRMED`. | Student |
| **FR-11** | **Grievance Reopening** | Allows students to reopen incomplete repairs with mandatory justification, resetting state to `IN_PROGRESS`. | Student |
| **FR-12** | **Analytics Dashboard** | Computes and displays campus metrics: active vs resolved counts, average ratings, category distributions, and SLA breaches. | Chief Admin |

### 3.4 Non-Functional Requirements
* **Thread Safety & Race Condition Prevention:** The system maintains state integrity when handling concurrent submissions using `AtomicInteger` for collision-free IDs and `ConcurrentHashMap` for safe thread access.
* **Persistence & Crash Resilience:** Every state change immediately flushes to disk via `StorageService`, making sure full state recovery upon restart.
* **Graceful Thread Termination:** SLA monitor threads are marked as daemon threads, making sure they never prevent clean JVM shutdown when exiting the console application.
* **Defensive Exception design:** Invalid inputs or illegal state transitions throw custom checked exceptions, preventing unhandled runtime crashes.
* **Performance & Scalability:** Grievance creation, routing heuristics, and disk flushing execute with sub-millisecond latency.

---

# CHAPTER 4: SYSTEM design & STRUCTURAL DESIGN

### 4.1 System design
The software is divided into four layers so that each part has a clear responsibility:
1. **Presentation Layer:** Handles user interaction via an interactive command-line interface (`Main.java`) and provides an automated test execution harness (`GrievanceSystemTest.java`).
2. **Business Logic & Concurrency Layer:** This is the main part of the application. `GrievanceSystem.java` serves as the central facade and coordinator, managing thread-safe registries and load balancing. `EscalationMonitor.java` runs asynchronous background timers for SLA enforcement, and `StorageService.java` serializes state to disk.
3. **Domain Model & Checked Exceptions Layer:** Contains the main entities (`Complaint`, `Student`, `Warden`, `Admin`), type-safe enums (`ComplaintCategory`, `ComplaintStatus`), and custom checked exceptions.
4. **Flat-File Persistence Layer:** Handles file operations for `data/complaints.csv` using an RFC-4180 compliant tokenizer.

### 4.2 Codebase Directory Hierarchy
```
GrievanceSystem/
├── src/
│   └── grievance/
│       ├── Main.java                         [Interactive Console UI Entry Point]
│       ├── model/                            [Domain Entities & State Enums]
│       │   ├── Admin.java                    [Administrator entity]
│       │   ├── Complaint.java                [Core grievance state machine & mutators]
│       │   ├── ComplaintCategory.java        [Enum: ELECTRICAL, WATER, FIRE_SAFETY, etc.]
│       │   ├── ComplaintStatus.java          [Enum: SUBMITTED, IN_PROGRESS, RESOLVED, etc.]
│       │   ├── Student.java                  [Resident profile & block parsing heuristic]
│       │   └── Warden.java                   [Warden profile & block association]
│       ├── service/                          [Business Logic & Background Workers]
│       │   ├── GrievanceSystem.java          [Central facade & load balancing coordinator]
│       │   ├── EscalationMonitor.java        [Daemon thread for 24h SLA countdown & escalation]
│       │   └── StorageService.java           [RFC-4180 CSV serialization engine]
│       ├── exception/                        [Custom Application Checked Exceptions]
│       │   ├── ComplaintAlreadyResolvedException.java
│       │   ├── ComplaintNotFoundException.java
│       │   └── InvalidCategoryException.java
│       └── test/                             [Automated Verification Harness]
│           └── GrievanceSystemTest.java      [34-Point automated test suite]
└── data/
    └── complaints.csv                        [15-Field flat-file persistence repository]
```

### 4.3 Structural design Diagram
```mermaid
graph TD
    subgraph "1. Presentation Layer"
        CLI["Main.java (Interactive Console CLI)"]
        TestHarness["GrievanceSystemTest.java (34-Point Automated Suite)"]
    end

    subgraph "2. Business Logic & Concurrency Layer"
        GS["GrievanceSystem.java (Central Facade & Load Balancer)"]
        EM["EscalationMonitor.java (24h SLA Daemon Thread)"]
        SS["StorageService.java (RFC-4180 CSV Engine)"]
    end

    subgraph "3. Domain Model & Exceptions Layer"
        C["Complaint.java (Synchronized State Machine)"]
        S["Student.java (Room Block Heuristic)"]
        W["Warden.java (Block Association)"]
        A["Admin.java (Escalation Target)"]
        Enums["Enums (ComplaintCategory, ComplaintStatus)"]
        Ex["Checked Exceptions (NotFound, AlreadyResolved, InvalidCat)"]
    end

    subgraph "4. Persistence Layer"
        CSV[("data/complaints.csv (15-Field RFC-4180 Flat Storage)")]
    end

    CLI --> GS
    TestHarness --> GS
    GS --> C
    GS --> S
    GS --> W
    GS --> A
    GS --> EM
    GS --> SS
    EM -. Alters State on SLA Timeout .-> C
    SS <--> CSV
```

---

# CHAPTER 5: DESIGN DIAGRAMS & SYSTEM MODELING

### 5.1 Use Case Diagram
```mermaid
graph LR
    Student((Resident Student))
    Warden((Block Warden))
    Admin((Chief Admin))

    subgraph "Hostel Grievance Redressal & SLA Management System"
        UC1[UC1: Lodge Grievance]
        UC2[UC2: Track Ticket Status]
        UC3[UC3: Confirm & Rate 1-5 Stars]
        UC4[UC4: Reopen Incomplete Repair]
        UC5[UC5: View Assigned Workload]
        UC6[UC6: Log Progress Notes]
        UC7[UC7: Log Resolution Remarks]
        UC8[UC8: View Campus Analytics]
        UC9[UC9: Monitor SLA Breaches]
        UC10[UC10: Register Block Warden]
        UC11[UC11: Run Self-Diagnostics]
    end

    Student --> UC1
    Student --> UC2
    Student --> UC3
    Student --> UC4

    Warden --> UC5
    Warden --> UC6
    Warden --> UC7

    Admin --> UC8
    Admin --> UC9
    Admin --> UC10
    Admin --> UC11
```

### 5.2 Workflow Diagram (Grievance State Transition Machine)
```mermaid
stateDiagram-v2
    [*] --> SUBMITTED : Student submits grievance
    SUBMITTED --> ASSIGNED : pickWarden() min-workload routing
    ASSIGNED --> IN_PROGRESS : Warden logs maintenance progress
    ASSIGNED --> ESCALATED : 24h SLA timer expires without action
    IN_PROGRESS --> RESOLVED : Warden logs repair resolution
    IN_PROGRESS --> ESCALATED : 24h SLA timer expires during repair
    ESCALATED --> RESOLVED : Senior Admin intervention & resolution
    RESOLVED --> CONFIRMED : Student verifies repair & rates 1-5★
    RESOLVED --> REOPENED : Student rejects repair (issue persists)
    REOPENED --> IN_PROGRESS : Warden re-commences repair
    CONFIRMED --> [*] : Ticket permanently closed
```

### 5.3 Sequence Diagram for Safety-Critical Grievances
```mermaid
sequenceDiagram
    autonumber
    actor Student
    participant Main as Main (CLI)
    participant GS as GrievanceSystem
    participant C as Complaint
    participant EM as EscalationMonitor
    participant SS as StorageService
    actor Warden
    actor Admin

    Student->>Main: submitComplaint(FIRE_SAFETY, "C-204", "Smoke detector beeping")
    Main->>GS: submitComplaint(student, category, description)
    GS->>C: new Complaint("CMP1001", ...)
    GS->>GS: pickWarden(student) -> Assigns Mr. Verma (Block C)
    GS->>C: assignTo(Warden: Verma)
    GS->>EM: new EscalationMonitor(complaint, 24h).start() [Daemon]
    GS->>SS: saveComplaints()
    SS-->>GS: Flushed to data/complaints.csv
    GS-->>Main: Complaint CMP1001 created
    Main-->>Student: Display formatted confirmation card

    Note over EM: Sleeping in background for SLA duration (24 hours)...

    alt Case A: Warden resolves issue before SLA expiration
        Warden->>Main: resolveComplaint("CMP1001", "Sensor battery replaced")
        Main->>GS: resolveComplaint("CMP1001", notes)
        GS->>C: markResolved(notes)
        GS->>EM: cancelMonitor() [thread.interrupt() awakens sleep cleanly]
        GS->>SS: saveComplaints()
        Main-->>Student: Ticket marked RESOLVED
        Student->>Main: confirmComplaint("CMP1001", 5, "Works perfectly!")
        Main->>GS: confirmComplaint(...)
        GS->>C: confirmByStudent(5, feedback)
        GS->>SS: saveComplaints()
    else Case B: 24h SLA expires without resolution
        EM->>C: escalate() [Status becomes ESCALATED]
        EM->>Admin: [SYSTEM ALERT - SLA BREACH] Escalated to Chief Warden!
        EM->>SS: saveComplaints()
    end
```

### 5.4 Class & Component design Diagram
```mermaid
classDiagram
    class Main {
        +main(args: String[]) void
        -handleStudentPortal() void
        -handleWardenPortal() void
        -handleAdminPortal() void
        -handleDiagnostics() void
    }

    class GrievanceSystem {
        -complaints: ConcurrentHashMap~String, Complaint~
        -wardens: ConcurrentHashMap~String, Warden~
        -activeMonitors: ConcurrentHashMap~String, EscalationMonitor~
        -idCounter: AtomicInteger
        +submitComplaint(s, cat, desc, sla) Complaint
        +pickWarden(student) Warden
        +markInProgress(id, remarks) void
        +resolveComplaint(id, notes) void
        +confirmComplaint(id, rating, fb) void
        +reopenComplaint(id, reason) void
        +getAnalytics() Map~String, Object~
        +saveAll() void
        +loadAll() void
    }

    class Complaint {
        -id: String
        -submittedBy: Student
        -category: ComplaintCategory
        -description: String
        -status: ComplaintStatus
        -submittedAt: LocalDateTime
        -resolvedAt: LocalDateTime
        -assignedWarden: Warden
        -escalated: boolean
        -rating: int
        +assignTo(w: Warden) void
        +markInProgress(remarks: String) void
        +markResolved(notes: String) void
        +confirmByStudent(rating: int, fb: String) void
        +reopen(reason: String) void
        +escalate() void
        +isResolved() boolean
        +isEscalated() boolean
    }

    class Student {
        -id: String
        -name: String
        -roomNumber: String
        -contact: String
        +getBlock() String
        +getRoomNumber() String
    }

    class Warden {
        -id: String
        -name: String
        -assignedBlock: String
        -contact: String
        +getAssignedBlock() String
    }

    class Admin {
        -id: String
        -name: String
        -designation: String
        -contact: String
    }

    class EscalationMonitor {
        -complaint: Complaint
        -admin: Admin
        -slaMillis: long
        -cancelled: volatile boolean
        +run() void
        +cancelMonitor() void
    }

    class StorageService {
        -filePath: String
        +save(List~Complaint~) void
        +load() List~Complaint~
        -parseCsvLine(line: String) List~String~
        -escapeCsv(val: String) String
    }

    class ComplaintCategory {
        <<enumeration>>
        ELECTRICAL
        WATER
        FIRE_SAFETY
        SECURITY
        HYGIENE
        DEPOSIT_DISPUTE
        MAINTENANCE
        +isCritical() boolean
    }

    class ComplaintStatus {
        <<enumeration>>
        SUBMITTED
        ASSIGNED
        IN_PROGRESS
        RESOLVED
        CONFIRMED
        REOPENED
        ESCALATED
    }

    EscalationMonitor --|> Thread : extends
    Main ..> GrievanceSystem : uses
    GrievanceSystem --> Complaint : manages
    GrievanceSystem --> Warden : manages
    GrievanceSystem --> EscalationMonitor : spawns
    GrievanceSystem --> StorageService : delegates I/O
    Complaint --> Student : submittedBy
    Complaint --> Warden : assignedWarden
    Complaint --> ComplaintCategory : has
    Complaint --> ComplaintStatus : has
```

### 5.5 Entity-Relationship (ER) Diagram & Storage Schema
```mermaid
erDiagram
    STUDENT ||--o{ COMPLAINT : submits
    WARDEN ||--o{ COMPLAINT : handles
    ADMIN ||--o{ COMPLAINT : oversees

    STUDENT {
        string student_id PK
        string student_name
        string room_number
        string contact_number
        string block "derived via heuristic"
    }

    WARDEN {
        string warden_id PK
        string warden_name
        string assigned_block
        string contact_number
    }

    ADMIN {
        string admin_id PK
        string admin_name
        string designation
        string contact
    }

    COMPLAINT {
        string complaint_id PK
        string student_id FK
        string assigned_warden_id FK
        string category
        string description
        string status
        string submitted_at
        string resolved_at
        string resolution_notes
        int rating
        string student_feedback
        string reopen_reason
        boolean is_escalated
    }
```

#### Data Storage Schema details (`data/complaints.csv`)

| # | Field Name | Data Type | Description & Constraints |
| :-: | :--- | :--- | :--- |
| **1** | `complaint_id` | String (PK) | Sequential atomic identifier (e.g., `CMP1001`, `CMP1002`). |
| **2** | `student_id` | String (FK) | Unique identifier of submitting student (e.g., `S101`). |
| **3** | `student_name` | String | Full name of the resident student. |
| **4** | `room_number` | String | Raw room string input (e.g., `C-204`, `Block A 108`). |
| **5** | `contact_number` | String | 10-digit mobile phone contact. |
| **6** | `category` | Enum | Category enum (`ELECTRICAL`, `WATER`, `FIRE_SAFETY`, etc.). |
| **7** | `description` | String (Escaped) | Sanitized breakdown description (handles quotes and commas). |
| **8** | `status` | Enum | Current lifecycle status (`IN_PROGRESS`, `CONFIRMED`, etc.). |
| **9** | `assigned_warden_id` | String (FK) | Unique identifier of assigned block warden (e.g., `W103`). |
| **10** | `submitted_at` | ISO-8601 String | Timestamp of grievance registration. |
| **11** | `resolved_at` | ISO-8601 String | Timestamp of warden repair completion (or empty if pending). |
| **12** | `resolution_notes` | String (Escaped) | Maintenance remarks logged by technician/warden. |
| **13** | `rating` | Integer (1-5) | Evaluation score assigned by student (0 if unrated). |
| **14** | `student_feedback` | String (Escaped) | Post-repair evaluation notes logged by student. |
| **15** | `is_escalated` | Boolean | Flag indicating whether 24h SLA was breached (`true`/`false`). |

---

# CHAPTER 6: DESIGN DECISIONS & ARCHITECTURAL RATIONALE

### 6.1 Core Java Standard Edition vs Heavy Web Frameworks
* **Decision:** Build purely in standard Java SE (JDK 17+) without Spring Boot, JavaFX, or third-party web dependencies.
* **Reason:** University lab viva evaluations frequently fail when running heavy web frameworks due to dependency download timeouts, port conflicts, or missing servlet containers. Standard Java ensures instantaneous compilation and execution on any standard machine.

### 6.2 Custom RFC-4180 CSV Storage vs Relational Database Servers
* **Decision:** Implement a custom flat-file CSV persistence engine rather than MySQL or SQLite.
* **Reason:** RDBMS setups require configuring database services, managing JDBC JAR paths, and setting root credentials. By implementing an RFC-4180 compliant tokenizer in `StorageService.java`, the application achieves zero-configuration portability with complete persistence.

### 6.3 Asynchronous Daemon Threads vs Polling Timers
* **Decision:** Use dedicated Java daemon threads (`EscalationMonitor`) with `Thread.sleep()` and `interrupt()` rather than active timer loops or polling.
* **Reason:** Polling loops consume unnecessary CPU cycles. Spawning a sleeping thread consumes minimal memory and allows instantaneous, zero-latency cancellation via `interrupt()` when a warden completes repairs early. Setting `setDaemon(true)` prevents background threads from blocking JVM termination on application exit.

### 6.4 Lock-Free CAS Counters vs Synchronized Sequence Blocks
* **Decision:** Use `java.util.concurrent.atomic.AtomicInteger` for unique ticket identifier generation.
* **Reason:** `AtomicInteger.incrementAndGet()` relies on hardware-level Compare-And-Swap (CAS) instructions. This eliminates synchronized block overhead, guaranteeing unique sequential IDs even under heavy concurrent thread submissions.

### 6.5 Closed-Loop Resident Verification vs Unilateral Closure
* **Decision:** Require student confirmation with a 1-5 star rating before a ticket reaches the closed state.
* **Reason:** Unilateral closures by technicians are a leading complaint in campus hostel management. Enforcing a resident verification step eliminates the "fake closure" loophole and provides actionable satisfaction metrics for university management.

---

# CHAPTER 7: OBJECT-ORIENTED development & CORE MECHANICS

### 7.1 Rigorous Application of the Four OOP Pillars

#### 7.1.1 Encapsulation
In `Complaint.java`, all domain fields (`status`, `assignedWarden`, `resolvedAt`, `rating`, `feedbackComment`, `escalated`) are declared `private`. Outside classes cannot modify state directly. State transitions are strictly governed by synchronized business methods that enforce domain rules:
```java
public synchronized void confirmByStudent(int rating, String feedback) {
    if (this.status != ComplaintStatus.RESOLVED && this.status != ComplaintStatus.ESCALATED) {
        throw new IllegalStateException("Only resolved or escalated complaints can be confirmed.");
    }
    this.rating = Math.max(1, Math.min(5, rating));
    this.feedbackComment = (feedback != null) ? feedback.trim() : "";
    this.status = ComplaintStatus.CONFIRMED;
}
```

#### 7.1.2 Abstraction
The user interface (`Main.java`) never interacts directly with file streams or thread primitives. It communicates entirely through high-level business methods exposed by the `GrievanceSystem` facade: `submitComplaint()`, `markInProgress()`, `resolveComplaint()`, and `confirmComplaint()`.

#### 7.1.3 Inheritance
`EscalationMonitor` extends Java's standard `java.lang.Thread`, overriding `run()` to execute background timers asynchronously. Custom checked exceptions (`ComplaintNotFoundException`, `ComplaintAlreadyResolvedException`, `InvalidCategoryException`) inherit from `java.lang.Exception`.

#### 7.1.4 Polymorphism
Method overloading is implemented extensively in `GrievanceSystem.java` to decouple testing from production defaults:
```java
// Production method applying the default 24-hour SLA
public Complaint submitComplaint(Student s, ComplaintCategory cat, String desc) 
        throws InvalidCategoryException {
    return submitComplaint(s, cat, desc, DEFAULT_SLA_MILLIS);
}

// Overloaded method accepting custom SLA durations for rapid test verification
public Complaint submitComplaint(Student s, ComplaintCategory cat, String desc, long slaMillis) 
        throws InvalidCategoryException {
    /* Ticket creation, routing, and daemon dispatch */
}
```

### 7.2 Multi-Threading & Asynchronous SLA Management
Safety-critical grievances (`FIRE_SAFETY` and `SECURITY`) automatically spawn an asynchronous daemon thread upon submission:
```java
public class EscalationMonitor extends Thread {
    private final Complaint complaint;
    private final Admin admin;
    private final long slaMillis;
    private final Runnable onEscalateCallback;
    private volatile boolean cancelled = false;

    public EscalationMonitor(Complaint c, Admin a, long sla, Runnable callback) {
        super("EscalationMonitor-" + c.getId());
        this.complaint = c; this.admin = a;
        this.slaMillis = sla; this.onEscalateCallback = callback;
        setDaemon(true); // Prevents background thread from blocking JVM shutdown
    }

    public void cancelMonitor() {
        this.cancelled = true;
        this.interrupt(); // Instantly awakens thread from sleep
    }

    @Override
    public void run() {
        try {
            Thread.sleep(slaMillis);
        } catch (InterruptedException e) {
            return; // Early resolution interrupted sleep; exit cleanly
        }
        if (cancelled) return;
        synchronized (complaint) {
            if (!complaint.isResolved()) {
                complaint.escalate();
                System.out.println("\n[SYSTEM ALERT - SLA BREACH] " + complaint.getId() +
                    " unresolved after " + (slaMillis / 1000) + "s! Escalated to: " +
                    admin.getName() + " (" + admin.getDesignation() + ")");
                if (onEscalateCallback != null) onEscalateCallback.run();
            }
        }
    }
}
```

### 7.3 Thread-Safe Concurrency design
* **Atomic ID Generation:** Using `AtomicInteger`, complaint identifiers are created via `"CMP" + idCounter.incrementAndGet()`, eliminating lock contention.
* **Concurrent Collections:** Internal complaint, warden, and monitor registries use `ConcurrentHashMap`, supporting high-throughput non-blocking concurrent reads.
* **Synchronized Complaint Updates:** Domain methods on `Complaint` use the `synchronized` keyword to guard against race conditions between background daemon threads and user interactions.

### 7.4 Checked Exception Hierarchy
The application has three custom checked exceptions that extend `java.lang.Exception`:
1. `ComplaintNotFoundException`: Thrown when searching for a non-existent grievance ID.
2. `ComplaintAlreadyResolvedException`: Thrown when attempting illegal mutations on finalized tickets.
3. `InvalidCategoryException`: Thrown when an invalid or null category is submitted.

### 7.5 RFC-4180 CSV Storage Engine
Standard `String.split(",")` corrupts CSV files when descriptions contain commas or quotation marks. `StorageService.java` implements a finite-state character tokenizer:
```java
private List<String> parseCsvLine(String line) {
    List<String> tokens = new ArrayList<>();
    StringBuilder sb = new StringBuilder();
    boolean inQuotes = false;
    for (int i = 0; i < line.length(); i++) {
        char c = line.charAt(i);
        if (c == '\"') {
            if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                sb.append('\"'); i++; // Unescape doubled quotes ("")
            } else {
                inQuotes = !inQuotes;  // Toggle quote state
            }
        } else if (c == ',' && !inQuotes) {
            tokens.add(sb.toString()); sb.setLength(0);
        } else {
            sb.append(c);
        }
    }
    tokens.add(sb.toString());
    return tokens;
}
```

### 7.6 Heuristic Room Parsing & Stream-Based Load Balancing
Students input room numbers in various formats (e.g., `C-204`, `Block A 108`, `b-312`). `Student.java` extracts the normalized block identifier:
```java
public String getBlock() {
    if (roomNumber == null || roomNumber.isBlank()) return "General";
    String upper = roomNumber.trim().toUpperCase();
    if (upper.startsWith("BLOCK ")) {
        String[] parts = upper.split("\\s+");
        return parts.length > 1 ? "Block " + parts[1] : "General";
    }
    char firstChar = upper.charAt(0);
    if (firstChar >= 'A' && firstChar <= 'Z') return "Block " + firstChar;
    return "General";
}
```
`GrievanceSystem.java` uses Java Streams to select the block warden carrying the smallest count of active grievances:
```java
private Warden pickWarden(Student student) {
    if (wardens.isEmpty()) return null;
    String studentBlock = (student != null) ? student.getBlock() : "General";
    List<Warden> blockWardens = wardens.values().stream()
        .filter(w -> w.getAssignedBlock().equalsIgnoreCase(studentBlock))
        .collect(Collectors.toList());
    List<Warden> candidates = blockWardens.isEmpty() ? new ArrayList<>(wardens.values()) : blockWardens;
    return candidates.stream()
        .min(Comparator.comparingInt(this::getActiveComplaintsCountForWarden))
        .orElse(candidates.get(0));
}
```

---

# CHAPTER 8: SCREENSHOTS, TERMINAL TRANSCRIPTS & RESULTS

### Transcript 8.1: Main Interactive Navigation Portal
```text
================================================================================
VIT HOSTEL GRIEVANCE REDRESSAL & REAL-TIME SLA MANAGEMENT SYSTEM
================================================================================
1. Student Portal  - Lodge Complaints, Track Status, Confirm & Rate
2. Warden Portal   - View Assigned Tickets, Update Progress, Resolve
3. Admin Portal    - Analytics Dashboard, SLA Breaches, Wardens
4. View All Complaints (Global Explorer & Search Filters)
5. Run Automated Self-Diagnostics & Concurrency Stress Tests
6. Exit & Save State to Disk
--------------------------------------------------------------------------------
Enter your choice (1-6): 1
```

### Transcript 8.2: Student Grievance Registration with Critical 24-Hour SLA Badge
```text
--- Lodge a Grievance ---
Enter your Full Name: Aarav Patel
Enter your Room Number (e.g. C-204, A-102, B-301): C-204
Enter your 10-digit Contact Number: 9811122233
Select Issue Category:
1. ELECTRICAL   2. WATER   3. FIRE_SAFETY [Critical 24h SLA]   4. SECURITY [Critical 24h SLA]
5. HYGIENE      6. DEPOSIT_DISPUTE      7. MAINTENANCE
Enter choice number (1-7): 3
Describe the issue clearly: Corridor smoke detector beeping continuously near C-204

>> CRITICAL CATEGORY DETECTED (FIRE_SAFETY) - 24-Hour SLA Auto-Escalation Daemon Activated.
+--------------------------------------------------------------------------------+
| Complaint ID   : CMP1001 [CRITICAL SLA]                                        |
| Status         : ASSIGNED                                                     |
| Category       : FIRE_SAFETY                                                  |
| Description    : Corridor smoke detector beeping continuously near C-204      |
| Student        : Aarav Patel (ID: S101, Room: C-204, Block: Block C)          |
| Contact        : 9811122233                                                   |
| Assigned Warden: Mr. Verma (Block C)                                          |
| Submitted At   : 2026-09-14T17:29:12.315                                      |
+--------------------------------------------------------------------------------+
```

### Transcript 8.3: Real-Time SLA Breach Escalation System Alert
```text
[SYSTEM ALERT - SLA BREACH] CMP1001 unresolved after 86400s!
>> Ticket has been automatically escalated to: Prof. Dr. Sanat Jain (Chief Warden)
>> High-priority notification dispatched. Updated status persisted to disk.
```

### Transcript 8.4: Closed-Loop Resident Verification & 5-Star Rating
```text
Enter Complaint ID to verify: CMP1001
Current Status: RESOLVED | Resolution Remarks: Sensor battery replaced by technician.
Was the problem fixed satisfactorily? (Y/N): Y
Please rate the repair quality (1-5 Stars): 5
Enter your feedback comments: Fixed within 2 hours, technician was courteous and professional!

>> SUCCESS: Complaint CMP1001 confirmed and closed permanently.
>> Thank you for helping keep VIT Bhopal residential blocks safe!
```

### Transcript 8.5: Executive Administrative Analytics Dashboard
```text
+================================================================================+
|                   HOSTEL GRIEVANCE SYSTEM CAMPUS ANALYTICS                     |
+================================================================================+
| Total Complaints Registered    : 14                                           |
| Pending / Active (Unresolved)  : 4                                            |
|   - Assigned to Warden         : 2                                            |
|   - In Progress (Under repair) : 2                                            |
| Resolved (Pending review)      : 1                                            |
| Confirmed Closed by Students   : 9                                            |
| Critical SLA Breaches          : 1                                            |
| Average Student Rating         : 4.82 / 5.00 Stars                            |
+--------------------------------------------------------------------------------+
| Category Breakdown:                                                            |
|   ELECTRICAL                   : 6                                            |
|   WATER                        : 4                                            |
|   FIRE_SAFETY                  : 2                                            |
|   SECURITY                     : 1                                            |
|   MAINTENANCE                  : 1                                            |
+================================================================================+
```

### Transcript 8.6: Flat-File CSV Disk Storage Snapshot (`data/complaints.csv`)
```csv
complaint_id,student_id,student_name,room_number,contact_number,category,description,status,assigned_warden_id,submitted_at,resolved_at,resolution_notes,rating,student_feedback,is_escalated
CMP1001,S101,"Aarav Patel","C-204","9811122233",FIRE_SAFETY,"Corridor smoke detector beeping",CONFIRMED,W103,2026-09-14T17:29:12,2026-09-14T18:15:00,"Sensor battery replaced",5,"Fixed quickly!",false
CMP1002,S102,"Rohan Sharma","A-108","9822233344",ELECTRICAL,"Study lamp socket sparking, urgent",IN_PROGRESS,W101,2026-09-14T17:35:40,,,2,0,"",false
```

---

# CHAPTER 9: TESTING APPROACH & EMPIRICAL VERIFICATION

### 9.1 Testing method
Instead of testing everything manually, we engineered a dedicated 34-assertion automated test harness in `GrievanceSystemTest.java`. The harness exercises state transitions, heuristic room parsing, custom exception throwing, background thread execution, and concurrent thread safety in under two seconds.

### 9.2 34-Assertion Verification Matrix

| Suite | Assertion Identifier | Test Case Description & Verification Objective | Result |
| :--- | :--- | :--- | :---: |
| **Suite 1** | `Submission-NotNull` | Verifies complaint instance is created non-null upon submission. | **PASS** |
| **Suite 1** | `ID-Format` | Verifies generated identifier conforms to 'CMP' prefix convention. | **PASS** |
| **Suite 1** | `Initial-Status` | Verifies new complaints are automatically routed to ASSIGNED. | **PASS** |
| **Suite 1** | `Category-Match` | Verifies domain category enum is preserved accurately. | **PASS** |
| **Suite 2** | `Block-A-Assignment` | Verifies room 'A-201' routes to Mrs. Rao (Block A). | **PASS** |
| **Suite 2** | `Block-B-Assignment` | Verifies room 'B-305' routes to Mr. Sharma (Block B). | **PASS** |
| **Suite 2** | `Block-C-Assignment` | Verifies room 'C-410' routes to Mr. Verma (Block C). | **PASS** |
| **Suite 3** | `Status-InProgress` | Verifies transition to IN_PROGRESS upon warden pickup. | **PASS** |
| **Suite 3** | `Remarks-Check` | Verifies progress remarks string is accurately stored. | **PASS** |
| **Suite 3** | `Status-Resolved` | Verifies transition to RESOLVED upon technician completion. | **PASS** |
| **Suite 3** | `ResolvedAt-Set` | Verifies resolved timestamp is recorded in ISO-8601 format. | **PASS** |
| **Suite 3** | `ResolutionNotes-Check` | Verifies technician resolution remarks are preserved. | **PASS** |
| **Suite 3** | `Status-Confirmed` | Verifies transition to CONFIRMED on student review. | **PASS** |
| **Suite 3** | `Rating-Check` | Verifies 5-star evaluation score is recorded properly. | **PASS** |
| **Suite 3** | `Feedback-Check` | Verifies student evaluation notes are stored accurately. | **PASS** |
| **Suite 4** | `Status-BeforeReopen` | Verifies ticket is in RESOLVED state prior to student rejection. | **PASS** |
| **Suite 4** | `Status-AfterReopen` | Verifies transition to REOPENED on student rejection. | **PASS** |
| **Suite 4** | `Reopen-Reason-Match` | Verifies student justification is captured in reopenReason. | **PASS** |
| **Suite 4** | `Reopen-IsResolvedFalse` | Verifies isResolved() returns false to keep ticket active in queue. | **PASS** |
| **Suite 5** | `Exception-NotFound` | Verifies ComplaintNotFoundException thrown on invalid ID lookup. | **PASS** |
| **Suite 5** | `Exception-AlreadyResolved`| Verifies ComplaintAlreadyResolvedException on illegal mutations. | **PASS** |
| **Suite 5** | `Exception-InvalidCategory`| Verifies InvalidCategoryException thrown on null/invalid category. | **PASS** |
| **Suite 6** | `Initial-NotEscalated` | Verifies isEscalated() returns false upon initial ticket filing. | **PASS** |
| **Suite 6** | `Triggered-Escalated` | Verifies daemon thread auto-escalates after SLA deadline. | **PASS** |
| **Suite 6** | `Status-Escalated` | Verifies status updates to ESCALATED upon SLA breach. | **PASS** |
| **Suite 6** | `Cancelled-NotEscalated` | Verifies thread interrupt prevents premature SLA escalation. | **PASS** |
| **Suite 6** | `Status-RemainsResolved` | Verifies resolved ticket remains in RESOLVED state despite timer. | **PASS** |
| **Suite 7** | `Concurrent-Count` | Verifies 20 parallel threads submit exactly 20 complaints. | **PASS** |
| **Suite 7** | `Concurrent-Unique-IDs` | Verifies all 20 generated complaint IDs are strictly distinct. | **PASS** |
| **Suite 7** | `Loaded-ID` | Verifies complaint ID is restored accurately from disk. | **PASS** |
| **Suite 7** | `Loaded-Status` | Verifies complaint status is preserved across disk reload. | **PASS** |
| **Suite 7** | `Loaded-Rating` | Verifies student rating score is preserved across disk reload. | **PASS** |
| **Suite 7** | `Loaded-Feedback` | Verifies student feedback text is preserved across disk reload. | **PASS** |
| **Suite 7** | `Loaded-ResolutionNotes` | Verifies warden resolution remarks are preserved across disk reload. | **PASS** |

### 9.3 Concurrency & Stress Testing
To check how the system behaves when many complaints are submitted together, the test harness launches 20 concurrent threads simulating simultaneous resident submissions:
```java
int threadCount = 20;
Thread[] threads = new Thread[threadCount];
Set<String> generatedIds = ConcurrentHashMap.newKeySet();

for (int i = 0; i < threadCount; i++) {
    final int idx = i;
    threads[i] = new Thread(() -> {
        try {
            Complaint c = gs.submitComplaint(
                new Student("STU" + idx, "Student" + idx, "A-" + (100 + idx), "9999999999"),
                ComplaintCategory.ELECTRICAL,
                "Concurrent test breakdown " + idx
            );
            generatedIds.add(c.getId());
        } catch (Exception ignored) {}
    });
    threads[i].start();
}
for (Thread t : threads) t.join();
assertEqual(generatedIds.size(), threadCount, "Concurrent-Unique-IDs");
```
* **Result:** All 20 threads completed without deadlocks, generating exactly 20 distinct sequential IDs (`CMP1001` through `CMP1020`) with zero collisions.

### 9.4 Automated Test Suite Execution Transcript
```text
================================================================================
                 STARTING AUTOMATED VERIFICATION TEST SUITE                     
================================================================================
  [PASS] Submission-NotNull                      
  [PASS] ID-Format                               
  [PASS] Initial-Status                          
  [PASS] Category-Match                          
  [PASS] Block-A-Assignment                      
  [PASS] Block-B-Assignment                      
  [PASS] Block-C-Assignment                      
  [PASS] Status-InProgress                       
  [PASS] Remarks-Check                           
  [PASS] Status-Resolved                         
  [PASS] ResolvedAt-Set                          
  [PASS] ResolutionNotes-Check                   
  [PASS] Status-Confirmed                        
  [PASS] Rating-Check                            
  [PASS] Feedback-Check                          
  [PASS] Status-BeforeReopen                     
  [PASS] Status-AfterReopen                      
  [PASS] Reopen-Reason-Match                     
  [PASS] Reopen-IsResolvedFalse                  
  [PASS] Exception-NotFound                      
  [PASS] Exception-AlreadyResolved               
  [PASS] Exception-InvalidCategory               
  [PASS] Initial-NotEscalated                    

[SYSTEM ALERT - SLA BREACH] CMP1001 unresolved after 0s! Escalated to: Test Admin (Superintendent)
  [PASS] Triggered-Escalated                     
  [PASS] Status-Escalated                        
  [PASS] Cancelled-NotEscalated                  
  [PASS] Status-RemainsResolved                  
  [PASS] Concurrent-Count                        
  [PASS] Concurrent-Unique-IDs                   
  [PASS] Loaded-ID                               
  [PASS] Loaded-Status                           
  [PASS] Loaded-Rating                           
  [PASS] Loaded-Feedback                         
  [PASS] Loaded-ResolutionNotes                  
================================================================================
TEST SUMMARY: 34 PASSED, 0 FAILED (TOTAL: 34 - 100% SUCCESS RATE)
================================================================================
```

---

# CHAPTER 10: PRACTICAL DEVELOPMENT HURDLES & SOLUTIONS

### 10.1 The Quoted Comma CSV Corruption Issue
* **Problem:** In the initial prototype, descriptions containing commas (e.g., *"Fan regulator broken, makes humming sound"*) caused `line.split(",")` to shift subsequent fields. Timestamps were parsed as ratings, causing fatal `NumberFormatException` crashes.
* **Fix:** We replaced naive string splitting with an RFC-4180 compliant finite-state tokenizer in `StorageService.java` that tracks quotation state (`inQuotes`) character by character and preserves escaped quotes (`""`).

### 10.2 Negative Complaint Identifiers from NanoTime Hashing
* **Problem:** Initially, ticket IDs were generated via `System.nanoTime() % 100000`. On multi-core CPUs, `nanoTime()` produced negative numbers, generating invalid IDs like `CMP-38102`.
* **Fix:** We adopted a thread-safe `AtomicInteger(1000)`. On startup, `GrievanceSystem` scans `complaints.csv`, parses the highest existing numeric suffix, and sets the counter to `Math.max(highestId, currentCounter)`, guaranteeing strictly positive sequential IDs.

### 10.3 Background SLA Threads Blocking JVM Shutdown
* **Problem:** When selecting "Exit" in the console menu, the terminal hung indefinitely. Non-daemon user threads sleeping for 24 hours kept the JVM process alive.
* **Fix:** We called `setDaemon(true)` in the `EscalationMonitor` constructor, allowing the JVM to stop cleanly upon application exit. Additionally, resolving a ticket triggers `cancelMonitor()`, which calls `interrupt()` to cleanly abort the thread.

### 10.4 Warden Workload Imbalances
* **Problem:** A naive assignment algorithm routed all complaints in a block to the first warden in the map, leaving other wardens idle.
* **Fix:** We implemented load balancing in `pickWarden()` using Java Streams to query active workloads and route complaints to the warden with the fewest active tickets.

### 10.5 Scanner Token Skipping in Console Menus
* **Problem:** Calling `scanner.nextInt()` followed by `scanner.nextLine()` caused the scanner to consume the trailing newline character, skipping user name input.
* **Fix:** We standardized all console inputs through a helper function that reads full lines via `scanner.nextLine().trim()` and parses integers defensively using `try-catch` blocks.

---

# CHAPTER 11: LEARNINGS & KEY TAKEAWAYS

* **Applied Object-Oriented Design:** Translating theoretical concepts (Encapsulation, Abstraction, Polymorphism) into resilient code with synchronized state mutators and clean facade boundaries.
* **Concurrency Pitfalls & Multi-Threading Nuances:** Managing thread interruption, memory visibility with `volatile`, atomic hardware instructions (CAS), and daemon thread lifecycles.
* **Defensive Engineering:** Building self-contained persistence with custom RFC-4180 parsing without relying on heavy third-party libraries.
* **Understanding the User Problem:** Developing software that addresses the practical, day-to-day frustrations of students, wardens, and administrative staff at VIT Bhopal.

---

# CHAPTER 12: LIMITATIONS & FUTURE ENHANCEMENTS

### 12.1 Current Limitations
* **Console-Only UI:** The interface runs in the terminal, lacking a web or mobile frontend.
* **Single-Host Concurrency Ceiling:** Flat CSV storage is suitable for standalone evaluation, but not suitable for high-throughput distributed server clusters.
* **Local Notifications Only:** Alerts are printed to the console rather than sent via SMS or push notifications.

### 12.2 Possible Future Improvements
1. **Spring Boot REST API:** Migrate the service layer to Spring Boot microservices, exposing REST endpoints for web and mobile clients.
2. **Real-Time Messaging (Twilio / WhatsApp):** Integrate WhatsApp Business API webhooks to notify students upon technician dispatch.
3. **Multimedia Damage Attachments:** Support image and video uploads of faulty fixtures stored on Amazon S3.
4. **PostgreSQL Migration:** Transition `StorageService` to PostgreSQL using Spring Data JPA and Flyway migrations.
5. **Mobile Application:** Build a cross-platform Flutter application for students and maintenance technicians.

---

# CHAPTER 13: CONCLUSION

We designed, developed, and tested the **Hostel Grievance Redressal and Real-Time SLA Management System** for our CSE2001 course project. The application tries to solve the problems of using paper registers at VIT Bhopal University by by adding automatic block-based assignment, basic workload balancing between wardens, background SLA timers for safety-critical emergencies, student confirmation and ratings, and simple CSV-based storage.

The project uses Core Java features such as `ConcurrentHashMap`, `AtomicInteger`, custom checked exceptions, daemon threads, and stream-based heuristics, we delivered a reliable, thread-safe solution verified by a 34-assertion automated test harness with all 34 tests passing.

---

# REFERENCES

1. Schildt, Herbert. *Java: The Complete Reference*, 12th Edition. McGraw Hill Education, 2021.
2. Bloch, Joshua. *Effective Java*, 3rd Edition. Addison-Wesley Professional, 2018.
3. Goetz, Brian, et al. *Java Concurrency in Practice*. Addison-Wesley Professional, 2006.
4. Oracle Corporation. *Java Standard Edition 17 & 21 API Documentation*: Package `java.util.concurrent` & `java.lang.Thread` design. Oracle Corporation, 2023.
5. Shafer, Dan. *RFC 4180: Common Format and MIME Type for Comma-Separated Values (CSV) Files*. Internet Engineering Task Force (IETF), 2005.
6. VIT Bhopal University. *CSE2001: Object Oriented Programming with Java Course Syllabus & Laboratory Manual*. School of Computing Science and Engineering, 2025-2026.
