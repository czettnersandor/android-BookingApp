package com.czettner.bookingapp;


public class Book {

    private String title;
    private String subtitle;
    private String infoLink;
    private String authors;

    public Book(String title, String subtitle, String authors) {
        this.title = title;
        this.subtitle = subtitle;
        this.authors = authors;
    }

    // Getters and setters
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setInfoLink(String link) {
        infoLink = link;
    }

    public String getInfoLink() {
        return infoLink;
    }

    public String getAuthors() {
        return authors;
    }
}
