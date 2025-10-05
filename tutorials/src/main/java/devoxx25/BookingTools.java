package devoxx25;

import dev.langchain4j.agent.tool.Tool;

public class BookingTools {

    @Tool("Returns booking details for a given booking number")
    public String getBookingDetails(String bookingNumber) {
        System.out.println("---Tool called: getBookingDetails with " + bookingNumber);
        Booking booking = new Booking(bookingNumber, "John Doe", "2025-10-15");
        return booking.toString();
    }

    @Tool("Cancels a booking by booking number")
    public String cancelBooking(String bookingNumber) {
        System.out.println("---Tool called: cancelBooking with " + bookingNumber);
        return "Booking " + bookingNumber + " has been successfully canceled.";
    }

    static class Booking {
        private final String number;
        private final String customer;
        private final String date;

        Booking(String number, String customer, String date) {
            this.number = number;
            this.customer = customer;
            this.date = date;
        }

        @Override
        public String toString() {
            return "Booking " + number + " for " + customer + " on " + date;
        }
    }
}
