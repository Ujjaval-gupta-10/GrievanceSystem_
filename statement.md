# Problem Statement & Project Scope

## 1. Problem Statement
In university residential hostels, complaints concerning various aspects of maintenance, hygiene, emergencies and infrastructure are very common among students, starting from water outage, broken electrical appliance up to electrical spark and safety violations. Current procedure of complaint handling has significant disadvantages:
- **Manual & Paper-based Logging:** Registering complaints manually in written registers of hostel reception gate usually results in lost complaints, delays and absence of accountability.
- **Absence of SLA & Escalation:** Critical complaints (e.g., fire safety hazard, gas leakage, water logging, security breach) go unhandled and do not get any alerts to relevant hostel management/administration.
- **Unbalanced Distribution of Workload:** Complaints get distributed to random people regardless of boundaries of particular hostel block and load balance.
- **No Transparency & Feedback:** There is no way for students to check real-time resolution progress, confirm whether problem was fixed and provide satisfaction ratings or escalate issues if problem is still pending.

## 2. Scope of the Project
**Hostel Grievance Redressal & SLA Management System** is an independent enterprise-class Java application aimed at digitization and automation of complaint lifecycle in university residential hostels.

The project includes:
- **Smart Routing:** Automatic assignment of complaints to proper warden based on block and minimal work-load of warden.
- **SLA Enforcement & Real-time Automatic Escalation:** Critical complaints get daemon watcher which automatically escalates unhandled complaint to Chief Hostel Administrator/Dean after SLA period.
- **End-to-end Lifecycle Tracking:** Full-fledged multi-stage state machine (`SUBMITTED -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CONFIRMED / REOPENED / ESCALATED`).
- **Data Persistence:** File-based CSV persistence to ensure that the system recovers and maintains full state after reboot without any additional database setup.
- **Quality Assurance & Student Feedback:** Post-resolution student feedback, 1 to 5 stars rating and one-click reopen of unresolved problems.
- **Executive Analysis:** Summary of statistics about number of grievances per type and average satisfaction rates per category.

## 3. Target Users
1. **Hostel Residents (Students):** 
   - Submit new grievance in standard and critical categories.
   - Monitor the real-time status of complaint and remarks of warden who handles complaint.
   - Provide feedback/rating or reopen grievance after resolution if needed.
2. **Hostel Wardens & Maintenance Staff:**
   - See complaints which are assigned to them in regard to their residential block.
   - Acknowledge the receipt of ticket and log in all maintenance activities and mark the ticket as `IN_PROGRESS`.
   - Submit resolution notes for the complaint once the problem is solved.
3. **Chief Hostel Administrator / Dean of Student Welfare:**
   - Get summary about hostel's condition in terms of real-time executive analytics.
   - Receive alerts about SLA expiration and safety hazard.
   - Control allocation of wardens, re-balance work-loads and create new wardens.

## 4. High Level Features
- **Role-based Portals:** Role-specific interactive workflows for Students, Wardens and Administrators.
- **Smart Warden Assignment:** Mapping student's block of residence (e.g., Block A, Block B, Block C) to particular warden assignments and then falling back to the least busy member of staff if needed.
- **Background Multi-threaded SLA Watcher:** Daemon threads monitoring the critical complaints and cancelling threads on early resolution of the problem.
- **Thread-safe Design:** `ConcurrentHashMap`, `AtomicInteger` and synchronized critical sections preventing any data corruption in multi-user concurrent environment.
- **Complete State Machine with Reopening Capability:** To prevent any false resolution and put the ticket closure in the control of the student who raised the issue.
- **Persistent Data Storage:** Application state synchronization with CSV disk storage.
- **Integrated Diagnostics & Verification:** 34-point test engine including unit tests, integration tests, concurrency tests and exceptions.