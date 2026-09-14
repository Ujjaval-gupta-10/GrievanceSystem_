package grievance.model;

public class Warden {
    private final String id;
    private final String name;
    private final String assignedBlock;
    private final String contactNumber;

    public Warden(String id, String name, String assignedBlock) {
        this(id, name, assignedBlock, "N/A");
    }

    public Warden(String id, String name, String assignedBlock, String contactNumber) {
        this.id = id;
        this.name = name;
        this.assignedBlock = assignedBlock;
        this.contactNumber = contactNumber;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAssignedBlock() { return assignedBlock; }
    public String getContactNumber() { return contactNumber; }

    @Override
    public String toString() {
        return "Warden{id='" + id + "', name='" + name + "', block='" + assignedBlock + "'}";
    }
}
