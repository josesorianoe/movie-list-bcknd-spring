package com.pelisbcknd.movielist.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class TmdbSearchResponse {
    private int page;

    @JsonProperty("total_pages")
    private int totalPages;

    private List<TmdbMovieResult> results;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public List<TmdbMovieResult> getResults() {
        return results;
    }

    public void setResults(List<TmdbMovieResult> results) {
        this.results = results;
    }
}