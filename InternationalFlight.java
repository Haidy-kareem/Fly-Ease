package oopflightbookingsystem.src;

// InternationalFlight subclass extending Flight
public class InternationalFlight extends Flight {
    private String passportAndVisaRequirements;
    private boolean visaRequired; // Simplified from original, can be expanded

    public InternationalFlight(int flightId, String flightNumberStr, String airline, String origin, String destination,
                               String departureTime, String arrivalTime,
                               String[] seatClassesInput, int[] totalSeatsInput, double[] pricesInput,
                               String passportAndVisaRequirements, boolean visaRequired) {
        super(flightId, flightNumberStr, airline, origin, destination, departureTime, arrivalTime, "International",
              seatClassesInput, totalSeatsInput, pricesInput);
        this.passportAndVisaRequirements = (passportAndVisaRequirements != null && !passportAndVisaRequirements.trim().isEmpty()) 
                                           ? passportAndVisaRequirements.trim() 
                                           : "Standard passport and visa rules apply.";
        this.visaRequired = visaRequired;
    }

    public String getPassportAndVisaRequirements() {
        return passportAndVisaRequirements;
    }

    public void setPassportAndVisaRequirements(String requirements) {
        if (requirements != null && !requirements.trim().isEmpty()) {
            this.passportAndVisaRequirements = requirements.trim();
        }
    }

    public boolean isVisaRequired() {
        return visaRequired;
    }

    public void setVisaRequired(boolean visaRequired) {
        this.visaRequired = visaRequired;
    }

    // Example of overriding getPrice to add an international surcharge
    @Override
    public double getPrice(String seatClass) {
        double basePrice = super.getPrice(seatClass);
        if (basePrice >= 0) {
            // Apply a hypothetical $50 international surcharge
            return basePrice + 50.0;
        }
        return basePrice; // Return -1.0 if class not found
    }

    @Override
    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder(super.getDetailedDescription());
        sb.append("Passport/Visa Info: ").append(passportAndVisaRequirements).append("\n");
        sb.append("Visa Required: ").append(visaRequired ? "Yes" : "No").append("\n");
        return sb.toString();
    }

    @Override
    public String toFileString() {
        // Base flight string: flightId|flightNumberStr|airline|origin|destination|departureTime|arrivalTime|flightType|seatClassesStr|totalSeatsStr|bookedSeatsStr|pricesStr
        String baseString = super.toFileString();
        return String.join(DELIMITER, 
            baseString, 
            passportAndVisaRequirements != null ? passportAndVisaRequirements : "", 
            String.valueOf(visaRequired)
        );
    }
}

