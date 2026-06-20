package oopflightbookingsystem.src;

public class Administrator extends User {
    private String securityLevel;

    public Administrator(int userId, String username, String password, String name,
                         String email, String contactInfo, String securityLevel) {
        super(userId, username, password, name, email, contactInfo, "Administrator");
        this.securityLevel = (securityLevel != null && !securityLevel.trim().isEmpty()) ? securityLevel.trim() : "Standard";
    }

    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) {
        if (securityLevel != null && !securityLevel.trim().isEmpty()) this.securityLevel = securityLevel.trim();
    }

    @Override
    public String toFileString() {
        return String.join(DELIMITER, getBaseFileString(), securityLevel != null ? securityLevel : "");
    }

    public static Administrator fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 9 && "Administrator".equals(parts[6])) {
            try {
                int userId = Integer.parseInt(parts[0]);
                Administrator admin = new Administrator(userId, parts[1], parts[2], parts[3], parts[4], parts[5], parts[8]);
                admin.setActive(Boolean.parseBoolean(parts[7]));
                return admin;
            } catch (Exception e) {
                System.err.println("Warning: Could not parse administrator: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String getDetailedDescription() {
        return super.getDetailedDescription() +
               "  Security Level: " + (securityLevel != null ? securityLevel : "N/A") + "\n";
    }
}
