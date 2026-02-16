package com.pelisbcknd.movielist.movies.controller;

import com.pelisbcknd.movielist.common.dto.MessageResponse;
import com.pelisbcknd.movielist.common.dto.PageResponse;
import com.pelisbcknd.movielist.movies.dto.CreateMovieRequest;
import com.pelisbcknd.movielist.movies.dto.PatchMovieRequest;
import com.pelisbcknd.movielist.movies.dto.UpdateMovieRequest;
import com.pelisbcknd.movielist.movies.model.MovieCache;
import com.pelisbcknd.movielist.movies.service.MovieCrudService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/movies")
public class MovieCrudController {

    private final MovieCrudService service;

    public MovieCrudController(MovieCrudService service) {
        this.service = service;
    }

    @GetMapping
    public PageResponse<MovieCache> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String genres
    ) {
        return service.list(page, size, title, year, genres);
    }

    @GetMapping("/{tmdbId}")
    public MovieCache get(@PathVariable int tmdbId) {
        return service.getByTmdbId(tmdbId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MovieCache create(@Valid @RequestBody CreateMovieRequest req) {
        return service.create(req);
    }

    @PutMapping("/{tmdbId}")
    public MovieCache update(@PathVariable int tmdbId, @Valid @RequestBody UpdateMovieRequest req) {
        return service.update(tmdbId, req);
    }

    @PatchMapping("/{tmdbId}")
    public MovieCache patch(@PathVariable int tmdbId, @RequestBody PatchMovieRequest req) {
        return service.patch(tmdbId, req);
    }

    @DeleteMapping("/{tmdbId}")
    public MessageResponse delete(@PathVariable int tmdbId) {
        service.delete(tmdbId);
        return new MessageResponse("Película eliminada");
    }
}