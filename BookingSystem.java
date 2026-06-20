package oopflightbookingsystem.src;

import javax.swing.JTextArea; // Added for GUI helper methods
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class BookingSystem {

    private FileManager fileManager;
    private ArrayList<User> users;
    private ArrayList<Flight> flights;
    private ArrayList<Booking> bookings;
    private ArrayList<Passenger> passengers;
    private ArrayList<Payment> payments; // In-memory storage for payments during session

    private int nextUserId = 1;
    private int nextFlightId = 1;
    private int nextBookingId = 1;
    private int nextPassengerId = 1;
    private int nextPaymentId = 1;

    private User currentUser;

    public BookingSystem(String dataDirectoryPath) {
        this.fileManager = new FileManager(dataDirectoryPath);
        this.users = fileManager.loadUsers();
        this.flights = fileManager.loadFlights();
        this.passengers = fileManager.loadPassengers(); 
        this.bookings = fileManager.loadBookings(); 
        resolveBookingPassengers(); 
        this.payments = new ArrayList<>(); // Initialize payments list
        this.currentUser = null;
        initializeIdCounters();
    }
    
    private void resolveBookingPassengers() {
        if (this.bookings != null && this.passengers != null) {
            for (Booking booking : this.bookings) {
                if (booking != null && booking.getPassengerId() > 0 && booking.getPassenger() == null) {
                    Passenger passenger = findPassengerById(booking.getPassengerId());
                    if (passenger != null) {
                        booking.setPassenger(passenger);
                    } else {
                        System.err.println("Warning: Could not resolve passenger with ID " + booking.getPassengerId() + " for booking ID " + booking.getBookingId());
                    }
                }
            }
        }
    }

    private void initializeIdCounters() {
        if (users != null && !users.isEmpty()) {
            nextUserId = users.stream().mapToInt(User::getUserId).max().orElse(0) + 1;
        }
        if (flights != null && !flights.isEmpty()) {
            nextFlightId = flights.stream().mapToInt(Flight::getFlightId).max().orElse(0) + 1;
        }
        if (bookings != null && !bookings.isEmpty()) {
            nextBookingId = bookings.stream().mapToInt(Booking::getBookingId).max().orElse(0) + 1;
        }
        if (passengers != null && !passengers.isEmpty()) {
            nextPassengerId = passengers.stream().mapToInt(Passenger::getPassengerId).max().orElse(0) + 1;
        }
        nextPaymentId = 1; 
    }

    public void saveData() {
        fileManager.saveUsers(users);
        fileManager.saveFlights(flights);
        fileManager.savePassengers(passengers); 
        fileManager.saveBookings(bookings); 
        System.out.println("\n--- All data saved successfully. ---");
    }

    private int generateUserId() { return nextUserId++; }
    private int generateFlightId() { return nextFlightId++; }
    private int generateBookingId() { return nextBookingId++; }
    private int generatePassengerId() { return nextPassengerId++; }
    private int generatePaymentId() { return nextPaymentId++; }

    public User findUserById(int userId) {
        for (User user : users) {
            if (user.getUserId() == userId) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String username) {
        if (username == null || username.isEmpty()) return null;
        for (User user : users) {
            if (username.equalsIgnoreCase(user.getUsername())) {
                return user;
            }
        }
        return null;
    }

    public Flight findFlightById(int flightId) {
        for (Flight flight : flights) {
            if (flight.getFlightId() == flightId) {
                return flight;
            }
        }
        return null;
    }

    public Flight findFlightByNumberStr(String flightNumberStr) {
        if (flightNumberStr == null || flightNumberStr.isEmpty()) return null;
        for (Flight flight : flights) {
            if (flightNumberStr.equalsIgnoreCase(flight.getFlightNumberStr())) {
                return flight;
            }
        }
        return null;
    }

    public Booking findBookingById(int bookingId) {
        for (Booking booking : bookings) {
            if (booking.getBookingId() == bookingId) {
                if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
                    Passenger p = findPassengerById(booking.getPassengerId());
                    if (p != null) booking.setPassenger(p);
                }
                return booking;
            }
        }
        return null;
    }

    public Passenger findPassengerById(int passengerId) {
        for (Passenger passenger : passengers) {
            if (passenger.getPassengerId() == passengerId) {
                return passenger;
            }
        }
        return null;
    }

    public Passenger findPassengerByPassport(String passportNumber) {
        if (passportNumber == null || passportNumber.isEmpty()) return null;
        for (Passenger passenger : passengers) {
            if (passportNumber.equals(passenger.getPassportNumber())) {
                return passenger;
            }
        }
        return null;
    }

    public boolean login(String username, String password) {
        User user = findUserByUsername(username);
        if (user != null && user.checkPassword(password) && user.isActive()) { 
            this.currentUser = user;
            System.out.println("\nWelcome, " + user.getName() + " (" + user.getRole() + ")!");
            return true;
        } else if (user != null && !user.isActive()) {
            System.out.println("\nError: Account for user \"" + username + "\" is inactive. Please contact an administrator.");
            this.currentUser = null;
            return false;
        } else {
            System.out.println("\nError: Invalid username or password.");
            this.currentUser = null;
            return false;
        }
    }

    public void logout() {
        if (this.currentUser != null) {
            System.out.println("\nLogging out " + this.currentUser.getName() + ".");
            this.currentUser = null;
        } else {
            System.out.println("No user currently logged in.");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean registerUser(String username, String password, String name, String email, String contactInfo, String role, String specificInfo1, String specificInfo2) {
        if (findUserByUsername(username) != null) {
            System.out.println("\nError: Username \"" + username + "\" already exists.");
            return false;
        }
        if (password == null || password.length() < 6 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*[0-9].*")) {
            System.out.println("\nError: Password must be at least 6 characters long and contain at least one letter and one number.");
            return false;
        }

        int userId = generateUserId();
        User newUser = null;

        switch (role) {
            case "Customer":
                newUser = new Customer(userId, username, password, name, email, contactInfo, specificInfo1);
                break;
            case "Agent":
                try {
                    double commission = Double.parseDouble(specificInfo2);
                    newUser = new Agent(userId, username, password, name, email, contactInfo, specificInfo1, commission);
                } catch (NumberFormatException e) {
                    System.out.println("\nError: Invalid commission format for Agent. Must be a number.");
                    return false;
                }
                break;
            case "Administrator":
                newUser = new Administrator(userId, username, password, name, email, contactInfo, specificInfo1);
                break;
            default:
                System.out.println("\nError: Unknown user role for registration.");
                return false;
        }

        if (newUser != null) {
            users.add(newUser);
            fileManager.saveUsers(users);
            System.out.println("\n" + role + " \"" + username + "\" registered successfully with ID " + userId + ".");
            return true;
        }
        return false;
    }
    
    public boolean adminCreateUser(String username, String password, String name, String email, String contactInfo, String role, String specificInfo1, String specificInfo2) {
        if (!(currentUser instanceof Administrator)) {
            System.out.println("\nError: Only administrators can create users through this panel.");
            return false;
        }
        return registerUser(username, password, name, email, contactInfo, role, specificInfo1, specificInfo2);
    }

    // Overloaded addFlight to handle specific Domestic/International parameters from GUI
    public Flight addFlight(String flightNumberStr, String airline, String origin, String destination,
                            String departureTime, String arrivalTime, String flightType,
                            String[] seatClasses, int[] totalSeats, double[] prices,
                            boolean isIntraGov, String passportNote, boolean visaRequired) {
        if (findFlightByNumberStr(flightNumberStr) != null) {
            System.out.println("\nError: Flight number " + flightNumberStr + " already exists.");
            return null;
        }
        if (flightNumberStr == null || flightNumberStr.isEmpty() || airline == null || airline.isEmpty() || 
            origin == null || origin.isEmpty() || destination == null || destination.isEmpty() || 
            departureTime == null || departureTime.isEmpty() || arrivalTime == null || arrivalTime.isEmpty() || 
            flightType == null || flightType.isEmpty() || seatClasses == null || seatClasses.length == 0 || 
            totalSeats == null || totalSeats.length != seatClasses.length || prices == null || prices.length != seatClasses.length) {
            System.out.println("\nError: Missing or invalid flight details provided.");
            return null;
        }

        int flightId = generateFlightId();
        Flight newFlight;
        
        if ("Domestic".equalsIgnoreCase(flightType)) {
            newFlight = new DomesticFlight(flightId, flightNumberStr, airline, origin, destination, departureTime, arrivalTime, seatClasses, totalSeats, prices, isIntraGov);
        } else if ("International".equalsIgnoreCase(flightType)) {
            newFlight = new InternationalFlight(flightId, flightNumberStr, airline, origin, destination, departureTime, arrivalTime, seatClasses, totalSeats, prices, passportNote, visaRequired);
        } else {
            System.out.println("Warning: Unknown flight type \"" + flightType + "\". Cannot add flight with specific parameters.");
            return null; // Or create a generic flight if that's desired, but GUI expects specific types
        }

        flights.add(newFlight);
        fileManager.saveFlights(flights); 
        System.out.println("\nFlight " + flightNumberStr + " (ID: " + flightId + ", Type: " + flightType + ") added successfully.");
        return newFlight;
    }
    
    // Original addFlight for console or simpler additions (if needed, or can be removed if GUI version is sufficient)
    public Flight addFlight(String flightNumberStr, String airline, String origin, String destination,
                            String departureTime, String arrivalTime, String flightType,
                            String[] seatClasses, int[] totalSeats, double[] prices) {
        // This version can call the more specific one with default additional parameters
        if ("Domestic".equalsIgnoreCase(flightType)) {
            return addFlight(flightNumberStr, airline, origin, destination, departureTime, arrivalTime, flightType, seatClasses, totalSeats, prices, false, "", false);
        } else if ("International".equalsIgnoreCase(flightType)) {
            return addFlight(flightNumberStr, airline, origin, destination, departureTime, arrivalTime, flightType, seatClasses, totalSeats, prices, false, "N/A", false);
        } else {
            System.out.println("\nError: Flight type must be 'Domestic' or 'International' for this simplified addFlight method.");
            return null;
        }
    }

    public boolean updateFlightSchedule(String flightNumberStr, String newDepartureTime, String newArrivalTime) {
        Flight flight = findFlightByNumberStr(flightNumberStr);
        if (flight != null) {
            boolean updated = false;
            if (newDepartureTime != null && !newDepartureTime.trim().isEmpty()) {
                flight.setDepartureTime(newDepartureTime.trim());
                updated = true;
            }
            if (newArrivalTime != null && !newArrivalTime.trim().isEmpty()) {
                flight.setArrivalTime(newArrivalTime.trim());
                updated = true;
            }
            if (updated) {
                fileManager.saveFlights(flights); 
                System.out.println("\nFlight " + flightNumberStr + " schedule updated.");
                return true;
            } else {
                System.out.println("\nNo changes provided for flight " + flightNumberStr + " schedule.");
                return false;
            }
        } else {
            System.out.println("\nError: Flight " + flightNumberStr + " not found.");
            return false;
        }
    }

    public void viewAllFlights() {
        if (flights.isEmpty()) {
            System.out.println("\nNo flights available in the system.");
            return;
        }
        System.out.println("\n--- All Available Flights ---");
        for (Flight flight : flights) {
            System.out.println(flight.getDetailedDescription()); 
            System.out.println("--------------------");
        }
    }

    public ArrayList<Flight> searchFlights(String origin, String destination, String date, String flightType) {
        ArrayList<Flight> results = new ArrayList<>();
        for (Flight flight : flights) {
            boolean originMatch = (origin == null || origin.trim().isEmpty() || flight.getOrigin().equalsIgnoreCase(origin.trim()));
            boolean destMatch = (destination == null || destination.trim().isEmpty() || flight.getDestination().equalsIgnoreCase(destination.trim()));
            boolean dateMatch = (date == null || date.trim().isEmpty() || flight.getDepartureTime().startsWith(date.trim())); 
            boolean typeMatch = (flightType == null || flightType.trim().isEmpty() || flight.getFlightType().equalsIgnoreCase(flightType.trim()));

            if (originMatch && destMatch && dateMatch && typeMatch) {
                results.add(flight);
            }
        }
        if (results.isEmpty()) {
            System.out.println("\nNo flights found matching your criteria.");
        } else {
            System.out.println("\n--- Flight Search Results ---");
            for (Flight flight : results) {
                 System.out.println(flight.getDetailedDescription());
                 System.out.println("--------------------");
            }
        }
        return results;
    }

    private Passenger findOrCreatePassenger(String name, String passport, String dob, String requests) {
        Passenger passenger = findPassengerByPassport(passport);
        if (passenger == null) {
            int passengerId = generatePassengerId();
            passenger = new Passenger(passengerId, name, passport, dob, requests);
            passengers.add(passenger);
            fileManager.savePassengers(passengers); 
        }
        return passenger;
    }

    public Booking createBooking(int customerId, int flightId, String[] passengerDetails, String seatClassPref) {
        User customer = findUserById(customerId);
        Flight flight = findFlightById(flightId);

        if (customer == null || !(customer instanceof Customer)) {
            System.out.println("\nError: Customer not found or invalid customer ID for booking.");
            return null;
        }
        if (flight == null) {
            System.out.println("\nError: Flight not found for booking.");
            return null;
        }
        if (passengerDetails == null || passengerDetails.length < 3 || seatClassPref == null || seatClassPref.isEmpty()) {
            System.out.println("\nError: Passenger details (name, passport, DOB) or seat preference are missing or invalid.");
            return null;
        }

        String name = passengerDetails[0];
        String passport = passengerDetails[1];
        String dob = passengerDetails[2];
        String requests = (passengerDetails.length > 3) ? passengerDetails[3] : "";

        if (name.isEmpty() || passport.isEmpty() || dob.isEmpty()) {
            System.out.println("\nError: Missing details (name, passport, or dob) for the passenger.");
            return null;
        }

        if (flight.getPrice(seatClassPref) < 0) { 
             System.out.println("\nError: Invalid seat class \"" + seatClassPref + "\" selected for flight " + flight.getFlightNumberStr() + ".");
             return null;
        }

        if (!flight.areSeatsAvailable(seatClassPref, 1)) {
            System.out.println("\nError: Not enough seats available in " + seatClassPref + " for flight " + flight.getFlightNumberStr() + ". Required: 1, Available: " + flight.getAvailableSeats(seatClassPref) + ".");
            return null;
        }

        Passenger passenger = findOrCreatePassenger(name, passport, dob, requests);
        if (passenger == null) { 
            System.out.println("\nError: Could not find or create passenger.");
            return null;
        }

        if (!flight.reserveSeats(seatClassPref, 1)) {
            System.out.println("\nCritical Error: Failed to reserve seat for class " + seatClassPref + " even after availability check. Booking failed.");
            return null; 
        }
        
        int bookingId = generateBookingId();
        Booking newBooking = new Booking(bookingId, customerId, flightId, passenger, seatClassPref);
        newBooking.calculateTotalPrice(this); 
        
        bookings.add(newBooking);
        fileManager.saveBookings(bookings);
        fileManager.saveFlights(flights); 

        System.out.println("\nBooking created successfully! ID: " + newBooking.getBookingId() + ", For Passenger: " + passenger.getName() + ", Total Price: $" + String.format("%.2f", newBooking.getTotalPrice()));
        return newBooking;
    }

    public boolean cancelBooking(int bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("\nError: Booking ID " + bookingId + " not found.");
            return false;
        }

        if (currentUser == null) {
            System.out.println("\nError: You must be logged in to cancel a booking.");
            return false;
        }
        boolean authorized = false;
        if (currentUser.getRole().equals("Customer") && currentUser.getUserId() == booking.getCustomerId()) {
            authorized = true;
        }
        if (currentUser.getRole().equals("Agent") || currentUser.getRole().equals("Administrator")) {
            authorized = true;
        }

        if (!authorized) {
            System.out.println("\nError: You are not authorized to cancel booking ID " + bookingId + ".");
            return false;
        }

        if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
            System.out.println("\nBooking ID " + bookingId + " is already cancelled.");
            return false;
        }

        Flight flight = findFlightById(booking.getFlightId());
        if (flight != null && booking.getSeatSelection() != null && !booking.getSeatSelection().isEmpty()) {
            flight.releaseSeats(booking.getSeatSelection(), 1); 
            fileManager.saveFlights(flights); 
        }

        booking.setStatus("Cancelled");
        if ("Paid".equalsIgnoreCase(booking.getPaymentStatus()) || "Completed".equalsIgnoreCase(booking.getPaymentStatus())) {
             booking.setPaymentStatus("Refunded"); 
        }
        fileManager.saveBookings(bookings);
        System.out.println("\nBooking ID " + bookingId + " has been cancelled.");
        return true;
    }

    public boolean processPayment(int bookingId, String paymentMethod, double amount) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("\nError: Booking ID " + bookingId + " not found for payment.");
            return false;
        }
        if (currentUser == null || 
            (currentUser.getRole().equals("Customer") && currentUser.getUserId() != booking.getCustomerId()) &&
            !(currentUser.getRole().equals("Agent") || currentUser.getRole().equals("Administrator"))){
                 System.out.println("\nError: You are not authorized to make a payment for this booking.");
                 return false;
        }

        if ("Paid".equalsIgnoreCase(booking.getPaymentStatus()) || "Completed".equalsIgnoreCase(booking.getPaymentStatus())) {
            System.out.println("\nBooking ID " + bookingId + " is already paid.");
            return true; 
        }

        booking.calculateTotalPrice(this); 
        if (amount < booking.getTotalPrice()) { 
            System.out.println("\nError: Payment amount ($" + String.format("%.2f", amount) + ") is less than the total price ($" + String.format("%.2f", booking.getTotalPrice()) + ") for booking ID " + bookingId + ".");
            return false;
        }

        int paymentId = generatePaymentId();
        String transactionDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        Payment payment = new Payment(paymentId, bookingId, amount, paymentMethod, "Pending", transactionDate);
        payments.add(payment); 

        System.out.println("\nAttempting to process payment for Booking ID: " + bookingId + " with amount: $" + String.format("%.2f", amount) + " via " + paymentMethod);
        boolean paymentSuccessful = payment.processPayment(); 

        if (paymentSuccessful) {
            booking.setPaymentStatus("Paid");
            booking.setStatus("Confirmed"); 
            System.out.println("Payment successful for Booking ID " + bookingId + ". Booking confirmed.");
        } else {
            booking.setPaymentStatus("Failed");
            System.out.println("Payment failed for Booking ID " + bookingId + ". Please try again.");
        }
        fileManager.saveBookings(bookings);
        return paymentSuccessful;
    }

    public String generateItinerary(int bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("\nError: Booking ID " + bookingId + " not found.");
            return null;
        }
        if (currentUser == null || 
            (currentUser.getRole().equals("Customer") && currentUser.getUserId() != booking.getCustomerId()) &&
            !(currentUser.getRole().equals("Agent") || currentUser.getRole().equals("Administrator"))){
                System.out.println("\nError: You are not authorized to generate an itinerary for this booking.");
                return null;
        }

        if (!"Confirmed".equalsIgnoreCase(booking.getStatus()) && !"Paid".equalsIgnoreCase(booking.getPaymentStatus())) {
            System.out.println("\nWarning: Booking ID " + bookingId + " is not yet confirmed or paid. Itinerary may be incomplete or provisional.");
        }
        if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
            Passenger p = findPassengerById(booking.getPassengerId());
            if (p != null) booking.setPassenger(p);
            else System.err.println("Could not resolve passenger for itinerary of booking " + bookingId);
        }
        String itinerary = booking.getItinerary(this);
        System.out.println(itinerary); // Also print to console for consistency
        return itinerary;
    }

    public void viewAllUsers() {
        if (!(currentUser instanceof Administrator)) {
            System.out.println("\nError: Only administrators can view all users.");
            return;
        }
        if (users.isEmpty()) {
            System.out.println("\nNo users found in the system.");
            return;
        }
        System.out.println("\n--- All Users ---");
        for (User user : users) {
            System.out.println(user.getDetailedDescription());
        }
    }

    public void viewAllBookings() {
        if (!(currentUser instanceof Administrator) && !(currentUser instanceof Agent) ) {
            System.out.println("\nError: Only administrators or agents can view all bookings.");
            return;
        }
        if (bookings.isEmpty()) {
            System.out.println("\nNo bookings found in the system.");
            return;
        }
        System.out.println("\n--- All Bookings ---");
        for (Booking booking : bookings) {
            if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
                 Passenger p = findPassengerById(booking.getPassengerId());
                 if (p != null) booking.setPassenger(p);
            }
            System.out.println(booking.getDetailedDescription(this));
        }
    }

    public void viewCustomerBookings(int customerId) {
        User customer = findUserById(customerId);
        if (customer == null || !(customer instanceof Customer)) {
            System.out.println("\nError: Customer with ID " + customerId + " not found.");
            return;
        }

        if (currentUser == null) {
            System.out.println("\nError: You must be logged in.");
            return;
        }
        boolean authorized = false;
        if (currentUser.getRole().equals("Customer") && currentUser.getUserId() == customerId) {
            authorized = true;
        }
        if (currentUser.getRole().equals("Agent") || currentUser.getRole().equals("Administrator")) {
            authorized = true;
        }

        if (!authorized) {
            System.out.println("\nError: You are not authorized to view bookings for customer ID " + customerId + ".");
            return;
        }

        ArrayList<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomerId() == customerId) {
                if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
                    Passenger p = findPassengerById(booking.getPassengerId());
                    if (p != null) booking.setPassenger(p);
                }
                customerBookings.add(booking);
            }
        }

        if (customerBookings.isEmpty()) {
            System.out.println("\nNo bookings found for customer " + customer.getName() + " (ID: " + customerId + ").");
        } else {
            System.out.println("\n--- Bookings for " + customer.getName() + " (ID: " + customerId + ") ---");
            for (Booking booking : customerBookings) {
                System.out.println(booking.getDetailedDescription(this));
            }
        }
    }
    
    public void generateReport() {
        if (!(currentUser instanceof Administrator) && !(currentUser instanceof Agent)) {
            System.out.println("\nError: Only administrators or agents can generate reports.");
            return;
        }
        System.out.println("\n----- System Report -----");
        System.out.println("Total Users: " + users.size());
        System.out.println("Total Flights: " + flights.size());
        System.out.println("Total Bookings: " + bookings.size());
        double totalRevenue = 0.0;
        int confirmedBookings = 0;
        for (Booking booking : bookings) {
            if ("Paid".equalsIgnoreCase(booking.getPaymentStatus()) || "Completed".equalsIgnoreCase(booking.getPaymentStatus())) {
                totalRevenue += booking.getTotalPrice();
                confirmedBookings++;
            }
        }
        System.out.println("Confirmed Bookings: " + confirmedBookings);
        System.out.println("Total Revenue from Paid Bookings: $" + String.format("%.2f", totalRevenue));
        System.out.println("-------------------------");
    }

    public boolean updateProfile(String oldPassword, String newName, String newPassword) {
        User user = getCurrentUser();
        if (user == null) {
            System.out.println("No user logged in to update profile.");
            return false;
        }

        if (!user.checkPassword(oldPassword)) {
            System.out.println("Error: Incorrect old password. Profile not updated.");
            return false;
        }

        boolean updated = false;
        if (newName != null && !newName.trim().isEmpty()) {
            user.setName(newName.trim());
            updated = true;
        }

        if (newPassword != null && !newPassword.trim().isEmpty()) {
            if (newPassword.trim().length() < 6 || !newPassword.trim().matches(".*[a-zA-Z].*") || !newPassword.trim().matches(".*[0-9].*")) {
                System.out.println("\nError: New password must be at least 6 characters long and contain at least one letter and one number.");
                return false;
            }
            user.setPassword(oldPassword, newPassword.trim()); 
            updated = true;
        }
        if (updated) {
            System.out.println("Profile updated successfully.");
            saveData();
            return true;
        } else {
            System.out.println("No changes provided for profile update.");
            return false;
        }
    }

    public void enableUser(String username, boolean enable) {
        if (!(currentUser instanceof Administrator)) {
            System.out.println("\nError: Only administrators can enable/disable users.");
            return;
        }
        User targetUser = findUserByUsername(username);
        if (targetUser != null) {
            targetUser.setActive(enable); 
            System.out.println("User " + username + " has been " + (enable ? "enabled." : "disabled."));
            saveData(); 
        } else {
            System.out.println("User " + username + " not found.");
        }
    }

    public void generateTicket(int bookingId) {
        Booking booking = findBookingById(bookingId);
        if (booking == null) {
            System.out.println("Error: Booking not found to generate ticket.");
            return;
        }
        if (!"Confirmed".equalsIgnoreCase(booking.getStatus())){
            System.out.println("Warning: Booking " + bookingId + " is not confirmed. Ticket may not be valid.");
        }
        System.out.println("\n--- TICKET FOR BOOKING ID: " + bookingId + " ---");
        System.out.println(booking.getItinerary(this)); 
        System.out.println("---------------------------");
    }

    public boolean manageUserAccess(String username, String newRole) {
        if (!(currentUser instanceof Administrator)) {
            System.out.println("\nError: Only administrators can manage user access/roles.");
            return false;
        }
        User targetUser = findUserByUsername(username);
        if (targetUser == null) {
            System.out.println("\nError: User \"" + username + "\" not found.");
            return false;
        }
        
        if (targetUser.getRole().equals(newRole)){
            System.out.println("User " + username + " already has the role " + newRole + ".");
            return true; 
        }

        if (!("Customer".equals(newRole) || "Agent".equals(newRole) || "Administrator".equals(newRole))) {
            System.out.println("\nError: Invalid role \"" + newRole + "\". Valid roles are Customer, Agent, Administrator.");
            return false;
        }
        
        // Create new user object of the new type, copy common fields, remove old, add new
        // This is a simplified approach. A more robust one would handle specific fields of each user type.
        User updatedUser = null;
        int userId = targetUser.getUserId();
        String currentUsername = targetUser.getUsername();
        String currentPassword = targetUser.getPassword(); // This might be hashed, direct copy might be okay or need re-set
        String currentName = targetUser.getName();
        String currentEmail = targetUser.getEmail();
        String currentContact = targetUser.getContactInfo();
        // Specific fields need careful handling
        String specificInfo1 = "N/A"; // Default
        String specificInfo2 = "0.0"; // Default

        if (targetUser instanceof Customer) specificInfo1 = ((Customer)targetUser).getAddress();
        if (targetUser instanceof Agent) {
            specificInfo1 = ((Agent)targetUser).getDepartment();
            specificInfo2 = String.valueOf(((Agent)targetUser).getCommissionRate());
        }
        if (targetUser instanceof Administrator) specificInfo1 = ((Administrator)targetUser).getSecurityLevel();

        switch (newRole) {
            case "Customer":
                updatedUser = new Customer(userId, currentUsername, currentPassword, currentName, currentEmail, currentContact, specificInfo1); // Assuming address is specificInfo1
                break;
            case "Agent":
                try {
                    updatedUser = new Agent(userId, currentUsername, currentPassword, currentName, currentEmail, currentContact, specificInfo1, Double.parseDouble(specificInfo2)); // Assuming department and commission
                } catch (NumberFormatException e) { /* Use default if parse fails */ 
                    updatedUser = new Agent(userId, currentUsername, currentPassword, currentName, currentEmail, currentContact, "DefaultDept", 0.05);
                }
                break;
            case "Administrator":
                updatedUser = new Administrator(userId, currentUsername, currentPassword, currentName, currentEmail, currentContact, specificInfo1); // Assuming security level is specificInfo1
                break;
        }

        if (updatedUser != null) {
            users.remove(targetUser);
            users.add(updatedUser);
            updatedUser.setActive(targetUser.isActive()); // Preserve active status
            fileManager.saveUsers(users);
            System.out.println("Role for user " + username + " changed to " + newRole + ". User object recreated.");
            // If current user is the one being changed, update currentUser reference
            if (currentUser != null && currentUser.getUserId() == userId) {
                currentUser = updatedUser;
            }
            return true;
        } else {
            System.out.println("Failed to recreate user object for role change.");
            return false;
        }
    }

    public boolean bookFlightForCustomer(int customerId, int flightId, String[] passengerDetails, String seatClassPref) {
        if (!(currentUser instanceof Agent || currentUser instanceof Administrator)) {
            System.out.println("\nError: Only Agents or Administrators can book flights for other customers.");
            return false;
        }
        User customer = findUserById(customerId);
        if (customer == null || !(customer instanceof Customer)){
            System.out.println("\nError: Customer with ID " + customerId + " not found or is not a valid customer.");
            return false;
        }
        Booking booking = createBooking(customerId, flightId, passengerDetails, seatClassPref);
        return booking != null;
    }

    public boolean bookFlight(int flightId, String[] passengerDetails, String seatClassPref) {
        if (!(currentUser instanceof Customer)) {
            System.out.println("\nError: Only logged-in customers can book flights for themselves. Agents/Admins should use 'bookFlightForCustomer'.");
            return false;
        }
        Booking booking = createBooking(currentUser.getUserId(), flightId, passengerDetails, seatClassPref);
        return booking != null;
    }

    // --- GUI Helper Methods ---
    public void viewAllFlightsGui(JTextArea textArea) {
        textArea.setText(""); // Clear previous content
        if (flights.isEmpty()) {
            textArea.append("No flights available in the system.\n");
            return;
        }
        textArea.append("--- All Available Flights ---\n");
        for (Flight flight : flights) {
            textArea.append(flight.getDetailedDescription() + "\n");
            textArea.append("--------------------\n");
        }
    }

    public void searchFlightsGui(String origin, String destination, String date, String flightType, JTextArea resultsArea) {
        resultsArea.setText(""); // Clear previous results
        ArrayList<Flight> results = new ArrayList<>();
        for (Flight flight : flights) {
            boolean originMatch = (origin == null || origin.trim().isEmpty() || flight.getOrigin().equalsIgnoreCase(origin.trim()));
            boolean destMatch = (destination == null || destination.trim().isEmpty() || flight.getDestination().equalsIgnoreCase(destination.trim()));
            boolean dateMatch = (date == null || date.trim().isEmpty() || flight.getDepartureTime().startsWith(date.trim()));
            boolean typeMatch = (flightType == null || flightType.trim().isEmpty() || flight.getFlightType().equalsIgnoreCase(flightType.trim()));

            if (originMatch && destMatch && dateMatch && typeMatch) {
                results.add(flight);
            }
        }
        if (results.isEmpty()) {
            resultsArea.append("No flights found matching your criteria.\n");
        } else {
            resultsArea.append("--- Flight Search Results ---\n");
            for (Flight flight : results) {
                resultsArea.append(flight.getDetailedDescription() + "\n");
                resultsArea.append("--------------------\n");
            }
        }
    }

    public void viewCustomerBookingsGui(int customerId, JTextArea textArea) {
        textArea.setText("");
        User customer = findUserById(customerId);
        if (customer == null || !(customer instanceof Customer)) {
            textArea.append("Error: Customer with ID " + customerId + " not found.\n");
            return;
        }
        // Authorization check (simplified for GUI context, assuming GUI handles who can call this)
        ArrayList<Booking> customerBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getCustomerId() == customerId) {
                if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
                    Passenger p = findPassengerById(booking.getPassengerId());
                    if (p != null) booking.setPassenger(p);
                }
                customerBookings.add(booking);
            }
        }
        if (customerBookings.isEmpty()) {
            textArea.append("No bookings found for customer " + customer.getName() + " (ID: " + customerId + ").\n");
        } else {
            textArea.append("--- Bookings for " + customer.getName() + " (ID: " + customerId + ") ---\n");
            for (Booking booking : customerBookings) {
                textArea.append(booking.getDetailedDescription(this) + "\n");
                textArea.append("--------------------\n");
            }
        }
    }

    public void viewAllUsersGui(JTextArea textArea) {
        textArea.setText("");
        if (!(currentUser instanceof Administrator)) {
            textArea.append("Error: Only administrators can view all users.\n");
            return;
        }
        if (users.isEmpty()) {
            textArea.append("No users found in the system.\n");
            return;
        }
        textArea.append("--- All Users ---\n");
        for (User user : users) {
            textArea.append(user.getDetailedDescription() + "\n"); // Assuming User has getDetailedDescription
            textArea.append("--------------------\n");
        }
    }

    public void viewAllBookingsGui(JTextArea textArea) {
        textArea.setText("");
        if (!(currentUser instanceof Administrator) && !(currentUser instanceof Agent)) {
            textArea.append("Error: Only administrators or agents can view all bookings.\n");
            return;
        }
        if (bookings.isEmpty()) {
            textArea.append("No bookings found in the system.\n");
            return;
        }
        textArea.append("--- All Bookings ---\n");
        for (Booking booking : bookings) {
            if (booking.getPassenger() == null && booking.getPassengerId() > 0) {
                 Passenger p = findPassengerById(booking.getPassengerId());
                 if (p != null) booking.setPassenger(p);
            }
            textArea.append(booking.getDetailedDescription(this) + "\n");
            textArea.append("--------------------\n");
        }
    }

    // ── Extra helpers for GUI ────────────────────────────────────────────────

    public ArrayList<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }

    public ArrayList<Booking> getAllBookings() {
        if (!(currentUser instanceof Administrator) && !(currentUser instanceof Agent)) {
            return new ArrayList<>();
        }
        for (Booking b : bookings) {
            if (b.getPassenger() == null && b.getPassengerId() > 0) {
                Passenger p = findPassengerById(b.getPassengerId());
                if (p != null) b.setPassenger(p);
            }
        }
        return new ArrayList<>(bookings);
    }

    public ArrayList<Booking> getCustomerBookings(int customerId) {
        ArrayList<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getCustomerId() == customerId) {
                if (b.getPassenger() == null && b.getPassengerId() > 0) {
                    Passenger p = findPassengerById(b.getPassengerId());
                    if (p != null) b.setPassenger(p);
                }
                result.add(b);
            }
        }
        return result;
    }

    public ArrayList<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    public String getReportString() {
        StringBuilder sb = new StringBuilder();
        sb.append("===== SYSTEM REPORT =====\n\n");
        sb.append("Total Users:    ").append(users.size()).append("\n");
        sb.append("Total Flights:  ").append(flights.size()).append("\n");
        sb.append("Total Bookings: ").append(bookings.size()).append("\n\n");
        double revenue = 0;
        int confirmed = 0;
        for (Booking b : bookings) {
            if ("Paid".equalsIgnoreCase(b.getPaymentStatus()) || "Completed".equalsIgnoreCase(b.getPaymentStatus())) {
                revenue += b.getTotalPrice();
                confirmed++;
            }
        }
        sb.append("Confirmed (Paid) Bookings: ").append(confirmed).append("\n");
        sb.append(String.format("Total Revenue:             $%.2f\n", revenue));
        sb.append("\n=========================\n");
        return sb.toString();
    }
}
