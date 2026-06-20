package oopflightbookingsystem.src;

public class Agent extends User {
    private String department;
    private double commissionRate;

    public Agent(int userId, String username, String password, String name,
                 String email, String contactInfo, String department, double commissionRate) {
        super(userId, username, password, name, email, contactInfo, "Agent");
        this.department = department;
        this.commissionRate = commissionRate >= 0 ? commissionRate : 0.0;
    }

    public String getDepartment() { return department; }
    public double getCommissionRate() { return commissionRate; }
    public void setDepartment(String department) {
        if (department != null && !department.trim().isEmpty()) this.department = department.trim();
    }
    public void setCommissionRate(double rate) {
        if (rate >= 0) this.commissionRate = rate;
    }

    @Override
    public String toFileString() {
        return String.join(DELIMITER, getBaseFileString(),
                department != null ? department : "",
                String.valueOf(commissionRate));
    }

    public static Agent fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 10 && "Agent".equals(parts[6])) {
            try {
                int userId = Integer.parseInt(parts[0]);
                double commission = 0.0;
                try { commission = Double.parseDouble(parts[9]); } catch (Exception ignored) {}
                Agent agent = new Agent(userId, parts[1], parts[2], parts[3], parts[4], parts[5], parts[8], commission);
                agent.setActive(Boolean.parseBoolean(parts[7]));
                return agent;
            } catch (Exception e) {
                System.err.println("Warning: Could not parse agent: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String getDetailedDescription() {
        return super.getDetailedDescription() +
               "  Department: " + (department != null ? department : "N/A") + "\n" +
               String.format("  Commission Rate: %.2f%%\n", commissionRate * 100);
    }
}
