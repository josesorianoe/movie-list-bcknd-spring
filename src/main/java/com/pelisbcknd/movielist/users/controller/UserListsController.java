package com.pelisbcknd.movielist.users.controller;

import com.pelisbcknd.movielist.common.dto.MessageResponse;
import com.pelisbcknd.movielist.users.dto.AddPendingRequest;
import com.pelisbcknd.movielist.users.dto.AddWatchedRequest;
import com.pelisbcknd.movielist.users.dto.PatchWatchedRequest;
import com.pelisbcknd.movielist.users.service.UserListsService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users/me")
public class UserListsController {

    private final UserListsService listsService;

    public UserListsController(UserListsService listsService) {
        this.listsService = listsService;
    }

    @PostMapping("/pending")
    public MessageResponse addPending(Authentication auth, @Valid @RequestBody AddPendingRequest req) {
        listsService.addPending(auth.getName(), req);
        return new MessageResponse("Película añadida a pendientes");
    }

    @PostMapping("/watched")
    public MessageResponse addWatched(Authentication auth, @Valid @RequestBody AddWatchedRequest req) {
        listsService.addWatched(auth.getName(), req);
        return new MessageResponse("Película añadida a vistas");
    }

    @DeleteMapping("/pending/{tmdbId}")
    public MessageResponse deletePending(Authentication auth, @PathVariable int tmdbId) {
        listsService.deletePending(auth.getName(), tmdbId);
        return new MessageResponse("Película eliminada de pendientes");
    }

    @DeleteMapping("/watched/{tmdbId}")
    public MessageResponse deleteWatched(Authentication auth, @PathVariable int tmdbId) {
        listsService.deleteWatched(auth.getName(), tmdbId);
        return new MessageResponse("Película eliminada de vistas");
    }

    @PatchMapping("/watched/{tmdbId}")
    public MessageResponse patchWatched(
            Authentication auth,
            @PathVariable int tmdbId,
            @Valid @RequestBody PatchWatchedRequest req
    ) {
        listsService.patchWatched(auth.getName(), tmdbId, req);
        return new MessageResponse("Película actualizada");
    }
}