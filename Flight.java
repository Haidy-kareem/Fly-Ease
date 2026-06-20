package oopflightbookingsystem.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List; 
import java.util.stream.Collectors;

public class Flight {

    protected int flightId;
    protected String flightNumberStr;
    protected String airline;
    protected String origin;
    protected String destination;
    protected String departureTime; 
    protected String arrivalTime;   
    protected String flightType;    

    protected ArrayList<String> seatClasses;    
    protected ArrayList<Integer> totalSeats;      
    protected ArrayList<Integer> bookedSeats;     
    protected ArrayList<Double> prices;       

    protected static final String DELIMITER = "|";
    protected static final String ARRAY_DELIMITER = ",";

    public Flight(int flightId, String flightNumberStr, String airline, String origin, String destination,
                  String departureTime, String arrivalTime, String flightType,
                  String[] seatClassesInput, int[] totalSeatsInput, double[] pricesInput) {
        this.flightId = flightId;
        this.flightNumberStr = flightNumberStr;
        this.airline = airline;
        this.origin = origin;
        this.destination = destination;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.flightType = flightType;

        this.seatClasses = new ArrayList<>();
        this.totalSeats = new ArrayList<>();
        this.bookedSeats = new ArrayList<>();
        this.prices = new ArrayList<>();

        if (seatClassesInput != null && totalSeatsInput != null && pricesInput != null &&
            seatClassesInput.length == totalSeatsInput.length && seatClassesInput.length == pricesInput.length) {
            for (int i = 0; i < seatClassesInput.length; i++) {
                this.seatClasses.add(seatClassesInput[i]);
                this.totalSeats.add(totalSeatsInput[i]);
                this.prices.add(pricesInput[i]);
                this.bookedSeats.add(0); 
            }
        } else {
            System.err.println("Warning: Invalid seat/price data provided for flight " + flightNumberStr + ". Initializing empty seat details.");
        }
    }

    // Getters
    public int getFlightId() {
        return flightId;
    }

    public String getFlightNumberStr() {
        return flightNumberStr;
    }

    public String getAirline() {
        return airline;
    }

    public String getOrigin() {
        return origin;
    }

