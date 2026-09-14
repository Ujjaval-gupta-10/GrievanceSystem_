package grievance.service;

import grievance.model.*;
import grievance.exception.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 It is the central service associated with the Student Accommodation Grievance System.
 * Thread-safe using ConcurrentHashMap, AtomicInteger, and background
 There is support for automatic allocation of warden instances on a block basis.
 * SLA escalation, full complaint process, and ongoing storage.
 */
public class GrievanceSystem {

    public static final long DEFAULT_SLA_MILLIS = 24L * 60 * 60 * 1000; // 24 hours

    private final Map<String, Complaint> complaints = new ConcurrentHashMap<>();
    private final Map<String, Warden> wardens = new ConcurrentHashMap<>();
    private final Map<String, EscalationMonitor> activeMonitors = new ConcurrentHashMap<>();
    private final Admin admin;
    private final AtomicInteger idCounter = new AtomicInteger(1000);
    private final StorageService storageService;

    public GrievanceSystem(Admin admin) {
        this(admin, new StorageService("data/complaints.csv"));
    }

    public GrievanceSystem(Admin admin, StorageService storageService) {
        this.admin = admin;
        this.storageService = storageService;
        loadExistingData();
    }

    private void loadExistingData() {
        if (storageService == null) return;
        List<Complaint> loaded = storageService.loadComplaints(wardens);
        for (Complaint c : loaded) {
            complaints.put(c.getId(), c);
            try {
                if (c.getId().startsWith("CMP")) {
                    int num = Integer.parseInt(c.getId().substring(3));
                    idCounter.updateAndGet(curr -> Math.max(curr, num));
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    private synchronized void persistState() {
        if (storageService != null) {
            storageService.saveComplaints(complaints.values());
        }
    }

    public void registerWarden(Warden warden) {
        wardens.put(warden.getId(), warden);
    }

    public List<Warden> getAllWardens() {
        return List.copyOf(wardens.values());
    }

    public Admin getAdmin() {
        return admin;
    }

    /** Submits using the standard 24-hour SLA. */
    public Complaint submitComplaint(Student student, ComplaintCategory category, String description)
            throws InvalidCategoryException {
        return submitComplaint(student, category, description, DEFAULT_SLA_MILLIS);
    }

    /** Submits a complaint with a custom SLA duration. */
    public Complaint submitComplaint(Student student, ComplaintCategory category, String description, long slaMillis)
            throws InvalidCategoryException {
        if (category == null) {
            throw new InvalidCategoryException("Complaint category cannot be null.");
        }
        if (student == null) {
            throw new IllegalArgumentException("Student cannot be null.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Complaint description cannot be empty.");
        }

        String complaintId = "CMP" + idCounter.incrementAndGet();
        Complaint complaint = new Complaint(complaintId, student, category, description.trim());

        Warden assigned = pickWarden(student);
        complaint.assignTo(assigned);

        complaints.put(complaintId, complaint);

        if (category.isCritical()) {
            EscalationMonitor monitor = new EscalationMonitor(complaint, admin, slaMillis, this::persistState);
            activeMonitors.put(complaintId, monitor);
            monitor.start();
        }

        persistState();
        return complaint;
    }

    /**
     * Smart Warden Assignment:
     * Match the student's block (for example, 'Block C') with the block assigned by the warden.
     If no matches or more than one match exists, the warden who has the least active workload is chosen.
     */
    private Warden pickWarden(Student student) {
        if (wardens.isEmpty()) {
            return null;
        }

        String studentBlock = student != null ? student.getBlock() : "General";

        // Filter by block
        List<Warden> blockWardens = wardens.values().stream()
                .filter(w -> w.getAssignedBlock().equalsIgnoreCase(studentBlock))
                .collect(Collectors.toList());

        List<Warden> candidates = blockWardens.isEmpty()
                ? new ArrayList<>(wardens.values())
                : blockWardens;

        // Select candidate with minimum active complaints
        return candidates.stream()
                .min(Comparator.comparingInt(this::getActiveComplaintsCountForWarden))
                .orElse(candidates.get(0));
    }

    private int getActiveComplaintsCountForWarden(Warden warden) {
        return (int) complaints.values().stream()
                .filter(c -> c.getAssignedWarden() != null && c.getAssignedWarden().getId().equals(warden.getId()))
                .filter(c -> !c.isResolved())
                .count();
    }

    public void markInProgress(String complaintId, String remarks)
            throws ComplaintNotFoundException, ComplaintAlreadyResolvedException {
        Complaint complaint = getComplaintOrThrow(complaintId);
        if (complaint.isResolved()) {
            throw new ComplaintAlreadyResolvedException("Complaint " + complaintId + " is already resolved.");
        }
        complaint.markInProgress(remarks);
        persistState();
    }

    public void resolveComplaint(String complaintId, String resolutionNotes)
            throws ComplaintNotFoundException, ComplaintAlreadyResolvedException {
        Complaint complaint = getComplaintOrThrow(complaintId);
        if (complaint.isResolved()) {
            throw new ComplaintAlreadyResolvedException("Complaint " + complaintId + " is already resolved.");
        }
        complaint.markResolved(resolutionNotes);

        EscalationMonitor monitor = activeMonitors.remove(complaintId);
        if (monitor != null) {
            monitor.cancelMonitor();
        }

        persistState();
    }

    public void resolveComplaint(String complaintId)
            throws ComplaintNotFoundException, ComplaintAlreadyResolvedException {
        resolveComplaint(complaintId, "Resolved by warden.");
    }

    public void confirmResolution(String complaintId, int rating, String feedback)
            throws ComplaintNotFoundException {
        Complaint complaint = getComplaintOrThrow(complaintId);
        complaint.confirmByStudent(rating, feedback);

        EscalationMonitor monitor = activeMonitors.remove(complaintId);
        if (monitor != null) {
            monitor.cancelMonitor();
        }

        persistState();
    }

    public void confirmResolution(String complaintId) throws ComplaintNotFoundException {
        confirmResolution(complaintId, 5, "Student confirmed resolution.");
    }

    public void reopenComplaint(String complaintId, String reason)
            throws ComplaintNotFoundException {
        Complaint complaint = getComplaintOrThrow(complaintId);
        complaint.reopen(reason);
        persistState();
    }

    public Complaint getComplaintOrThrow(String complaintId) throws ComplaintNotFoundException {
        Complaint complaint = complaints.get(complaintId);
        if (complaint == null) {
            throw new ComplaintNotFoundException("No complaint found with ID: " + complaintId);
        }
        return complaint;
    }

    public List<Complaint> getAllComplaints() {
        return complaints.values().stream()
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaints.values().stream()
                .filter(c -> c.getStatus() == status)
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsByStudent(String studentNameOrId) {
        if (studentNameOrId == null || studentNameOrId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String query = studentNameOrId.trim().toLowerCase();
        return complaints.values().stream()
                .filter(c -> c.getSubmittedBy() != null &&
                        (c.getSubmittedBy().getId().toLowerCase().contains(query) ||
                                c.getSubmittedBy().getName().toLowerCase().contains(query)))
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Complaint> getComplaintsByWarden(String wardenId) {
        return complaints.values().stream()
                .filter(c -> c.getAssignedWarden() != null && c.getAssignedWarden().getId().equalsIgnoreCase(wardenId))
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    public List<Complaint> getEscalatedComplaints() {
        return complaints.values().stream()
                .filter(Complaint::isEscalated)
                .sorted(Comparator.comparing(Complaint::getSubmittedAt).reversed())
                .collect(Collectors.toList());
    }

    /**
     Creates analytical dashboard metrics for the purpose of administrative review.
     */
    public String getAnalyticsReport() {
        int total = complaints.size();
        if (total == 0) {
            return "No complaints recorded yet.";
        }

        long submitted = countByStatus(ComplaintStatus.SUBMITTED);
        long assigned = countByStatus(ComplaintStatus.ASSIGNED);
        long inProgress = countByStatus(ComplaintStatus.IN_PROGRESS);
        long resolved = countByStatus(ComplaintStatus.RESOLVED);
        long confirmed = countByStatus(ComplaintStatus.CONFIRMED);
        long reopened = countByStatus(ComplaintStatus.REOPENED);
        long escalated = complaints.values().stream().filter(Complaint::isEscalated).count();

        double avgRating = complaints.values().stream()
                .filter(c -> c.getRating() > 0)
                .mapToInt(Complaint::getRating)
                .average()
                .orElse(0.0);

        long criticalCount = complaints.values().stream()
                .filter(c -> c.getCategory().isCritical())
                .count();

        StringBuilder sb = new StringBuilder();
        sb.append("+================================================================================+\n");
        sb.append("|                       HOSTEL GRIEVANCE SYSTEM ANALYTICS                        |\n");
        sb.append("+================================================================================+\n");
        sb.append(String.format("| Total Complaints Registered   : %-*d |\n", 46, total));
        sb.append(String.format("| Pending / Active (Unresolved) : %-46d |\n", (submitted + assigned + inProgress + reopened)));
        sb.append(String.format("|   - Submitted (Pending triage): %-*d |\n", 46, submitted));
        sb.append(String.format("|   - Assigned to Warden        : %-46d |\n", assigned));
        sb.append(String.format("|   - In Progress (Under repair): %-46d |\n", inProgress));
        sb.append(String.format("|   - Reopened by Student       : %-46d |\n", reopened));
        sb.append(String.format("| Resolved (Pending confirmation): %-45d |\n", resolved));
        sb.append(String.format("| Confirmed Closed by Students  : %-46d |\n", confirmed));
        sb.append(String.format("| Number of critical SLA breaches / escalations: %-46d |\n", escalated));
        sb.append(String.format("| Total Critical Issues Reported: %-46d |\n", criticalCount));
        sb.append(String.format("| Average Student Rating        : %-46.2f / 5.00 |\n", avgRating));
        sb.append("+--------------------------------------------------------------------------------+\n");
        sb.append("| Category Breakdown:                                                            |\n");
        for (ComplaintCategory cat : ComplaintCategory.values()) {
            long count = complaints.values().stream()
                    .filter(c -> c.getCategory() == cat)
                    .count();
            if (count > 0) {
                sb.append(String.format("|   %-20s : %-48d |\n", cat.name(), count));
            }
        }
        sb.append("+================================================================================+");
        return sb.toString();
    }

    private long countByStatus(ComplaintStatus status) {
        return complaints.values().stream().filter(c -> c.getStatus() == status).count();
    }
}
