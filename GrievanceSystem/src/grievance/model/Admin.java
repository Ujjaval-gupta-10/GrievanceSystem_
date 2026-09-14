package grievance.model;

public class Admin {
    private final String id;
    private final String name;
    private final String designation;

    public Admin(String id, String name) {
        this(id, name, "Chief Hostel Administrator");
    }

    public Admin(String id, String name, String designation) {
        this.id = id;
        this.name = name;
        this.designation = designation;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getDesignation() { return designation; }

    @Override
    public String toString() {
        return "Admin{id='" + id + "', name='" + name + "', role='" + designation + "'}";
    }
}
