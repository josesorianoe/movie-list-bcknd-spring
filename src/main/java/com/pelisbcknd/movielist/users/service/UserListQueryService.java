package com.pelisbcknd.movielist.users.service;

import com.pelisbcknd.movielist.movies.model.MovieCache;
import com.pelisbcknd.movielist.movies.repository.MovieCacheRepository;
import com.pelisbcknd.movielist.users.dto.UserMovieListResponse;
import com.pelisbcknd.movielist.users.model.MovieListItem;
import com.pelisbcknd.movielist.users.model.User;
import com.pelisbcknd.movielist.users.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

@Service
public class UserListQueryService {

    private final UserRepository userRepo;
    private final MovieCacheRepository movieRepo;

    public UserListQueryService(UserRepository userRepo, MovieCacheRepository movieRepo) {
        this.userRepo = userRepo;
        this.movieRepo = movieRepo;
    }

    public List<UserMovieListResponse> getPending(
            String email,
            int page,
            int size,
            String title,
            Integer year,
            String genres
    ) {
        User user = userRepo.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<String> wantedGenres = parseGenres(genres);

        Stream<MovieListItem> stream = user.getPending().stream();

        return stream
                .map(item -> {
                    MovieCache movie = movieRepo.findByTmdbId(item.getTmdbId()).orElse(null);
                    if (movie == null) return null;

                    if (!matchesGenres(movie.getGenres(), wantedGenres)) return null;

                    return buildResponse(item, movie);
                })
                .filter(r -> r != null)
                .filter(r -> filterByTitle(r, title))
                .filter(r -> filterByYear(r, year))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    public List<UserMovieListResponse> getWatched(
            String email,
            int page,
            int size,
            String title,
            Integer year,
            Integer minRating,
            String genres
    ) {
        User user = userRepo.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        List<String> wantedGenres = parseGenres(genres);

        Stream<MovieListItem> stream = user.getWatched().stream();

        return stream
                .map(item -> {
                    MovieCache movie = movieRepo.findByTmdbId(item.getTmdbId()).orElse(null);
                    if (movie == null) return null;

                    if (!matchesGenres(movie.getGenres(), wantedGenres)) return null;

                    return buildResponse(item, movie);
                })
                .filter(r -> r != null)
                .filter(r -> filterByTitle(r, title))
                .filter(r -> filterByYear(r, year))
                .filter(r -> filterByMinRating(r, minRating))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }


    private UserMovieListResponse buildResponse(MovieListItem item, MovieCache movie) {
        if (movie == null) return null;

        UserMovieListResponse r = new UserMovieListResponse();
        r.setTmdbId(movie.getTmdbId());
        r.setTitle(movie.getTitle());
        r.setPosterPath(movie.getPosterPath());

        if (movie.getReleaseDate() != null) {
            r.setYear(movie.getReleaseDate().getYear());
        }

        r.setAddedAt(item.getAddedAt());
        r.setWatchedAt(item.getWatchedAt());
        r.setRating(item.getRating());
        r.setNotes(item.getNotes());

        return r;
    }

    private boolean filterByTitle(UserMovieListResponse r, String title) {
        if (title == null || title.isBlank()) return true;
        return r.getTitle().toLowerCase(Locale.ROOT)
                .contains(title.toLowerCase(Locale.ROOT));
    }

    private boolean filterByYear(UserMovieListResponse r, Integer year) {
        if (year == null) return true;
        return year.equals(r.getYear());
    }

    private boolean filterByMinRating(UserMovieListResponse r, Integer minRating) {
        if (minRating == null) return true;
        return r.getRating() != null && r.getRating() >= minRating;
    }

    private List<String> parseGenres(String genresParam) {
        if (genresParam == null || genresParam.isBlank()) return List.of();

        return Arrays.stream(genresParam.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(s -> s.toLowerCase(Locale.ROOT))
                .toList();
    }

    private boolean matchesGenres(List<String> movieGenres, List<String> wantedGenres) {
        if (wantedGenres.isEmpty()) return true;
        if (movieGenres == null || movieGenres.isEmpty()) return false;

        for (String mg : movieGenres) {
            if (mg != null && wantedGenres.contains(mg.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }
}