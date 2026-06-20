## FlyEase - Flight Booking System

SkyBook is a desktop flight booking and management system written in Java. It's built around a complete object-oriented domain model — users, flights, bookings, passengers, and payments — backed by file-based persistence, with a custom-built Swing interface.

It supports three roles - **Customer**, **Agent**, and **Administrator** - each with their own permissions, covering the full booking lifecycle from search to payment to cancellation.

### Features

- Search, browse, and book flights (Domestic & International, multiple seat classes)
- Role-based dashboards: Customer, Agent, and Administrator
- Booking management: book, cancel, pay, and generate e-tickets/itineraries
- Agent tools: book on behalf of customers, add flights, update schedules, generate reports
- Admin tools: manage users (create, enable/disable, change roles), manage all bookings/flights
- All data (users, flights, bookings, passengers) persists to plain text files

### Architecture

- **Domain model**: `User` (abstract) → `Customer`, `Agent`, `Administrator` | `Flight` → `DomesticFlight`, `InternationalFlight` | `Booking`, `Passenger`, `Payment`
- **`BookingSystem`** - core business logic / service layer used by every role
- **`FileManager`** - handles reading/writing all data to text files
- **`Gui` / `UI` / `Icons`** - custom dark-themed Swing interface, including a hand-drawn vector icon library (no external image assets or icon packs)

### Getting Started

```bash
git clone https://github.com/<your-username>/skybook.git
cd skybook
chmod +x run.sh
./run.sh
```

Requires Java JDK 17+.


### Built With

- Java (OOP — abstraction, inheritance, polymorphism, encapsulation)
- Java Swing & AWT `Graphics2D` (no external UI libraries)
- Plain text file I/O for persistence (no database)
