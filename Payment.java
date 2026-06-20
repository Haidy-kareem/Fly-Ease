package oopflightbookingsystem.src;

public class Payment {

    private int paymentId;
    private int bookingId;
    private double amount;
    private String method;
    private String status;
    private String transactionDate;

    private static final String DELIMITER = "|";

    public Payment(int paymentId, int bookingId, double amount, String method, String status, String transactionDate) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = (status != null) ? status : "Pending";
        this.transactionDate = transactionDate;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setStatus(String status) {
        if (status != null && !status.isEmpty()) {
            this.status = status;
        }
    }

    // Simulate the payment process
    public boolean processPayment() {
        // A more realistic simulation of processing (or connect to a real payment service)
        if (method.equalsIgnoreCase("Credit Card")) {
            processCreditCardPayment(amount);
        } else if (method.equalsIgnoreCase("Bank")) {
            processBankPayment(amount);
        } else {
            System.out.println("Unsupported payment method: " + method);
            return false;
        }
        System.out.println("Processing payment ID: " + paymentId);
        boolean success = Math.random() > 0.1;
        if (success) {
            setStatus("Completed");
            System.out.println("Payment successful.");
        } else {
            setStatus("Failed");
            System.out.println("Payment failed.");
        }
        return success;
    }

    // Private method to handle credit card payment
    private void processCreditCardPayment(double amount) {
        // Simulate credit card payment processing
        System.out.println("Processing credit card payment of " + amount);
    }

    // Private method to handle PayPal payment
    private void processBankPayment(double amount) {
        // Simulate PayPal payment processing
        System.out.println("Processing Bank payment of " + amount);
    }
    
    // Validate payment details (e.g., amount > 0, valid method)
    public boolean validatePaymentDetails() {
        if (amount <= 0) {
            System.out.println("Error: Amount must be greater than zero.");
            return false;
        }
        if (method == null || method.isEmpty()) {
            System.out.println("Error: Payment method is required.");
            return false;
        }
        if (status == null || status.isEmpty()) {
            System.out.println("Error: Payment status is required.");
            return false;
        }
        return true;
    }

    // Update payment status
    public void updateStatus(String newStatus) {
        if (newStatus != null && !newStatus.isEmpty()) {
            setStatus(newStatus);
            System.out.println("Payment status updated to: " + newStatus);
        } else {
            System.out.println("Error: Invalid status update.");
        }
    }

    // File saving (optional)
    public String toFileString() {
        return String.join(DELIMITER,
                String.valueOf(paymentId),
                String.valueOf(bookingId),
                String.valueOf(amount),
                method != null ? method : "",
                status != null ? status : "",
                transactionDate != null ? transactionDate : ""
        );
    }

    public static Payment fromFileString(String line) {
        String[] parts = line.split("\\" + DELIMITER, -1);
        if (parts.length == 6) {
            try {
                int paymentId = Integer.parseInt(parts[0]);
                int bookingId = Integer.parseInt(parts[1]);
                double amount = Double.parseDouble(parts[2]);
                String method = parts[3];
                String status = parts[4];
                String transactionDate = parts[5];

                return new Payment(paymentId, bookingId, amount, method, status, transactionDate);

            } catch (NumberFormatException e) {
                System.err.println("Error parsing payment data: " + e.getMessage());
                return null;
            }
        } else {
            System.err.println("Invalid payment data format.");
            return null;
        }
    }

    @Override
    public String toString() {
        return "Payment{" +
               "ID=" + paymentId +
               ", BookingID=" + bookingId +
               ", Amount=$%.2f" + String.format("%.2f", amount) +
               ", Method=" + method +
               ", Status=" + status +
               ", Date=" + transactionDate +
               "}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return paymentId == payment.paymentId;
    }

}


