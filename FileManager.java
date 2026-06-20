package oopflightbookingsystem.src;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private String dataDirectoryPath;
    private static final String USERS_FILE = "users.txt";
    private static final String FLIGHTS_FILE = "flights.txt";
    private static final String BOOKINGS_FILE = "bookings.txt";
    private static final String PASSENGERS_FILE = "passengers.txt";

    public FileManager(String dataDirectoryPath) {
        this.dataDirectoryPath = dataDirectoryPath;
        ensureDirectoryAndFiles();
    }

    public void setDataDirectoryPath(String newPath) {
        this.dataDirectoryPath = newPath;
        ensureDirectoryAndFiles();
    }

    private void ensureDirectoryAndFiles() {
        File dataDir = new File(dataDirectoryPath);
        if (!dataDir.exists()) {
            if (dataDir.mkdirs()) {
                System.out.println("Data directory created: " + dataDirectoryPath);
            } else {
                System.err.println("Error: Could not create data directory: " + dataDirectoryPath);
            }
        }
        createFileIfNotExists(USERS_FILE);
        createFileIfNotExists(FLIGHTS_FILE);
        createFileIfNotExists(BOOKINGS_FILE);
        createFileIfNotExists(PASSENGERS_FILE);
    }

    private void createFileIfNotExists(String fileName) {
        File file = new File(dataDirectoryPath + File.separator + fileName);
        try {
            if (file.createNewFile()) {
                System.out.println("File created: " + file.getAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Error creating file: " + file.getAbsolutePath() + " - " + e.getMessage());
        }
    }

    // --- Load Users ---
    public ArrayList<User> loadUsers() {
        ArrayList<User> users = new ArrayList<>();
        String filePath = dataDirectoryPath + File.separator + USERS_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Users file not found, starting with empty user list.");
            return users;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                User user = null;
                String[] parts = line.split("\\|", -1); // Corrected delimiter regex
                if (parts.length > 6) { // Base user has 7 fields, role is 7th (index 6)
                    String role = parts[6];
                    if ("Customer".equals(role)) {
                        user = Customer.fromFileString(line);
                    } else if ("Agent".equals(role)) {
                        user = Agent.fromFileString(line);
                    } else if ("Administrator".equals(role)) {
                        user = Administrator.fromFileString(line);
                    }
                }
                if (user != null) {
                    users.add(user);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading users file: " + filePath + " - " + e.getMessage());
        }
        return users;
    }

    // --- Save Users ---
    public void saveUsers(ArrayList<User> users) {
        String filePath = dataDirectoryPath + File.separator + USERS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (User user : users) {
                if (user != null) {
                    writer.write(user.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing users file: " + filePath + " - " + e.getMessage());
        }
    }

    // --- Load Flights ---
    public ArrayList<Flight> loadFlights() {
        ArrayList<Flight> flights = new ArrayList<>();
        String filePath = dataDirectoryPath + File.separator + FLIGHTS_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Flights file not found, starting with empty flight list.");
            return flights;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Flight flight = Flight.fromFileString(line);
                if (flight != null) {
                    flights.add(flight);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading flights file: " + filePath + " - " + e.getMessage());
        }
        return flights;
    }

    // --- Save Flights ---
    public void saveFlights(ArrayList<Flight> flights) {
        String filePath = dataDirectoryPath + File.separator + FLIGHTS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Flight flight : flights) {
                if (flight != null) {
                    writer.write(flight.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing flights file: " + filePath + " - " + e.getMessage());
        }
    }

    // --- Load Bookings ---
    public ArrayList<Booking> loadBookings() {
        ArrayList<Booking> bookings = new ArrayList<>();
        String filePath = dataDirectoryPath + File.separator + BOOKINGS_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Bookings file not found, starting with empty booking list.");
            return bookings;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Booking booking = Booking.fromFileString(line);
                if (booking != null) {
                    bookings.add(booking);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading bookings file: " + filePath + " - " + e.getMessage());
        }
        return bookings;
    }

    // --- Save Bookings ---
    public void saveBookings(ArrayList<Booking> bookings) {
        String filePath = dataDirectoryPath + File.separator + BOOKINGS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Booking booking : bookings) {
                if (booking != null) {
                    writer.write(booking.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing bookings file: " + filePath + " - " + e.getMessage());
        }
    }

    // --- Load Passengers ---
    public ArrayList<Passenger> loadPassengers() {
        ArrayList<Passenger> passengers = new ArrayList<>();
        String filePath = dataDirectoryPath + File.separator + PASSENGERS_FILE;
        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("Passengers file not found, starting with empty passenger list.");
            return passengers;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Passenger passenger = Passenger.fromFileString(line);
                if (passenger != null) {
                    passengers.add(passenger);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading passengers file: " + filePath + " - " + e.getMessage());
        }
        return passengers;
    }

    // --- Save Passengers ---
    public void savePassengers(ArrayList<Passenger> passengers) {
        String filePath = dataDirectoryPath + File.separator + PASSENGERS_FILE;
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Passenger passenger : passengers) {
                if (passenger != null) {
                    writer.write(passenger.toFileString());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error writing passengers file: " + filePath + " - " + e.getMessage());
        }
    }

  
    public void writeLog(String message) {
        try (FileWriter writer = new FileWriter("logs.txt", true)) { // append = true
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String timestamp = LocalDateTime.now().format(formatter);
            writer.write("[" + timestamp + "] " + message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    

}

