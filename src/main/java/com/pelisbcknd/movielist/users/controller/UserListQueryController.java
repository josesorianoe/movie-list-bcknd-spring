package com.pelisbcknd.movielist.users.controller;

import com.pelisbcknd.movielist.users.dto.UserMovieListResponse;
import com.pelisbcknd.movielist.users.service.UserListQueryService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me")
public class UserListQueryController {

    private final UserListQueryService queryService;

    public UserListQueryController(UserListQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping("/pending")
    public List<UserMovieListResponse> getPending(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String genres
    ) {
        return queryService.getPending(
                auth.getName(),
                page,
                size,
                title,
                year,
                genres
        );
    }

    @GetMapping("/watched")
    public List<UserMovieListResponse> getWatched(
            Authentication auth,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) String genres
    ) {
        return queryService.getWatched(
                auth.getName(),
                page,
                size,
                title,
                year,
                minRating,
                genres
        );
    }
}
