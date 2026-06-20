package oopflightbookingsystem.src;

// DomesticFlight subclass extending Flight
public class DomesticFlight extends Flight {
    private boolean isIntraGovernorate;

    public DomesticFlight(int flightId, String flightNumberStr, String airline, String origin, String destination,
                          String departureTime, String arrivalTime,
                          String[] seatClassesInput, int[] totalSeatsInput, double[] pricesInput,
                          boolean isIntraGovernorate) {
        super(flightId, flightNumberStr, airline, origin, destination, departureTime, arrivalTime, "Domestic",
              seatClassesInput, totalSeatsInput, pricesInput);
        this.isIntraGovernorate = isIntraGovernorate;
    }

    public boolean isIntraGovernorate() {
        return isIntraGovernorate;
    }

    public void setIntraGovernorate(boolean intraGovernorate) {
        isIntraGovernorate = intraGovernorate;
    }

    // Example of overriding getPrice to add a domestic tax
    @Override
    public double getPrice(String seatClass) {
        double basePrice = super.getPrice(seatClass);
        if (basePrice >= 0) {
            // Apply a hypothetical 5% domestic tax
            return basePrice * 1.05; 
        }
        return basePrice; // Return -1.0 if class not found
    }

    @Override
    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder(super.getDetailedDescription());
        sb.append("Intra-Governorate: ").append(isIntraGovernorate ? "Yes" : "No").append("\n");
        return sb.toString();
    }

    @Override
    public String toFileString() {
        // Base flight string: flightId|flightNumberStr|airline|origin|destination|departureTime|arrivalTime|flightType|seatClassesStr|totalSeatsStr|bookedSeatsStr|pricesStr
        String baseString = super.toFileString(); 
        return String.join(DELIMITER, 
            baseString, 
            String.valueOf(isIntraGovernorate)
        );
    }
}

