package grievance.model;

/**
 * The type of issue a student is reporting.
 * FIRE_SAFETY and SECURITY are treated as critical and get an
 * automatic escalation watcher (see EscalationMonitor).
 */
public enum ComplaintCategory {
    ELECTRICAL,
    WATER,
    FIRE_SAFETY,
    SECURITY,
    HYGIENE,
    DEPOSIT_DISPUTE,
    MAINTENANCE;

    public boolean isCritical() {
        return this == FIRE_SAFETY || this == SECURITY;
    }
}
