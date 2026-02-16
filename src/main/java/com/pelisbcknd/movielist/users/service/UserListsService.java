package com.pelisbcknd.movielist.users.service;

import com.pelisbcknd.movielist.common.error.NotFoundException;
import com.pelisbcknd.movielist.movies.service.MovieCacheService;
import com.pelisbcknd.movielist.users.dto.AddPendingRequest;
import com.pelisbcknd.movielist.users.dto.AddWatchedRequest;
import com.pelisbcknd.movielist.users.dto.PatchWatchedRequest;
import com.pelisbcknd.movielist.users.model.MovieListItem;
import com.pelisbcknd.movielist.users.model.User;
import com.pelisbcknd.movielist.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class UserListsService {

    private final UserRepository userRepo;
    private final MovieCacheService cacheService;

    public UserListsService(UserRepository userRepo, MovieCacheService cacheService) {
        this.userRepo = userRepo;
        this.cacheService = cacheService;
    }

    public void addPending(String email, AddPendingRequest req) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));

        cacheService.ensureCached(req.getTmdbId());

        boolean alreadyInPending = user.getPending().stream().anyMatch(i -> i.getTmdbId() == req.getTmdbId());
        boolean alreadyInWatched = user.getWatched().stream().anyMatch(i -> i.getTmdbId() == req.getTmdbId());

        if (alreadyInWatched) throw new IllegalArgumentException("Película ya en vistas");
        if (alreadyInPending) throw new IllegalArgumentException("Película ya en pendientes");

        MovieListItem item = new MovieListItem();
        item.setTmdbId(req.getTmdbId());
        item.setAddedAt(Instant.now());

        user.getPending().add(item);
        userRepo.save(user);
    }

    public void addWatched(String email, AddWatchedRequest req) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        cacheService.ensureCached(req.getTmdbId());

        boolean alreadyInWatched = user.getWatched().stream().anyMatch(i -> i.getTmdbId() == req.getTmdbId());
        if (alreadyInWatched) throw new IllegalArgumentException("Película ya en vistas");

        user.getPending().removeIf(i -> i.getTmdbId() == req.getTmdbId());

        MovieListItem item = new MovieListItem();
        item.setTmdbId(req.getTmdbId());
        item.setAddedAt(Instant.now());
        item.setWatchedAt(Instant.now());
        item.setRating(req.getRating());
        item.setNotes(req.getNotes());

        user.getWatched().add(item);
        userRepo.save(user);
    }

    public void deletePending(String email, int tmdbId) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        boolean removed = user.getPending().removeIf(i -> i.getTmdbId() == tmdbId);
        if (!removed) throw new NotFoundException("Película no encontrada en pendientes");

        userRepo.save(user);
    }

    public void deleteWatched(String email, int tmdbId) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        boolean removed = user.getWatched().removeIf(i -> i.getTmdbId() == tmdbId);
        if (!removed) throw new NotFoundException("Película no encontrada en vistas");

        userRepo.save(user);
    }

    public void patchWatched(String email, int tmdbId, PatchWatchedRequest req) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        MovieListItem item = user.getWatched().stream()
                .filter(i -> i.getTmdbId() == tmdbId)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Película no encontrada en vistas"));

        if (req.getRating() != null) item.setRating(req.getRating());
        if (req.getNotes() != null) item.setNotes(req.getNotes());

        userRepo.save(user);
    }
}