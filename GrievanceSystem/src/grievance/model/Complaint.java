package grievance.model;

import java.time.LocalDateTime;

/**
 * Core entity of the system. Status is read/written from both the
 * main thread (student/warden actions) and a background
 * EscalationMonitor thread, so every mutator is synchronized.
 */
public class Complaint {
    private final String id;
    private final Student submittedBy;
    private final ComplaintCategory category;
    private final String description;
    private ComplaintStatus status;
    private final LocalDateTime submittedAt;
    private LocalDateTime resolvedAt;
    private Warden assignedWarden;
    private boolean escalated;
    private String progressRemarks;
    private String resolutionNotes;
    private int rating; // 1-5, 0 if not yet rated
    private String feedbackComment;
    private String reopenReason;

    public Complaint(String id, Student submittedBy, ComplaintCategory category, String description) {
        this(id, submittedBy, category, description, ComplaintStatus.SUBMITTED,
                LocalDateTime.now(), null, null, false, "", "", 0, "", "");
    }

    public Complaint(String id, Student submittedBy, ComplaintCategory category, String description,
                     ComplaintStatus status, LocalDateTime submittedAt, LocalDateTime resolvedAt,
                     Warden assignedWarden, boolean escalated, String progressRemarks,
                     String resolutionNotes, int rating, String feedbackComment, String reopenReason) {
        this.id = id;
        this.submittedBy = submittedBy;
        this.category = category;
        this.description = description;
        this.status = status;
        this.submittedAt = submittedAt != null ? submittedAt : LocalDateTime.now();
        this.resolvedAt = resolvedAt;
        this.assignedWarden = assignedWarden;
        this.escalated = escalated;
        this.progressRemarks = progressRemarks != null ? progressRemarks : "";
        this.resolutionNotes = resolutionNotes != null ? resolutionNotes : "";
        this.rating = rating;
        this.feedbackComment = feedbackComment != null ? feedbackComment : "";
        this.reopenReason = reopenReason != null ? reopenReason : "";
    }

    public synchronized void assignTo(Warden warden) {
        this.assignedWarden = warden;
        if (this.status == ComplaintStatus.SUBMITTED) {
            this.status = ComplaintStatus.ASSIGNED;
        }
    }

    public synchronized void markInProgress(String remarks) {
        this.status = ComplaintStatus.IN_PROGRESS;
        this.progressRemarks = remarks != null ? remarks : "Work in progress.";
    }

    public synchronized void markInProgress() {
        markInProgress("Work commenced by assigned warden.");
    }

    public synchronized void markResolved(String notes) {
        this.status = ComplaintStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolutionNotes = notes != null ? notes : "Resolved by warden.";
    }

    public synchronized void markResolved() {
        markResolved("Resolved by warden.");
    }

    public synchronized void confirmByStudent(int rating, String feedback) {
        this.status = ComplaintStatus.CONFIRMED;
        this.rating = Math.max(1, Math.min(5, rating));
        this.feedbackComment = feedback != null ? feedback : "";
    }

    public synchronized void confirmByStudent() {
        confirmByStudent(5, "Satisfied");
    }

    public synchronized void reopen(String reason) {
        this.status = ComplaintStatus.REOPENED;
        this.reopenReason = reason != null ? reason : "Student reported issue still persists.";
        this.resolvedAt = null;
    }

    public synchronized void escalate() {
        this.status = ComplaintStatus.ESCALATED;
        this.escalated = true;
    }

    public synchronized boolean isResolved() {
        return status == ComplaintStatus.RESOLVED || status == ComplaintStatus.CONFIRMED;
    }

    public synchronized boolean isEscalated() {
        return escalated;
    }

    public synchronized ComplaintStatus getStatus() {
        return status;
    }

    public String getId() { return id; }
    public Student getSubmittedBy() { return submittedBy; }
    public ComplaintCategory getCategory() { return category; }
    public String getDescription() { return description; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public synchronized LocalDateTime getResolvedAt() { return resolvedAt; }
    public synchronized Warden getAssignedWarden() { return assignedWarden; }
    public synchronized String getProgressRemarks() { return progressRemarks; }
    public synchronized String getResolutionNotes() { return resolutionNotes; }
    public synchronized int getRating() { return rating; }
    public synchronized String getFeedbackComment() { return feedbackComment; }
    public synchronized String getReopenReason() { return reopenReason; }

    @Override
    public synchronized String toString() {
        return String.format("[%s] %-14s | %-12s | %-10s | %s (%s)",
                id,
                category,
                status,
                (category.isCritical() ? "CRITICAL" : "NORMAL"),
                submittedBy != null ? submittedBy.getName() : "Unknown",
                submittedBy != null ? submittedBy.getRoomNumber() : "N/A");
    }

    /**
     * Formats complaint details into a clear, bordered card for CLI presentation.
     */
    public synchronized String toDetailedString() {
        StringBuilder sb = new StringBuilder();
        sb.append("+--------------------------------------------------------------------------------+\n");
        sb.append(String.format("| Complaint ID   : %-60s |\n", id + (category.isCritical() ? " [CRITICAL SLA]" : "")));
        sb.append(String.format("| Status         : %-60s |\n", status + (escalated ? " (ESCALATED TO ADMIN)" : "")));
        sb.append(String.format("| Category       : %-60s |\n", category));
        sb.append(String.format("| Description    : %-60s |\n", description));
        if (submittedBy != null) {
            sb.append(String.format("| Student        : %-60s |\n",
                    submittedBy.getName() + " (ID: " + submittedBy.getId() + ", Room: " + submittedBy.getRoomNumber() + ", Block: " + submittedBy.getBlock() + ")"));
            sb.append(String.format("| Contact        : %-60s |\n", submittedBy.getContact()));
        }
        sb.append(String.format("| Assigned Warden: %-60s |\n",
                assignedWarden != null ? assignedWarden.getName() + " (" + assignedWarden.getAssignedBlock() + ")" : "Unassigned"));
        sb.append(String.format("| Submitted At   : %-60s |\n", submittedAt));
        if (resolvedAt != null) {
            sb.append(String.format("| Resolved At    : %-60s |\n", resolvedAt));
        }
        if (!progressRemarks.isEmpty()) {
            sb.append(String.format("| Progress Notes : %-60s |\n", progressRemarks));
        }
        if (!resolutionNotes.isEmpty()) {
            sb.append(String.format("| Resolution     : %-60s |\n", resolutionNotes));
        }
        if (!reopenReason.isEmpty()) {
            sb.append(String.format("| Reopen Reason  : %-60s |\n", reopenReason));
        }
        if (rating > 0) {
            String stars = "★".repeat(rating) + "☆".repeat(5 - rating);
            sb.append(String.format("| Rating         : %-60s |\n", stars + " (" + rating + "/5)"));
        }
        if (!feedbackComment.isEmpty()) {
            sb.append(String.format("| Feedback       : %-60s |\n", feedbackComment));
        }
        sb.append("+--------------------------------------------------------------------------------+");
        return sb.toString();
    }
}
