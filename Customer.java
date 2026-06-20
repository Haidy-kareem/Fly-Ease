package oopflightbookingsystem.src;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class Customer extends User {
    private String address;
    private ArrayList<Integer> bookingHistory;
    private ArrayList<String> preferences;

    public Customer(int userId, String username, String password, String name,
                    String email, String contactInfo, String address) {
        super(userId, username, password, name, email, contactInfo, "Customer");
        this.address = address;
        this.bookingHistory = new ArrayList<>();
        this.preferences = new ArrayList<>();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) {
        if (address != null && !address.trim().isEmpty()) this.address = address.trim();
    }

    public ArrayList<Integer> getBookingHistory() { return new ArrayList<>(bookingHistory); }
    public void addBookingToHistory(int bookingId) {
        if (!this.bookingHistory.contains(bookingId)) this.bookingHistory.add(bookingId);
    }

    @Override
    public String toFileString() {
        String baseString = getBaseFileString();
        String historyString = bookingHistory.stream().map(String::valueOf).collect(Collectors.joining(","));
        String preferencesString = preferences.stream().filter(p -> p != null && !p.isEmpty()).collect(Collectors.joining(";"));
        return String.join(DELIMITER, baseString,
                address != null ? address : "",
                historyString,
                preferencesString);
    }

    public static Customer fromFileString(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length >= 11 && "Customer".equals(parts[6])) {
            try {
                int userId = Integer.parseInt(parts[0]);
                Customer customer = new Customer(userId, parts[1], parts[2], parts[3],
                        parts[4], parts[5], parts[8]);
                customer.setActive(Boolean.parseBoolean(parts[7]));
                if (parts.length > 9 && !parts[9].isEmpty()) {
                    for (String idStr : parts[9].split(",")) {
                        if (!idStr.trim().isEmpty())
                            customer.addBookingToHistory(Integer.parseInt(idStr.trim()));
                    }
                }
                return customer;
            } catch (Exception e) {
                System.err.println("Warning: Could not parse customer: " + line);
                return null;
            }
        }
        return null;
    }

    @Override
    public String getDetailedDescription() {
        return super.getDetailedDescription() +
               "  Address: " + (address != null ? address : "N/A") + "\n";
    }
}