    public String getDestination() {
        return destination;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public String getArrivalTime() {
        return arrivalTime;
    }

    public String getFlightType() {
        return flightType;
    }

    // Setters
    public void setDepartureTime(String departureTime) {
        if (departureTime != null && !departureTime.trim().isEmpty()) {
            this.departureTime = departureTime.trim();
        }
    }

    public void setArrivalTime(String arrivalTime) {
        if (arrivalTime != null && !arrivalTime.trim().isEmpty()) {
            this.arrivalTime = arrivalTime.trim();
        }
    }

    public int getAvailableSeats(String seatClass) {
        int index = findClassIndex(seatClass);
        if (index != -1) {
            return totalSeats.get(index) - bookedSeats.get(index);
        }
        return 0;
    }

    public double getPrice(String seatClass) {
        int index = findClassIndex(seatClass);
        if (index != -1) {
            return prices.get(index);
        }
        System.err.println("Warning: Seat class \"" + seatClass + "\" not found for flight " + this.flightNumberStr + ". Returning -1.0 price.");
        return -1.0; 
    }

    protected int findClassIndex(String seatClass) {
        if (seatClass == null) return -1;
        for (int i = 0; i < seatClasses.size(); i++) {
            if (seatClass.equalsIgnoreCase(seatClasses.get(i))) {
                return i;
            }
        }
        return -1;
    }

    public void updateSchedule(String newDepartureTime, String newArrivalTime) {
        setDepartureTime(newDepartureTime);
        setArrivalTime(newArrivalTime);
        System.out.println("Schedule updated for flight " + flightNumberStr + ".");
    }

    public boolean reserveSeats(String seatClass, int numSeats) {
        int index = findClassIndex(seatClass);
        if (index != -1) {
            if (getAvailableSeats(seatClass) >= numSeats) {
                bookedSeats.set(index, bookedSeats.get(index) + numSeats);
                return true;
            } else {
                System.err.println("Error: Not enough seats available in " + seatClass + " for flight " + flightNumberStr + ". Required: " + numSeats + ", Available: " + getAvailableSeats(seatClass));
                return false;
            }
        }
        System.err.println("Error: Cannot reserve seat. Invalid seat class \"" + seatClass + "\" for flight " + flightNumberStr + ".");
        return false;
    }

    public boolean releaseSeats(String seatClass, int numSeats) {
        int index = findClassIndex(seatClass);
        if (index != -1) {
            if (bookedSeats.get(index) >= numSeats) {
                bookedSeats.set(index, bookedSeats.get(index) - numSeats);
                return true;
            } else {
                System.err.println("Warning: Attempted to release more seats than booked in " + seatClass + " for flight " + flightNumberStr + ". Adjusting booked seats to 0.");
                bookedSeats.set(index, 0); 
                return false; 
            }
        }
        System.err.println("Error: Cannot release seat. Invalid seat class \"" + seatClass + "\" for flight " + flightNumberStr + ".");
        return false;
    }
    
    public boolean areSeatsAvailable(String seatClass, int numSeatsNeeded) {
        int index = findClassIndex(seatClass);
        if (index != -1) {
            return (totalSeats.get(index) - bookedSeats.get(index)) >= numSeatsNeeded;
        }
        return false; 
    }

    public void displaySeatAvailability() {
        System.out.println("Seat Availability for Flight " + this.flightNumberStr + ":");
        if (this.seatClasses.isEmpty()) {
            System.out.println("  No seat class information available for this flight.");
            return;
        }
        for (int i = 0; i < this.seatClasses.size(); i++) {
            String sClass = this.seatClasses.get(i);
            int available = getAvailableSeats(sClass);
            double price = getPrice(sClass); 
            System.out.println(String.format("  - Class: %-15s | Available Seats: %-5d | Price: $%.2f", sClass, available, price));
        }
        System.out.println("----------------------------------------");
    }

    public String getDetailedDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight ID: ").append(flightId).append("\n");
        sb.append("Flight Number: ").append(flightNumberStr).append("\n");
        sb.append("Airline: ").append(airline).append("\n");
        sb.append("Origin: ").append(origin).append("\n");
        sb.append("Destination: ").append(destination).append("\n");
        sb.append("Departure: ").append(departureTime).append("\n");
        sb.append("Arrival: ").append(arrivalTime).append("\n");
        sb.append("Type: ").append(flightType).append("\n");
        sb.append("Seats:\n");
        if (seatClasses.isEmpty()) {
            sb.append("  No seat class information available.\n");
        } else {
            for (int i = 0; i < seatClasses.size(); i++) {
                sb.append(String.format("  - Class: %-15s | Total: %-4d | Booked: %-4d | Available: %-4d | Price: $%.2f\n", 
                                      seatClasses.get(i), 
                                      totalSeats.get(i), 
                                      bookedSeats.get(i),
                                      getAvailableSeats(seatClasses.get(i)), 
                                      getPrice(seatClasses.get(i))));
            }
        }
        return sb.toString();
    }

    public String toFileString() {
        String classesStr = seatClasses.stream().collect(Collectors.joining(ARRAY_DELIMITER));
        String totalsStr = totalSeats.stream().map(String::valueOf).collect(Collectors.joining(ARRAY_DELIMITER));
        String bookedStr = bookedSeats.stream().map(String::valueOf).collect(Collectors.joining(ARRAY_DELIMITER));
        String pricesStr = prices.stream().map(d -> String.format("%.2f", d).replace(",", ".")).collect(Collectors.joining(ARRAY_DELIMITER));

        return String.join(DELIMITER,
                String.valueOf(flightId),
                flightNumberStr != null ? flightNumberStr : "",
                airline != null ? airline : "",
                origin != null ? origin : "",
                destination != null ? destination : "",
                departureTime != null ? departureTime : "",
                arrivalTime != null ? arrivalTime : "",
                flightType != null ? flightType : "",
                classesStr,
                totalsStr,
                bookedStr,
                pricesStr
        );
    }

