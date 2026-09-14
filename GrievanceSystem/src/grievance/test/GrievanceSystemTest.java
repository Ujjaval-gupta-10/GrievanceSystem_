package grievance.test;

import grievance.exception.*;
import grievance.model.*;
import grievance.service.GrievanceSystem;
import grievance.service.StorageService;
import java.io.File;
import java.util.concurrent.*;

/**
 * Comprehensive automated verification suite testing all business logic,
* state machine transitions, concurrent safety, auto-escalation, and persistence.
* Can be run standalone from CLI without any external dependencies or JARs.
 */
public class GrievanceSystemTest {

private static int testsPassed = 0;
private static int testsFailed = 0;

public static void main(String[] args) {
runAllTests();
}

public static void runAllTests() {
System.out.println("================================================================================");
System.out.println("                 STARTING AUTOMATED VERIFICATION TEST SUITE                     ");
System.out.println("================================================================================");

testsPassed = 0;
testsFailed = 0;

testComplaintSubmissionAndIdGeneration();
testBlockBasedWardenAllocation();
testCompleteLifecycle();
testReopenWorkflow();
testExceptionHandling();
testCriticalComplaintEscalation();
testEarlyResolutionCancelsEscalation();
testConcurrentSubmissions();
testCsvStoragePersistence();

System.out.println("================================================================================");
System.out.printf("TEST SUMMARY: %d PASSED, %d FAILED (TOTAL: %d)\n",
testsPassed, testsFailed, (testsPassed + testsFailed));
System.out.println("================================================================================");

if (testsFailed > 0) {
throw new RuntimeException(testsFailed + " test(s) failed!");
}
}

private static void assertTrue(String testName, boolean condition, String message) {
if (condition) {
System.out.printf("  [PASS] %-40s\n", testName);
testsPassed++;
} else {
System.err.printf("  [FAIL] %-40s : %s\n", testName, message);
testsFailed++;
}
}

private static void assertEquals(String testName, Object expected, Object actual) {
boolean match = (expected == null && actual == null) || (expected != null && expected.equals(actual));
if (match) {
System.out.printf("  [PASS] %-40s\n", testName);
testsPassed++;
} else {
System.err.printf("  [FAIL] %-40s : Expected <%s> but got <%s>\n", testName, expected, actual);
testsFailed++;
}
}

private static GrievanceSystem createIsolatedSystem() {
Admin admin = new Admin("TEST_A1", "Test Admin", "Superintendent");
String scratchName = "data/test_scratch_" + System.currentTimeMillis() + "_" + (int) (Math.random() * 1000) + ".csv";
File scratchFile = new File(scratchName);
scratchFile.deleteOnExit();
StorageService dummyStorage = new StorageService(scratchName);
GrievanceSystem system = new GrievanceSystem(admin, dummyStorage);
system.registerWarden(new Warden("W_A", "Warden Alpha", "Block A"));
system.registerWarden(new Warden("W_B", "Warden Beta", "Block B"));
system.registerWarden(new Warden("W_C", "Warden Gamma", "Block C"));
return system;
}

private static void testComplaintSubmissionAndIdGeneration() {
GrievanceSystem system = createIsolatedSystem();
try {
Student s = new Student("S10", "Dev Test", "C-101", "9998887770");
Complaint c = system.submitComplaint(s, ComplaintCategory.ELECTRICAL, "Fan regulator broken");

assertTrue("Submission-NotNull", c != null, "Complaint was null");
assertTrue("ID-Format", c.getId().startsWith("CMP"), "ID format should start with CMP");
assertEquals("Initial-Status", ComplaintStatus.ASSIGNED, c.getStatus());
assertEquals("Category-Match", ComplaintCategory.ELECTRICAL, c.getCategory());
} catch (Exception e) {
assertTrue("Submission-Exception", false, e.getMessage());
}
}

private static void testBlockBasedWardenAllocation() {
GrievanceSystem system = createIsolatedSystem();
try {
Student sA = new Student("S11", "Student A", "A-201", "9991112220");
Student sB = new Student("S12", "Student B", "B-305", "9991112221");
Student sC = new Student("S13", "Student C", "C-410", "9991112222");

Complaint cA = system.submitComplaint(sA, ComplaintCategory.WATER, "Tap leak in A block");
Complaint cB = system.submitComplaint(sB, ComplaintCategory.HYGIENE, "Corridor dirty in B block");
Complaint cC = system.submitComplaint(sC, ComplaintCategory.MAINTENANCE, "Door lock jammed in C block");

assertEquals("Block-A-Assignment", "Block A", cA.getAssignedWarden().getAssignedBlock());
assertEquals("Block-B-Assignment", "Block B", cB.getAssignedWarden().getAssignedBlock());
assertEquals("Block-C-Assignment", "Block C", cC.getAssignedWarden().getAssignedBlock());
} catch (Exception e) {
assertTrue("Block-Allocation-Exception", false, e.getMessage());
}
}

private static void testCompleteLifecycle() {
GrievanceSystem system = createIsolatedSystem();
try {
Student s = new Student("S20", "Lifecycle Student", "A-105", "9990001111");
Complaint c = system.submitComplaint(s, ComplaintCategory.ELECTRICAL, "Flickering tube light");
String cid = c.getId();

// 1. In Progress
system.markInProgress(cid, "Electrician summoned with new tube");
assertEquals("Status-InProgress", ComplaintStatus.IN_PROGRESS, c.getStatus());
assertEquals("Remarks-Check", "Electrician summoned with new tube", c.getProgressRemarks());

// 2. Resolved
system.resolveComplaint(cid, "Replaced choke and LED starter");
assertEquals("Status-Resolved", ComplaintStatus.RESOLVED, c.getStatus());
assertTrue("ResolvedAt-Set", c.getResolvedAt() != null, "resolvedAt should be recorded");
assertEquals("ResolutionNotes-Check", "Replaced choke and LED starter", c.getResolutionNotes());

// 3. Confirmed by student
system.confirmResolution(cid, 5, "Fixed rapidly. Very bright now!");
assertEquals("Status-Confirmed", ComplaintStatus.CONFIRMED, c.getStatus());
assertEquals("Rating-Check", 5, c.getRating());
assertEquals("Feedback-Check", "Fixed rapidly. Very bright now!", c.getFeedbackComment());
} catch (Exception e) {
assertTrue("Lifecycle-Exception", false, e.getMessage());
}
}

private static void testReopenWorkflow() {
GrievanceSystem system = createIsolatedSystem();
try {
Student s = new Student("S30", "Reopen Student", "B-202", "9994445555");
Complaint c = system.submitComplaint(s, ComplaintCategory.WATER, "Shower not providing hot water");
String cid = c.getId();

system.resolveComplaint(cid, "Geyser element reset");
assertEquals("Status-BeforeReopen", ComplaintStatus.RESOLVED, c.getStatus());

system.reopenComplaint(cid, "Water turned cold again after 5 minutes");
assertEquals("Status-AfterReopen", ComplaintStatus.REOPENED, c.getStatus());
assertEquals("Reopen-Reason-Match", "Water turned cold again after 5 minutes", c.getReopenReason());
assertTrue("Reopen-IsResolvedFalse", !c.isResolved(), "Reopened complaint must not report as resolved");
} catch (Exception e) {
assertTrue("Reopen-Exception", false, e.getMessage());
}
}

private static void testExceptionHandling() {
GrievanceSystem system = createIsolatedSystem();

// 1. Complaint Not Found Exception
boolean notFoundCaught = false;
try {
system.getComplaintOrThrow("CMP_DOES_NOT_EXIST");
} catch (ComplaintNotFoundException e) {
notFoundCaught = true;
}
assertTrue("Exception-NotFound", notFoundCaught, "Should throw ComplaintNotFoundException for fake ID");

// 2. Already Resolved Exception
boolean alreadyResolvedCaught = false;
try {
Student s = new Student("S40", "Test", "C-301", "9000000000");
Complaint c = system.submitComplaint(s, ComplaintCategory.HYGIENE, "Trash can full");
system.resolveComplaint(c.getId(), "Emptied bin");
system.resolveComplaint(c.getId(), "Trying to resolve again");
} catch (ComplaintAlreadyResolvedException e) {
alreadyResolvedCaught = true;
} catch (Exception e) {
alreadyResolvedCaught = false;
}
assertTrue("Exception-AlreadyResolved", alreadyResolvedCaught, "Should throw ComplaintAlreadyResolvedException");

// 3. Null Category Exception
boolean invalidCatCaught = false;
try {
Student s = new Student("S41", "Test", "C-301", "9000000000");
system.submitComplaint(s, null, "Null category check");
} catch (InvalidCategoryException e) {
invalidCatCaught = true;
} catch (Exception e) {
invalidCatCaught = false;
}
assertTrue("Exception-InvalidCategory", invalidCatCaught, "Should throw InvalidCategoryException on null category");
}

private static void testCriticalComplaintEscalation() {
GrievanceSystem system = createIsolatedSystem();
try {
Student s = new Student("S50", "Emergency Student", "C-101", "9111111111");
// Set SLA to 400 milliseconds for fast unit testing
Complaint c = system.submitComplaint(s, ComplaintCategory.FIRE_SAFETY, "Sparking plug point in corridor", 400L);

assertTrue("Initial-NotEscalated", !c.isEscalated(), "Should not be escalated immediately");

// Sleep past SLA window
Thread.sleep(650);

assertTrue("Triggered-Escalated", c.isEscalated(), "Critical complaint should be auto-escalated past SLA");
assertEquals("Status-Escalated", ComplaintStatus.ESCALATED, c.getStatus());
} catch (Exception e) {
assertTrue("Escalation-Exception", false, e.getMessage());
}
}

private static void testEarlyResolutionCancelsEscalation() {
GrievanceSystem system = createIsolatedSystem();
try {
Student s = new Student("S51", "Security Student", "A-101", "9222222222");
// Set SLA to 800 milliseconds
Complaint c = system.submitComplaint(s, ComplaintCategory.SECURITY, "Lock missing from balcony door", 800L);

// Resolve within 200 ms
Thread.sleep(200);
system.resolveComplaint(c.getId(), "Fitted heavy duty padlock");

// Sleep past original SLA window
Thread.sleep(800);

assertTrue("Cancelled-NotEscalated", !c.isEscalated(), "Resolved complaint should cancel escalation");
assertEquals("Status-RemainsResolved", ComplaintStatus.RESOLVED, c.getStatus());
} catch (Exception e) {
assertTrue("Early-Resolution-Exception", false, e.getMessage());
}
}

private static void testConcurrentSubmissions() {
GrievanceSystem system = createIsolatedSystem();
int threads = 20;
ExecutorService pool = Executors.newFixedThreadPool(threads);
ConcurrentLinkedQueue<Complaint> generated = new ConcurrentLinkedQueue<>();
CountDownLatch latch = new CountDownLatch(threads);

for (int i = 0; i < threads; i++) {
final int idIdx = i;
pool.submit(() -> {
try {
Student s = new Student("S" + idIdx, "Student " + idIdx, "C-" + (100 + idIdx), "98000000" + (idIdx % 100));
Complaint c = system.submitComplaint(s, ComplaintCategory.ELECTRICAL, "Concurrent issue " + idIdx);
generated.add(c);
} catch (Exception e) {
e.printStackTrace();
} finally {
latch.countDown();
}
});
}

try {
latch.await(5, TimeUnit.SECONDS);
pool.shutdown();

assertEquals("Concurrent-Count", threads, generated.size());
long uniqueIds = generated.stream().map(Complaint::getId).distinct().count();
assertEquals("Concurrent-Unique-IDs", (long) threads, uniqueIds);
} catch (InterruptedException e) {
assertTrue("Concurrent-Timeout", false, "Latch wait timed out");
}
}

private static void testCsvStoragePersistence() {
String testCsv = "data/test_persistence_verify.csv";
new File(testCsv).deleteOnExit();

StorageService storage = new StorageService(testCsv);
Admin admin = new Admin("A1", "Admin Sharma");
GrievanceSystem sys1 = new GrievanceSystem(admin, storage);
Warden w = new Warden("W1", "Mr. Verma", "Block C");
sys1.registerWarden(w);

try {
Student s = new Student("S99", "Persistence Student", "C-302", "9988776655");
Complaint c = sys1.submitComplaint(s, ComplaintCategory.WATER, "Persistent pipe leakage");
sys1.markInProgress(c.getId(), "Plumber en route");
sys1.resolveComplaint(c.getId(), "Fixed leaking joint with Teflon tape");
sys1.confirmResolution(c.getId(), 5, "Great job!");

// Re-instantiate system reading same file
GrievanceSystem sys2 = new GrievanceSystem(admin, storage);
sys2.registerWarden(w);

Complaint loaded = sys2.getComplaintOrThrow(c.getId());
assertEquals("Loaded-ID", c.getId(), loaded.getId());
assertEquals("Loaded-Status", ComplaintStatus.CONFIRMED, loaded.getStatus());
assertEquals("Loaded-Rating", 5, loaded.getRating());
assertEquals("Loaded-Feedback", "Great job!", loaded.getFeedbackComment());
assertEquals("Loaded-ResolutionNotes", "Fixed leaking joint with Teflon tape", loaded.getResolutionNotes());
} catch (Exception e) {
assertTrue("Persistence-Exception", false, e.getMessage());
} finally {
new File(testCsv).delete();
}
}
}
