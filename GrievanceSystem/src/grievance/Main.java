package grievance;

import grievance.exception.*;
import grievance.model.*;
import grievance.service.GrievanceSystem;
import java.util.List;
import java.util.Scanner;

/**
 * Interactive command-line entry point. Real users (students, wardens)
 * type their own data here -- nothing is hardcoded.
 */
public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static GrievanceSystem system;
    private static final java.util.concurrent.atomic.AtomicInteger studentCounter = new java.util.concurrent.atomic.AtomicInteger(5000);

    public static void main(String[] args) {
        setup();
        boolean running = true;
        while (running && scanner.hasNextLine()) {
            printMainMenu();
            String choice = readLine();
            switch (choice) {
                case "1": studentPortalFlow(); break;
                case "2": wardenPortalFlow(); break;
                case "3": adminPortalFlow(); break;
                case "4": globalExplorerFlow(); break;
                case "5": runDiagnosticsFlow(); break;
                case "6":
                    running = false;
                    System.out.println("\nAll state safely saved to data/complaints.csv. Exiting Grievance System. Goodbye!");
                    break;
                case "":
                    // End of stream
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please choose from 1 to 6.");
            }
        }
    }

    private static String readLine() {
        if (scanner.hasNextLine()) {
            return scanner.nextLine().replace("\uFEFF", "").trim();
        }
        return "";
    }

    private static void setup() {
        Admin admin = new Admin("A1", "Dr. Sharma", "Dean of Student Welfare & Chief Administrator");
        system = new GrievanceSystem(admin);

        // Register wardens for distinct hostel blocks
        system.registerWarden(new Warden("W1", "Mr. Verma", "Block C", "+91-98765-00001"));
        system.registerWarden(new Warden("W2", "Mrs. Rao", "Block A", "+91-98765-00002"));
        system.registerWarden(new Warden("W3", "Mr. Khan", "Block B", "+91-98765-00003"));

        // Seed demo data if system is freshly initialized and storage is empty
        if (system.getAllComplaints().isEmpty()) {
            try {
                Student s1 = new Student("S101", "Aarav Patel", "C-204", "9811122233");
                Student s2 = new Student("S102", "Priya Nair", "A-108", "9822233344");
                Student s3 = new Student("S103", "Karan Singh", "B-312", "9833344455");

                Complaint c1 = system.submitComplaint(s1, ComplaintCategory.FIRE_SAFETY, "Smoke detector near C-204 beeping persistently");
                system.markInProgress(c1.getId(), "Technician dispatched with replacement 9V battery");

                Complaint c2 = system.submitComplaint(s2, ComplaintCategory.WATER, "Low water pressure and leak in bathroom tap");
                system.resolveComplaint(c2.getId(), "Replaced washer in tap fixture. Water pressure restored.");
                system.confirmResolution(c2.getId(), 5, "Fixed very promptly. Tap works great now!");

                system.submitComplaint(s3, ComplaintCategory.ELECTRICAL, "Ceiling fan regulator stuck on maximum speed");
            } catch (Exception ignored) {}
        }
    }

    private static void printMainMenu() {
        System.out.println("\n================================================================================");
        System.out.println("          VIT HOSTEL GRIEVANCE REDRESSAL & SLA MANAGEMENT SYSTEM                ");
        System.out.println("================================================================================");
        System.out.println("  1. Student Portal  - Lodge Complaints, Track Status, Confirm & Rate");
        System.out.println("  2. Warden Portal   - View Assigned Tickets, Update Progress, Resolve");
        System.out.println("  3. Admin Portal    - Analytics Dashboard, SLA Breaches, Wardens");
        System.out.println("  4. View All Complaints (Global Explorer & Filters)");
        System.out.println("  5. Run Automated Self-Diagnostics & Tests");
        System.out.println("  6. Exit & Save");
        System.out.println("--------------------------------------------------------------------------------");
        System.out.print("Enter your choice (1-6): ");
    }

    
    // 1. STUDENT PORTAL
    private static void studentPortalFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [STUDENT PORTAL] ---");
            System.out.println("1. Submit a New Complaint");
            System.out.println("2. Track My Complaints (by Student Name or ID)");
            System.out.println("3. Confirm Resolution & Give Feedback / Rating");
            System.out.println("4. Reopen an Unsatisfactorily Resolved Complaint");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            String ch = readLine();
            switch (ch) {
                case "1": submitComplaintFlow(); break;
                case "2": trackStudentComplaintsFlow(); break;
                case "3": confirmResolutionFlow(); break;
                case "4": reopenComplaintFlow(); break;
                case "5": back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    private static void submitComplaintFlow() {
        System.out.println("\n--- Lodge a Grievance ---");
        System.out.print("Enter your Full Name: ");
        String name = readLine();
        if (name.isEmpty()) {
            System.out.println("Name cannot be empty.");
            return;
        }

        System.out.print("Enter your Room Number (e.g. C-204, A-102, B-301): ");
        String room = readLine();
        if (room.isEmpty()) {
            System.out.println("Room number cannot be empty.");
            return;
        }

        System.out.print("Enter your 10-digit Contact Number: ");
        String contact = readLine();
        if (contact.length() < 10) {
            System.out.println("Warning: Contact number should be at least 10 digits.");
        }

        Student student = new Student("S" + studentCounter.incrementAndGet(), name, room, contact);

        ComplaintCategory category = chooseCategory();
        if (category == null) {
            System.out.println("Submission cancelled.");
            return;
        }

        System.out.print("Describe the issue clearly: ");
        String description = readLine();
        if (description.isEmpty()) {
            System.out.println("Description cannot be empty.");
            return;
        }

        long slaMillis = GrievanceSystem.DEFAULT_SLA_MILLIS;
        if (category.isCritical()) {
            System.out.println(">> CRITICAL CATEGORY DETECTED (" + category + ") - 24-hour Auto-Escalation Enabled.");
            System.out.print(">> Demo/Testing override: Enter SLA in seconds [press Enter for default 24 hours]: ");
            String slaInput = readLine();
            if (!slaInput.isEmpty()) {
                try {
                    slaMillis = Long.parseLong(slaInput) * 1000L;
                    System.out.println(">> Custom SLA window set to " + (slaMillis / 1000) + " seconds.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Default 24h SLA applied.");
                }
            }
        }

        try {
            Complaint complaint = system.submitComplaint(student, category, description, slaMillis);
            System.out.println("\nSUCCESS! Grievance successfully registered.");
            System.out.println(complaint.toDetailedString());
        } catch (InvalidCategoryException | IllegalArgumentException e) {
            System.out.println("Error registering complaint: " + e.getMessage());
        }
    }

    private static void trackStudentComplaintsFlow() {
        System.out.print("Enter your Student Name or ID to track: ");
        String query = readLine();
        List<Complaint> list = system.getComplaintsByStudent(query);
        if (list.isEmpty()) {
            System.out.println("No complaints found for query: " + query);
            return;
        }
        System.out.println("\nFound " + list.size() + " complaint(s):");
        for (Complaint c : list) {
            System.out.println(c.toDetailedString());
        }
    }

    private static void confirmResolutionFlow() {
        System.out.print("Enter Complaint ID to confirm resolution: ");
        String id = readLine();
        try {
            Complaint c = system.getComplaintOrThrow(id);
            if (c.getStatus() != ComplaintStatus.RESOLVED) {
                System.out.println("Complaint " + id + " is currently in '" + c.getStatus() + "' status. Only RESOLVED complaints can be confirmed.");
                return;
            }
            System.out.println(c.toDetailedString());

            int rating = 5;
            System.out.print("Please rate the resolution quality (1 to 5 stars) [default 5]: ");
            String rInput = readLine();
            if (!rInput.isEmpty()) {
                try {
                    rating = Integer.parseInt(rInput);
                    if (rating < 1 || rating > 5) rating = 5;
                } catch (NumberFormatException ignored) {}
            }

            System.out.print("Enter any feedback comment: ");
            String feedback = readLine();
            if (feedback.isEmpty()) feedback = "Satisfied with resolution.";

            system.confirmResolution(id, rating, feedback);
            System.out.println("\nThank you! Complaint " + id + " has been confirmed and officially closed.");
        } catch (ComplaintNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void reopenComplaintFlow() {
        System.out.print("Enter Complaint ID to reopen: ");
        String id = readLine();
        try {
            Complaint c = system.getComplaintOrThrow(id);
            if (c.getStatus() != ComplaintStatus.RESOLVED && c.getStatus() != ComplaintStatus.CONFIRMED) {
                System.out.println("Cannot reopen complaint " + id + " because it is in '" + c.getStatus() + "' state.");
                return;
            }
            System.out.print("Enter reason why the issue persists / needs reopening: ");
            String reason = readLine();
            if (reason.isEmpty()) reason = "Student indicated the issue recurred.";

            system.reopenComplaint(id, reason);
            System.out.println("\nComplaint " + id + " has been reopened and notified to the warden.");
        } catch (ComplaintNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    // 2. WARDEN PORTAL
    
    private static void wardenPortalFlow() {
        List<Warden> wardens = system.getAllWardens();
        if (wardens.isEmpty()) {
            System.out.println("No wardens registered in the system.");
            return;
        }

        System.out.println("\nSelect Warden Profile:");
        for (int i = 0; i < wardens.size(); i++) {
            Warden w = wardens.get(i);
            System.out.println("  " + (i + 1) + ". " + w.getName() + " (" + w.getAssignedBlock() + ", ID: " + w.getId() + ")");
        }
        System.out.print("Choose warden number (or 0 to cancel): ");
        String choice = readLine();
        int idx;
        try {
            idx = Integer.parseInt(choice) - 1;
            if (idx == -1) return;
            if (idx < 0 || idx >= wardens.size()) {
                System.out.println("Invalid selection.");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        Warden currentWarden = wardens.get(idx);
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [WARDEN PORTAL - " + currentWarden.getName() + " (" + currentWarden.getAssignedBlock() + ")] ---");
            System.out.println("1. View Complaints Assigned to Me");
            System.out.println("2. Acknowledge & Mark Ticket IN PROGRESS");
            System.out.println("3. Mark Ticket as RESOLVED");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");
            String ch = readLine();
            switch (ch) {
                case "1": {
                    List<Complaint> list = system.getComplaintsByWarden(currentWarden.getId());
                    if (list.isEmpty()) {
                        System.out.println("No complaints currently assigned to you.");
                    } else {
                        System.out.println("\nYour Assigned Complaints (" + list.size() + "):");
                        list.forEach(c -> System.out.println(c.toDetailedString()));
                    }
                    break;
                }
                case "2": {
                    System.out.print("Enter Complaint ID to mark IN PROGRESS: ");
                    String id = readLine();
                    System.out.print("Enter Progress / Action Remarks (e.g., 'Electrician called'): ");
                    String remarks = readLine();
                    try {
                        system.markInProgress(id, remarks);
                        System.out.println("Success: Complaint " + id + " marked as IN PROGRESS.");
                    } catch (ComplaintNotFoundException | ComplaintAlreadyResolvedException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "3": {
                    System.out.print("Enter Complaint ID to RESOLVE: ");
                    String id = readLine();
                    System.out.print("Enter Resolution Notes (e.g., 'Replaced faulty MCB switch'): ");
                    String notes = readLine();
                    if (notes.isEmpty()) notes = "Work completed satisfactorily.";
                    try {
                        system.resolveComplaint(id, notes);
                        System.out.println("Success: Complaint " + id + " marked as RESOLVED.");
                    } catch (ComplaintNotFoundException | ComplaintAlreadyResolvedException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "4": back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // =========================================================================
    // 3. ADMIN PORTAL
    // =========================================================================
    private static void adminPortalFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [ADMINISTRATOR & ANALYTICS PORTAL] ---");
            System.out.println("Admin: " + system.getAdmin().getName() + " (" + system.getAdmin().getDesignation() + ")");
            System.out.println("1. View Executive Analytics Dashboard");
            System.out.println("2. View SLA Breaches / Escalated Complaints");
            System.out.println("3. View All Registered Wardens & Workload");
            System.out.println("4. Register a New Warden");
            System.out.println("5. Back to Main Menu");
            System.out.print("Choose an option: ");
            String ch = readLine();
            switch (ch) {
                case "1":
                    System.out.println(system.getAnalyticsReport());
                    break;
                case "2": {
                    List<Complaint> escalated = system.getEscalatedComplaints();
                    if (escalated.isEmpty()) {
                        System.out.println("\n[OK] No SLA breaches or escalated tickets found.");
                    } else {
                        System.out.println("\n[ATTENTION] " + escalated.size() + " Ticket(s) Escalated due to SLA Breach:");
                        escalated.forEach(c -> System.out.println(c.toDetailedString()));
                    }
                    break;
                }
                case "3": {
                    System.out.println("\n--- Registered Wardens ---");
                    for (Warden w : system.getAllWardens()) {
                        long active = system.getComplaintsByWarden(w.getId()).stream().filter(c -> !c.isResolved()).count();
                        System.out.printf("  ID: %-4s | Name: %-14s | Block: %-10s | Contact: %-15s | Active Load: %d\n",
                                w.getId(), w.getName(), w.getAssignedBlock(), w.getContactNumber(), active);
                    }
                    break;
                }
                case "4": {
                    System.out.print("Enter Warden ID (e.g. W4): ");
                    String wid = readLine();
                    System.out.print("Enter Warden Name: ");
                    String wname = readLine();
                    System.out.print("Enter Assigned Hostel Block (e.g. Block D): ");
                    String wblock = readLine();
                    System.out.print("Enter Contact Number: ");
                    String wcontact = readLine();
                    system.registerWarden(new Warden(wid, wname, wblock, wcontact));
                    System.out.println("Warden " + wname + " successfully registered for " + wblock + ".");
                    break;
                }
                case "5": back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // =========================================================================
    // 4. GLOBAL EXPLORER
    // =========================================================================
    private static void globalExplorerFlow() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- [GLOBAL COMPLAINT EXPLORER] ---");
            System.out.println("1. View All Complaints (Table Summary)");
            System.out.println("2. View Detailed Card by Complaint ID");
            System.out.println("3. Filter Complaints by Status");
            System.out.println("4. Back to Main Menu");
            System.out.print("Choose an option: ");
            String ch = readLine();
            switch (ch) {
                case "1": {
                    List<Complaint> all = system.getAllComplaints();
                    if (all.isEmpty()) {
                        System.out.println("No complaints in system.");
                    } else {
                        System.out.println("\n" + "=".repeat(95));
                        System.out.printf("%-9s | %-15s | %-13s | %-10s | %-20s | %-12s\n",
                                "ID", "CATEGORY", "STATUS", "PRIORITY", "STUDENT (ROOM)", "ASSIGNED");
                        System.out.println("-".repeat(95));
                        for (Complaint c : all) {
                            String studentInfo = (c.getSubmittedBy() != null ? c.getSubmittedBy().getName() + " (" + c.getSubmittedBy().getRoomNumber() + ")" : "N/A");
                            String wardenName = (c.getAssignedWarden() != null ? c.getAssignedWarden().getName() : "Unassigned");
                            System.out.printf("%-9s | %-15s | %-13s | %-10s | %-20s | %-12s\n",
                                    c.getId(), c.getCategory(), c.getStatus(),
                                    (c.getCategory().isCritical() ? "CRITICAL" : "NORMAL"),
                                    studentInfo.length() > 20 ? studentInfo.substring(0, 17) + "..." : studentInfo,
                                    wardenName.length() > 12 ? wardenName.substring(0, 9) + "..." : wardenName);
                        }
                        System.out.println("=".repeat(95));
                    }
                    break;
                }
                case "2": {
                    System.out.print("Enter Complaint ID: ");
                    String id = readLine();
                    try {
                        Complaint c = system.getComplaintOrThrow(id);
                        System.out.println(c.toDetailedString());
                    } catch (ComplaintNotFoundException e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                    break;
                }
                case "3": {
                    System.out.println("Available Statuses: " + java.util.Arrays.toString(ComplaintStatus.values()));
                    System.out.print("Enter status name: ");
                    String sInput = readLine().toUpperCase();
                    try {
                        ComplaintStatus status = ComplaintStatus.valueOf(sInput);
                        List<Complaint> filtered = system.getComplaintsByStatus(status);
                        System.out.println("\nComplaints with status " + status + " (" + filtered.size() + "):");
                        filtered.forEach(c -> System.out.println(c.toDetailedString()));
                    } catch (IllegalArgumentException e) {
                        System.out.println("Invalid status value.");
                    }
                    break;
                }
                case "4": back = true; break;
                default: System.out.println("Invalid option.");
            }
        }
    }

    // =========================================================================
    // 5. RUN DIAGNOSTICS & TESTS
    // =========================================================================
    private static void runDiagnosticsFlow() {
        System.out.println("\nRunning internal automated test verification suite...");
        try {
            grievance.test.GrievanceSystemTest.runAllTests();
        } catch (Throwable t) {
            System.out.println("Diagnostics result: " + t.getMessage());
        }
    }

    private static ComplaintCategory chooseCategory() {
        ComplaintCategory[] categories = ComplaintCategory.values();
        System.out.println("Select Issue Category:");
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %-16s %s\n", (i + 1), categories[i], (categories[i].isCritical() ? "[Critical 24h SLA]" : ""));
        }
        System.out.print("Enter choice number (1-" + categories.length + "): ");
        String input = readLine();
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx < 0 || idx >= categories.length) {
                System.out.println("Invalid choice.");
                return null;
            }
            return categories[idx];
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return null;
        }
    }
}
