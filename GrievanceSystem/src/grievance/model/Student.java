package grievance.model;

public class Student {
    private final String id;
    private final String name;
    private final String roomNumber;
    private final String contact;

    public Student(String id, String name, String roomNumber, String contact) {
        this.id = id;
        this.name = name;
        this.roomNumber = roomNumber;
        this.contact = contact;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getRoomNumber() { return roomNumber; }
    public String getContact() { return contact; }

    /**
     * Determines hostel block from room number (e.g., 'C-204' -> 'Block C', 'A102' -> 'Block A').
     */
    public String getBlock() {
        if (roomNumber == null || roomNumber.isBlank()) {
            return "General";
        }
        String upper = roomNumber.trim().toUpperCase();
        if (upper.startsWith("BLOCK ")) {
            String[] parts = upper.split("\\s+");
            return parts.length > 1 ? "Block " + parts[1] : "General";
        }
        char firstChar = upper.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            return "Block " + firstChar;
        }
        return "General";
    }

    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', room='" + roomNumber + "', block='" + getBlock() + "'}";
    }
}
