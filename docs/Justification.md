# Justification

## 1. Key Design Decisions

### Domain model and event hierarchy

The core entities are `User`, `Venue`, `Event`, and `Booking`. `Event` is an abstract base class with three concrete types:

- `FullySeatedEvent`: manages individual seats in a `Map<String, Seat>`.
- `SectionedSeatedEvent`: manages `Section` objects with their own capacities.
- `OpenSeatingEvent`: tracks a simple booked-count vs max capacity.

Each subclass implements the same contract:

- `canAccommodateBooking(int numberOfSeats)`
- `reserveSeats(Booking booking)`
- `getAvailableCapacity()`

This lets `BookingService` work with `Event` without knowing which seating model is used. **Liskov's Substition**: all event types substitute with `Event`.
**Open Closed Principle**: new types extend without modifying BookingService.

### BookingService and payment flow

`BookingService.createBooking(userId, eventId, numberOfSeats, creditCardNumber)`:

1. Validates inputs.
2. Loads `User`, `Event`, and ensures the event is in the future.
3. Loads the `Venue`.
4. Inside `synchronized (event)`:
   - Checks event capacity via `canAccommodateBooking`.
   - Applies a venue capacity guard by summing seats booked at that venue on the same date and comparing against `venue.totalCapacity`.
   - Processes payment via `PaymentGateway`.
   - On success, creates a `Booking` with `PaymentStatus.PAID`, calls `event.reserveSeats`, and saves both booking and event.

The ordering is intentional:
- Capacity checks happen before payment.
- Payment happens before seat reservation and persistence. (Only want to reserve seats for paying customers, but can also potentially reserve seats for 3 minutes once a customer is in the booking process, similar to what TicketMaster does.)
- The synchronized block makes the whole "check → pay → reserve → save" flow atomic per event in this in-memory version.

### PaymentGateway and repositories

The payment integration is abstracted by:

- `PaymentGateway` interface (`processPayment`).
- `PaymentResult` carrying `success`, `paymentId`, `failureReason`.
- `MockPaymentGateway` for deterministic tests. **Production would add idempotency keys, retries, circuit breakers.**

Repositories (`UserRepository`, `VenueRepository`, `EventRepository`, `BookingRepository`) hide storage details and are implemented with `ConcurrentHashMap`. They expose query-like methods to simulate SQL:

- `findFutureEvents`
- `findByUserId`, `findByVenueId`
- `findBookingsForPaidUsersAtVenue`
- `findUsersWithoutBookingsInVenue`

**Dependency Inversion Principle**: service depends on interfaces (not concrete storage/payment)
**Interface Segregation Principle**: each repository interface contains only relevant methods for its entity type.

## 2. Assumptions

- **Capacity semantics**
  - Primary rule: each event's own seating model defines its capacity.
  - Venue capacity is a secondary safety net: for a given venue and calendar date, the total seats booked across all events on that date must not exceed `venue.totalCapacity`. This approximates "concurrent capacity" without full time-range overlap logic.

- **Pricing**
  - Flat price per seat (e.g., 50 per seat). The `Event` parameter is kept in `calculateAmount` so event-specific pricing can be added later without changing the service API.

- **User data**
  - EBS stores minimal user data locally. In a real system, this would likely be synchronized from an external User Backend; the `UserRepository` abstraction allows that to be added later.

- **Concurrency**
  - `ConcurrentHashMap` is used for in-memory storage.
  - `synchronized (event)` is used to avoid race conditions between capacity checks and seat reservation. In production, this would be replaced by database transactions and locking/optimistic concurrency.

- **Scope**
  - No HTTP/controller layer; the focus is on domain model, business logic, repository pattern, and schema design.
  - Bookings are immutable once paid; cancellations/changes are out of scope.

## 3. Database Schema Reasoning

The `schema.sql` models the domain in a relational database:

- **users**
  - `userid` PK, `email` unique + indexed.
  - Minimal fields (id, name, email, createdAt).

- **venues**
  - `venueid` PK, `totalcapacity` with a `CHECK` constraint.
  - Index on `name` for search.

- **events**
  - Single-table inheritance:
    - `eventtype` discriminator (`FULLY_SEATED`, `SECTIONED`, `OPEN`).
    - `seatingdata` JSON column for seating configuration.
  - FK `venueid` → `venues`, `ON DELETE CASCADE`.
  - Indexes on `(venueid, eventdate)` and `eventdate` to support venue calendars and future-events queries.
  - `CHECK (eventdate > createdat)` to avoid past events at creation.

- **bookings**
  - `bookingid` PK; FKs to `users` and `events` (with `ON DELETE CASCADE` on `eventid`).
  - Fields: `numberofseats`, `seatdetails` JSON, `paymentstatus`, `paymentid`, `bookingdate`, `totalamount`.
  - Indexes on `(userid, bookingdate DESC)`, `eventid`, and `paymentstatus` to support:
    - User booking history.
    - Event-level and venue-level booking queries.
    - Filtering by payment status.

This schema supports the required repository methods and advanced queries (bookings for paid users at a venue, users without bookings at a venue) while matching the polymorphic event design through a discriminator + JSON seating data.
