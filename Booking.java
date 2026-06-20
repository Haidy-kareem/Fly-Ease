package oopflightbookingsystem.src;

// No longer need ArrayList, List, Collectors, IntStream for multiple passengers

public class Booking {

    private int bookingId;
    private int customerId;
    private int flightId;

    // Composition: Booking has one Passenger
    private int passengerId; // Stores the ID of the passenger for persistence
    private transient Passenger passenger; // The actual composed Passenger object, resolved by BookingSystem
    private String seatSelection; // Single seat selection for this passenger

    private String status;
    private String paymentStatus;
    private double totalPrice;

    private static final String DELIMITER = "|";

    // Constructor for creating a new booking with a Passenger object
    public Booking(int bookingId, int customerId, int flightId, Passenger passenger, String seatSelection) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.passenger = passenger;
        if (passenger != null) {
            this.passengerId = passenger.getPassengerId();
        }
        this.seatSelection = seatSelection;
        this.status = "Reserved"; // Default status
        this.paymentStatus = "Pending"; // Default payment status
        this.totalPrice = 0.0; // Should be calculated after creation
    }

    // Constructor used by fromFileString (Passenger object not yet resolved)
    // This constructor will be primarily used during loading from file
    public Booking(int bookingId, int customerId, int flightId, int passengerId, String seatSelection, String status, String paymentStatus, double totalPrice) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.flightId = flightId;
        this.passengerId = passengerId;
        this.passenger = null; // Passenger object to be resolved later by BookingSystem
        this.seatSelection = seatSelection;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getBookingId() {
        return bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public int getFlightId() {
        return flightId;
    }

    public Passenger getPassenger() {
        // Ensure passenger is resolved if ID exists and object is null
        // This might be better handled by BookingSystem when a Booking object is retrieved.
        // For now, this getter just returns the current state.
        return passenger;
    }

    public int getPassengerId() { // Returns the ID of the passenger
        return passengerId;
    }

    public String getSeatSelection() {
        return seatSelection;
    }

    public String getStatus() {
        return status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Setters
    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
        if (passenger != null) {
            this.passengerId = passenger.getPassengerId(); // Keep passengerId consistent
        } else {
            this.passengerId = 0; // Or some indicator for no passenger
        }
    }

    public void setSeatSelection(String seatSelection) {
        this.seatSelection = seatSelection;
    }

    public void setStatus(String status) {
        if (status != null && !status.isEmpty()) {
            this.status = status;
        }
    }

    public void setPaymentStatus(String paymentStatus) {
        if (paymentStatus != null && !paymentStatus.isEmpty()) {
            this.paymentStatus = paymentStatus;
        }
    }

    public void setTotalPrice(double totalPrice) {
        if (totalPrice >= 0) {
            this.totalPrice = totalPrice;
        } else {
            System.err.println("Warning: Attempted to set negative total price for booking " + bookingId + ". Price not changed.");
        }
    }

    // Business Logic Methods
    public void calculateTotalPrice(BookingSystem bookingSystem) {
        Flight flight = bookingSystem.findFlightById(this.flightId);
        if (flight == null) {
            System.err.println("Error: Flight not found for calculating total price for booking " + bookingId);
            this.totalPrice = 0.0;
            return;
        }
        if (this.seatSelection == null || this.seatSelection.isEmpty()) {
            System.err.println("Error: Seat selection is missing for booking " + bookingId + ". Price cannot be calculated.");
            this.totalPrice = 0.0;
            return;
        }

        double classPrice = flight.getPrice(this.seatSelection);
        if (classPrice >= 0) {
            this.totalPrice = classPrice;
        } else {
            System.err.println("Warning: Price not found for seat class \"" + this.seatSelection + "\" in booking " + bookingId + " on flight " + flight.getFlightNumberStr());
            this.totalPrice = 0.0; // Default to 0 if price is invalid
        }
    }

    public void confirmBooking() {
        if (!"Confirmed".equalsIgnoreCase(status)) {
            setStatus("Confirmed");
            System.out.println("Booking " + bookingId + " has been confirmed.");
        } else {
            System.out.println("Booking " + bookingId + " is already confirmed.");
        }
    }

    public void cancelBooking() {
        if (!"Cancelled".equalsIgnoreCase(status)) {
            setStatus("Cancelled");
            System.out.println("Booking " + bookingId + " has been cancelled.");
        } else {
            System.out.println("Booking " + bookingId + " is already cancelled.");
        }
    }

    public String getItinerary(BookingSystem bookingSystem) {
        StringBuilder sb = new StringBuilder();
        Flight flight = bookingSystem.findFlightById(this.flightId);
        User customer = bookingSystem.findUserById(this.customerId);
        // Ensure passenger object is resolved for itinerary generation
        Passenger currentPassenger = this.passenger;
        if (currentPassenger == null && this.passengerId > 0 && bookingSystem != null) {
            currentPassenger = bookingSystem.findPassengerById(this.passengerId);
        }

        sb.append("----- Itinerary for Booking ").append(bookingId).append(" -----\n");
        if (customer != null) {
            sb.append("Customer: ").append(customer.getName()).append(" (ID: ").append(customerId).append(")\n");
        }
        if (flight != null) {
            sb.append("Flight: ").append(flight.getFlightNumberStr()).append(" from ").append(flight.getOrigin()).append(" to ").append(flight.getDestination()).append("\n");
            sb.append("Departure: ").append(flight.getDepartureTime()).append(", Arrival: ").append(flight.getArrivalTime()).append("\n");
        }
        sb.append("Status: ").append(status).append("\n");
        sb.append("Payment Status: ").append(paymentStatus).append("\n");
        sb.append(String.format("Total Price: $%.2f\n", totalPrice));
        
        sb.append("Passenger Details:\n");
        if (currentPassenger != null) { 
            sb.append("  Name: ").append(currentPassenger.getName()).append(" (ID: ").append(currentPassenger.getPassengerId()).append(")\n");
            sb.append("  Passport: ").append(currentPassenger.getPassportNumber()).append("\n");
            sb.append("  Seat Class: ").append(this.seatSelection).append("\n");
        } else if (this.passengerId > 0) { 
            sb.append("  Passenger ID: ").append(this.passengerId).append("\n");
            sb.append("  Seat Class: ").append(this.seatSelection).append("\n");
            sb.append("  (Full passenger details could not be loaded/resolved at this time)\n");
        } else {
            sb.append("  No passenger information available.\n");
        }
        sb.append("-----------------------------\n");
        return sb.toString();
    }

    // Added getDetailedDescription method as required by BookingSystem
    public String getDetailedDescription(BookingSystem bookingSystem) {
        // This can be similar to getItinerary or a more concise summary
        StringBuilder sb = new StringBuilder();
        Flight flight = (bookingSystem != null) ? bookingSystem.findFlightById(this.flightId) : null;
        User customer = (bookingSystem != null) ? bookingSystem.findUserById(this.customerId) : null;
        Passenger currentPassenger = this.passenger;
        if (currentPassenger == null && this.passengerId > 0 && bookingSystem != null) {
            currentPassenger = bookingSystem.findPassengerById(this.passengerId);
        }

        sb.append("Booking ID: ").append(bookingId).append("\n");
        sb.append("  Customer: ");
        if (customer != null) {
            sb.append(customer.getName()).append(" (ID: ").append(customerId).append(")\n");
        } else {
            sb.append("ID: ").append(customerId).append(" (Details not loaded)\n");
        }
        sb.append("  Flight: ");
        if (flight != null) {
            sb.append(flight.getFlightNumberStr()).append(" (").append(flight.getOrigin()).append(" -> ").append(flight.getDestination()).append(")\n");
        } else {
            sb.append("ID: ").append(flightId).append(" (Details not loaded)\n");
        }
        sb.append("  Passenger: ");
        if (currentPassenger != null) {
            sb.append(currentPassenger.getName()).append(" (Passport: ").append(currentPassenger.getPassportNumber()).append(")\n");
        } else if (this.passengerId > 0) {
            sb.append("ID: ").append(this.passengerId).append(" (Details not loaded)\n");
        } else {
            sb.append("N/A\n");
        }
        sb.append("  Seat: ").append(seatSelection != null ? seatSelection : "N/A").append("\n");
        sb.append("  Status: ").append(status).append("\n");
        sb.append("  Payment Status: ").append(paymentStatus).append("\n");
        sb.append(String.format("  Total Price: $%.2f\n", totalPrice));
        return sb.toString();
    }

    // File Operations
    public String toFileString() {
        return String.join(DELIMITER,
                String.valueOf(bookingId),
                String.valueOf(customerId),
                String.valueOf(flightId),
                String.valueOf(passengerId), 
                seatSelection != null ? seatSelection : "",
                status != null ? status : "",
                paymentStatus != null ? paymentStatus : "",
                String.format("%.2f", totalPrice).replace(",", ".") 
        );
    }

    public static Booking fromFileString(String line) {
        String[] parts = line.split("\\" + DELIMITER, -1);
        if (parts.length == 8) {
            try {
                int bookingIdFromFile = Integer.parseInt(parts[0]);
                int customerIdFromFile = Integer.parseInt(parts[1]);
                int flightIdFromFile = Integer.parseInt(parts[2]);
                int pIdFromFile = Integer.parseInt(parts[3]);
                String sSelectionFromFile = parts[4];
                String statusFromFile = parts[5];
                String paymentStatusFromFile = parts[6];
                double totalPriceFromFile = Double.parseDouble(parts[7].replace(",", "."));

                return new Booking(bookingIdFromFile, customerIdFromFile, flightIdFromFile, pIdFromFile, sSelectionFromFile, statusFromFile, paymentStatusFromFile, totalPriceFromFile);
            } catch (NumberFormatException e) {
                System.err.println("Warning: Could not parse numeric data for booking from line: " + line + ". Error: " + e.getMessage());
                return null;
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Warning: Malformed booking data line (not enough parts): " + line + ". Error: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Warning: Invalid booking data line format (expected 8 parts, got " + parts.length + "): " + line);
            return null;
        }
    }

    @Override
    public String toString() {
        String passengerName = (passenger != null) ? passenger.getName() : ("ID:" + (passengerId > 0 ? passengerId : "N/A"));
        return "Booking{" +
               "ID=" + bookingId +
               ", CustID=" + customerId +
               ", FlightID=" + flightId +
               ", Passenger=" + passengerName +
               ", Seat=" + (seatSelection != null ? seatSelection : "N/A") +
               ", Status=\t" + status + 	"\t" +
               ", Payment=\t" + paymentStatus + 	"\t" +
               ", Price=" + String.format("$%.2f", totalPrice) +
               "}";
    }
}

