package com.pelisbcknd.movielist.users.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public class AddWatchedRequest {
    @Min(1)
    private int tmdbId;

    @Min(0)
    @Max(10)
    private Integer rating;

    private String notes;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
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
