package oopflightbookingsystem.src;

// Passenger class representing passenger information
public class Passenger {

    private int passengerId;
    private String name;
    private String passportNumber;
    private String dateOfBirth; // Format: YYYY-MM-DD
    private String specialRequests;

    // Delimiter for file storage
    private static final String DELIMITER = "|";

    // Constructor
    public Passenger(int passengerId, String name, String passportNumber, String dateOfBirth, String specialRequests) {
        this.passengerId = passengerId;
        this.name = name;
        this.passportNumber = passportNumber;
        this.dateOfBirth = dateOfBirth;
        this.specialRequests = (specialRequests != null) ? specialRequests : ""; // Ensure not null
    }

    // Getters
    public int getPassengerId() {
        return passengerId;
    }

    public String getName() {
        return name;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    // Setters / Modifiers (as per PDF: updateInfo)
    public void updateInfo(String name, String passportNumber, String dateOfBirth, String specialRequests) {
        if (name != null && !name.isEmpty()) {
            this.name = name;
        }
        if (passportNumber != null && !passportNumber.isEmpty()) {
            this.passportNumber = passportNumber;
        }
        if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
            // Add date format validation if needed
            this.dateOfBirth = dateOfBirth;
        }
        if (specialRequests != null) { // Allow empty string for requests
            this.specialRequests = specialRequests;
        }
        System.out.println("Passenger information updated for ID: " + passengerId);
    }

    // PDF mentions getPassengerDetails() - this can be achieved via getters or toString()
    public String getPassengerDetails() {
        return toString(); // Simple implementation using existing toString
    }

    // --- File Representation ---
    public String toFileString() {
        return String.join(DELIMITER,
                String.valueOf(passengerId),
                name != null ? name : "",
                passportNumber != null ? passportNumber : "",
                dateOfBirth != null ? dateOfBirth : "",
                specialRequests != null ? specialRequests : ""
        );
    }

    // Static method to create a Passenger object from a file string
    public static Passenger fromFileString(String line) {
        String[] parts = line.split("\\" + DELIMITER, -1);
        // Expected format: passengerId|name|passportNumber|dateOfBirth|specialRequests
        if (parts.length == 5) {
            try {
                int passengerId = Integer.parseInt(parts[0]);
                String name = parts[1];
                String passportNumber = parts[2];
                String dateOfBirth = parts[3];
                String specialRequests = parts[4];

                return new Passenger(passengerId, name, passportNumber, dateOfBirth, specialRequests);

            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse passenger ID for data line: " + line + ". Error: " + e.getMessage());
                return null;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Warning: Malformed passenger data line (missing fields): " + line + ". Error: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Warning: Invalid passenger data line format: " + line);
            return null;
        }
    }

    // --- Overridden Methods ---
    @Override
    public String toString() {
        return "Passenger{" +
               "ID=" + passengerId +
               ", Name=	" + name + 	"\t" +
               ", Passport=	" + passportNumber + 	"\t" +
               ", DOB=	" + dateOfBirth +
               ", Requests=	'" + specialRequests + "'" +
               "}";
    }

    // equals and hashCode based on passengerId for uniqueness
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Passenger passenger = (Passenger) o;
        return passengerId == passenger.passengerId;
    }

  
}