    public static Flight fromFileString(String line) {
        String[] parts = line.split("\\" + DELIMITER, -1);
        if (parts.length >= 12) { // Allow for more parts for subclasses
            try {
                int flightIdFromFile = Integer.parseInt(parts[0]);
                String flightNumberStrFromFile = parts[1];
                String airlineFromFile = parts[2];
                String originFromFile = parts[3];
                String destinationFromFile = parts[4];
                String departureTimeFromFile = parts[5];
                String arrivalTimeFromFile = parts[6];
                String flightTypeFromFile = parts[7];

                String[] classesArr = parts[8].isEmpty() ? new String[0] : parts[8].split(ARRAY_DELIMITER);
                String[] totalsArrStr = parts[9].isEmpty() ? new String[0] : parts[9].split(ARRAY_DELIMITER);
                String[] bookedArrStr = parts[10].isEmpty() ? new String[0] : parts[10].split(ARRAY_DELIMITER);
                String[] pricesArrStr = parts[11].isEmpty() ? new String[0] : parts[11].split(ARRAY_DELIMITER);

                int count = classesArr.length;
                if (count != totalsArrStr.length || count != bookedArrStr.length || count != pricesArrStr.length) {
                    System.err.println("Warning: Mismatched array lengths in flight data line: " + line + ". Skipping flight.");
                    return null;
                }

                int[] totalSeatsFromFile = new int[count];
                double[] pricesFromFile = new double[count];
                int[] bookedSeatsFromFile = new int[count];

                for (int i = 0; i < count; i++) {
                    totalSeatsFromFile[i] = Integer.parseInt(totalsArrStr[i]);
                    bookedSeatsFromFile[i] = Integer.parseInt(bookedArrStr[i]);
                    pricesFromFile[i] = Double.parseDouble(pricesArrStr[i].replace(",", "."));
                }
                
                Flight flight;
                if ("Domestic".equalsIgnoreCase(flightTypeFromFile)) {
                    boolean isIntraGov = (parts.length > 12 && parts[12] != null) ? Boolean.parseBoolean(parts[12]) : false;
                    flight = new DomesticFlight(flightIdFromFile, flightNumberStrFromFile, airlineFromFile, originFromFile, destinationFromFile, 
                                              departureTimeFromFile, arrivalTimeFromFile, 
                                              classesArr, totalSeatsFromFile, pricesFromFile, 
                                              isIntraGov); 
                } else if ("International".equalsIgnoreCase(flightTypeFromFile)) {
                    String passportNote = (parts.length > 12 && parts[12] != null) ? parts[12] : "Standard passport rules apply";
                    boolean visaReq = (parts.length > 13 && parts[13] != null) ? Boolean.parseBoolean(parts[13]) : false;
                    flight = new InternationalFlight(flightIdFromFile, flightNumberStrFromFile, airlineFromFile, originFromFile, destinationFromFile, 
                                                 departureTimeFromFile, arrivalTimeFromFile, 
                                                 classesArr, totalSeatsFromFile, pricesFromFile, 
                                                 passportNote, visaReq); 
                } else {
                    flight = new Flight(flightIdFromFile, flightNumberStrFromFile, airlineFromFile, originFromFile, destinationFromFile, 
                                    departureTimeFromFile, arrivalTimeFromFile, flightTypeFromFile, 
                                    classesArr, totalSeatsFromFile, pricesFromFile);
                }

                for(int i=0; i < flight.bookedSeats.size(); i++){
                    if(i < bookedSeatsFromFile.length){
                        flight.bookedSeats.set(i, bookedSeatsFromFile[i]);
                    }
                }
                return flight;

            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                System.err.println("Warning: Error parsing flight data line: " + line + ". Error: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Warning: Invalid flight data line format (expected at least 12 parts): " + line + " (got " + parts.length + " parts)");
            return null;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flight{");
        sb.append("ID=").append(flightId);
        sb.append(", No=").append(flightNumberStr).append("\t");
        sb.append(", Airline=").append(airline).append("\t");
        sb.append(", Route=").append(origin).append(" -> ").append(destination);
        sb.append(", Dep=").append(departureTime);
        sb.append(", Arr=").append(arrivalTime);
        sb.append(", Type=").append(flightType);
        sb.append(", Seats=[");
        if (seatClasses.isEmpty()) {
            sb.append("No seat info");
        } else {
            for (int i = 0; i < seatClasses.size(); i++) {
                sb.append(seatClasses.get(i)).append(": ")
                  .append(getAvailableSeats(seatClasses.get(i))).append("/").append(totalSeats.get(i))
                  .append(" ($").append(String.format("%.2f", getPrice(seatClasses.get(i)))).append(")"); 
                if (i < seatClasses.size() - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]}");
        return sb.toString();
    }

    public static class Aircraft { 
        private String model;
        private int capacity;

        public Aircraft(String model, int capacity) {
            this.model = model;
            this.capacity = capacity;
        }

        public String getModel() {
            return model;
        }

        public int getCapacity() {
            return capacity;
        }
    }

    public static class Airline { 
        private String name;
        private List<Aircraft> fleet;

        public Airline(String name) {
            this.name = name;
            this.fleet = new ArrayList<>();
        }

        public void addAircraft(Aircraft aircraft) {
            this.fleet.add(aircraft);
        }

        public String getName() {
            return name;
        }

        public List<Aircraft> getFleet() {
            return fleet;
        }
    }
}

