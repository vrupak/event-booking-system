package com.eventbookingsystem.model;

public class Section {
    private String sectionName;
    private int totalSeats;
    private int availableSeats;

    public Section() {}

    public Section(String sectionName, int totalSeats, int availableSeats) {
        this.sectionName = sectionName;
        this.totalSeats = totalSeats;
        this.availableSeats = availableSeats;
    }

    public String getSectionName() { return sectionName; }
    public void setSectionName(String sectionName) { this.sectionName = sectionName; }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getAvailableSeats() { return availableSeats; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
}
