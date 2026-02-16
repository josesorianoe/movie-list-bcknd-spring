package com.pelisbcknd.movielist.users.model;

import java.time.Instant;

public class MovieListItem {

    private Integer tmdbId;
    private Instant addedAt;
    private Instant watchedAt;
    private Integer rating;
    private String notes;

    public MovieListItem() {
    }

    public MovieListItem(Integer tmdbId, Instant addedAt) {
        this.tmdbId = tmdbId;
        this.addedAt = addedAt;
    }

    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public Instant getAddedAt() {
        return addedAt;
    }

    public void setAddedAt(Instant addedAt) {
        this.addedAt = addedAt;
    }

    public Instant getWatchedAt() {
        return watchedAt;
    }

    public void setWatchedAt(Instant watchedAt) {
        this.watchedAt = watchedAt;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}