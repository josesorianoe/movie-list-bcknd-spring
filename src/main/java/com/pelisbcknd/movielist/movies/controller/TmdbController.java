package com.pelisbcknd.movielist.movies.controller;

import com.pelisbcknd.movielist.movies.dto.TmdbSearchResponse;
import com.pelisbcknd.movielist.movies.service.TmdbClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tmdb")
public class TmdbController {

    private final TmdbClient tmdbClient;

    public TmdbController(TmdbClient tmdbClient) {
        this.tmdbClient = tmdbClient;
    }

    @GetMapping("/search")
    public TmdbSearchResponse search(
            @RequestParam String query,
            @RequestParam(defaultValue = "1") int page
    ) {
        return tmdbClient.searchMovies(query, page);
    }
}