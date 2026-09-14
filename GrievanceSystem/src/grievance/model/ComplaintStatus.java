package grievance.model;

/**
 * Lifecycle states a complaint moves through:
 * SUBMITTED -> ASSIGNED -> IN_PROGRESS -> RESOLVED -> CONFIRMED
 * A complaint can also move to ESCALATED if it breaches its SLA.
 */
public enum ComplaintStatus {
    SUBMITTED,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CONFIRMED,
    REOPENED,
    ESCALATED
}
