package com.pelisbcknd.movielist.users.dto;

import jakarta.validation.constraints.Min;

public class AddPendingRequest {
    @Min(1)
    private int tmdbId;

    public int getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(int tmdbId) {
        this.tmdbId = tmdbId;
    }
}