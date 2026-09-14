package grievance.service;

import grievance.model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * StorageService handles persistence of complaints to a CSV file.
 * Thread-safe and uses pure Java standard library (no external dependencies).
 */
public class StorageService {

    private final Path filePath;
    private static final String CSV_HEADER = "id,studentId,studentName,studentRoom,studentContact,category,description,status,submittedAt,resolvedAt,wardenId,wardenName,wardenBlock,escalated,progressRemarks,resolutionNotes,rating,feedbackComment,reopenReason";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public StorageService(String dataFilePath) {
        this.filePath = Paths.get(dataFilePath);
        ensureFileExists();
    }

    private synchronized void ensureFileExists() {
        try {
            if (filePath.getParent() != null && !Files.exists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (!Files.exists(filePath)) {
                Files.writeString(filePath, CSV_HEADER + System.lineSeparator(), StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            System.err.println("[StorageService] Warning: Unable to initialize storage file: " + e.getMessage());
        }
    }

    public synchronized void saveComplaints(Collection<Complaint> complaints) {
        try {
            ensureFileExists();
            StringBuilder sb = new StringBuilder();
            sb.append(CSV_HEADER).append(System.lineSeparator());

            for (Complaint c : complaints) {
                sb.append(toCsvRecord(c)).append(System.lineSeparator());
            }

            Files.writeString(filePath, sb.toString(), StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.err.println("[StorageService] Error saving complaints to disk: " + e.getMessage());
        }
    }

    public synchronized List<Complaint> loadComplaints(Map<String, Warden> wardens) {
        List<Complaint> list = new ArrayList<>();
        if (!Files.exists(filePath)) {
            return list;
        }

        try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
            String line = reader.readLine(); // Header
            if (line == null) {
                return list;
            }

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Complaint c = parseComplaint(line, wardens);
                if (c != null) {
                    list.add(c);
                }
            }
        } catch (IOException e) {
            System.err.println("[StorageService] Error loading complaints from disk: " + e.getMessage());
        }
        return list;
    }

    private String toCsvRecord(Complaint c) {
        Student s = c.getSubmittedBy();
        Warden w = c.getAssignedWarden();

        return String.join(",",
                csvEscape(c.getId()),
                csvEscape(s != null ? s.getId() : ""),
                csvEscape(s != null ? s.getName() : ""),
                csvEscape(s != null ? s.getRoomNumber() : ""),
                csvEscape(s != null ? s.getContact() : ""),
                csvEscape(c.getCategory().name()),
                csvEscape(c.getDescription()),
                csvEscape(c.getStatus().name()),
                csvEscape(c.getSubmittedAt() != null ? c.getSubmittedAt().format(FORMATTER) : ""),
                csvEscape(c.getResolvedAt() != null ? c.getResolvedAt().format(FORMATTER) : ""),
                csvEscape(w != null ? w.getId() : ""),
                csvEscape(w != null ? w.getName() : ""),
                csvEscape(w != null ? w.getAssignedBlock() : ""),
                csvEscape(String.valueOf(c.isEscalated())),
                csvEscape(c.getProgressRemarks()),
                csvEscape(c.getResolutionNotes()),
                csvEscape(String.valueOf(c.getRating())),
                csvEscape(c.getFeedbackComment()),
                csvEscape(c.getReopenReason())
        );
    }

    private Complaint parseComplaint(String line, Map<String, Warden> wardens) {
        try {
            List<String> cols = parseCsvLine(line);
            if (cols.size() < 19) {
                return null;
            }

            String id = cols.get(0);
            String studentId = cols.get(1);
            String studentName = cols.get(2);
            String studentRoom = cols.get(3);
            String studentContact = cols.get(4);
            Student student = new Student(studentId, studentName, studentRoom, studentContact);

            ComplaintCategory category = ComplaintCategory.valueOf(cols.get(5));
            String description = cols.get(6);
            ComplaintStatus status = ComplaintStatus.valueOf(cols.get(7));

            LocalDateTime submittedAt = cols.get(8).isEmpty() ? LocalDateTime.now() : LocalDateTime.parse(cols.get(8), FORMATTER);
            LocalDateTime resolvedAt = cols.get(9).isEmpty() ? null : LocalDateTime.parse(cols.get(9), FORMATTER);

            String wardenId = cols.get(10);
            String wardenName = cols.get(11);
            String wardenBlock = cols.get(12);
            Warden warden = null;
            if (!wardenId.isEmpty()) {
                if (wardens != null && wardens.containsKey(wardenId)) {
                    warden = wardens.get(wardenId);
                } else {
                    warden = new Warden(wardenId, wardenName, wardenBlock);
                }
            }

            boolean escalated = Boolean.parseBoolean(cols.get(13));
            String progressRemarks = cols.get(14);
            String resolutionNotes = cols.get(15);
            int rating = 0;
            try {
                rating = Integer.parseInt(cols.get(16));
            } catch (NumberFormatException ignored) {}

            String feedbackComment = cols.get(17);
            String reopenReason = cols.get(18);

            return new Complaint(id, student, category, description, status, submittedAt, resolvedAt,
                    warden, escalated, progressRemarks, resolutionNotes, rating, feedbackComment, reopenReason);
        } catch (Exception e) {
            System.err.println("[StorageService] Failed to parse line: " + line + " -> " + e.getMessage());
            return null;
        }
    }

    private static String csvEscape(String text) {
        if (text == null) return "\"\"";
        String escaped = text.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    public static List<String> parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    values.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        values.add(sb.toString());
        return values;
    }
}
