-- Single table inheritance for events with JSON seating data
CREATE TABLE users (
    userid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    createdat TIMESTAMP NOT NULL,
    INDEX idx_email (email)
);

CREATE TABLE venues (
    venueid UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255) NOT NULL,
    totalcapacity INT CHECK (totalcapacity >= 0),
    createdat TIMESTAMP NOT NULL,
    INDEX idx_name (name)
);

CREATE TABLE events (
    eventid UUID PRIMARY KEY,
    venueid UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    eventdate TIMESTAMP NOT NULL,
    eventtype VARCHAR(50) NOT NULL, -- 'FULLY_SEATED', 'SECTIONED', 'OPEN'
    seatingdata JSON NOT NULL,
    createdat TIMESTAMP NOT NULL,
    FOREIGN KEY (venueid) REFERENCES venues(venueid) ON DELETE CASCADE,
    CHECK (eventdate > createdat),
    INDEX idx_venue_date (venueid, eventdate),
    INDEX idx_eventdate (eventdate)
);

CREATE TABLE bookings (
    bookingid UUID PRIMARY KEY,
    userid UUID NOT NULL,
    eventid UUID NOT NULL,
    numberofseats INT NOT NULL,
    seatdetails JSON,
    paymentstatus VARCHAR(20) NOT NULL,
    paymentid VARCHAR(255),
    bookingdate TIMESTAMP NOT NULL,
    totalamount DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (userid) REFERENCES users(userid),
    FOREIGN KEY (eventid) REFERENCES events(eventid) ON DELETE CASCADE,
    INDEX idx_user_date (userid, bookingdate DESC),
    INDEX idx_event (eventid),
    INDEX idx_paymentstatus (paymentstatus)
);
