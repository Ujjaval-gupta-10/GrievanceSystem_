package grievance.service;

import grievance.model.Admin;
import grievance.model.Complaint;

/**
 * One instance is started per critical complaint (see
 * GrievanceSystem.submitComplaint). It sleeps for the SLA window,
 * then checks whether the complaint has been resolved in the
 * meantime. If not, it escalates the complaint to the admin.
 *
 * Runs as a daemon thread so it never blocks JVM shutdown.
 */
public class EscalationMonitor extends Thread {
    private final Complaint complaint;
    private final Admin admin;
    private final long slaMillis;
    private final Runnable onEscalateCallback;
    private volatile boolean cancelled = false;

    public EscalationMonitor(Complaint complaint, Admin admin, long slaMillis) {
        this(complaint, admin, slaMillis, null);
    }

    public EscalationMonitor(Complaint complaint, Admin admin, long slaMillis, Runnable onEscalateCallback) {
        super("EscalationMonitor-" + complaint.getId());
        this.complaint = complaint;
        this.admin = admin;
        this.slaMillis = slaMillis;
        this.onEscalateCallback = onEscalateCallback;
        setDaemon(true);
    }

    public void cancelMonitor() {
        this.cancelled = true;
        this.interrupt();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(slaMillis);
        } catch (InterruptedException e) {
            // Thread interrupted because complaint was resolved early or cancelled
            return;
        }

        if (cancelled) {
            return;
        }

        synchronized (complaint) {
            if (!complaint.isResolved()) {
                complaint.escalate();
                System.out.println("\n[SYSTEM ALERT - SLA BREACH] " + complaint.getId() +
                        " unresolved after " + (slaMillis / 1000) + "s! Escalated to: " + admin.getName() + " (" + admin.getDesignation() + ")");
                if (onEscalateCallback != null) {
                    try {
                        onEscalateCallback.run();
                    } catch (Exception ex) {
                        System.err.println("[EscalationMonitor] Callback error: " + ex.getMessage());
                    }
                }
            }
        }
    }
}
