package com.pelisbcknd.movielist.movies.service;

import com.pelisbcknd.movielist.common.error.BadRequestException;
import com.pelisbcknd.movielist.common.error.ExternalServiceException;
import com.pelisbcknd.movielist.common.error.NotFoundException;
import com.pelisbcknd.movielist.movies.dto.TmdbMovieDetail;
import com.pelisbcknd.movielist.movies.model.MovieCache;
import com.pelisbcknd.movielist.movies.repository.MovieCacheRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class MovieCacheService {

    private final MovieCacheRepository movieRepo;
    private final TmdbClient tmdbClient;

    public MovieCacheService(MovieCacheRepository movieRepo, TmdbClient tmdbClient) {
        this.movieRepo = movieRepo;
        this.tmdbClient = tmdbClient;
    }

    public MovieCache ensureCached(int tmdbId) {
        if (tmdbId <= 0) {
            throw new BadRequestException("tmdbId debe ser positivo");
        }

        return movieRepo.findByTmdbId(tmdbId).orElseGet(() -> {
            TmdbMovieDetail detail;
            try {
                detail = tmdbClient.getMovieDetail(tmdbId);
            } catch (NotFoundException e) {
                throw e;
            } catch (Exception e) {
                throw new ExternalServiceException("TMDB no está disponible o algo falló");
            }

            if (detail == null) {
                throw new ExternalServiceException("La respuesta de TMDB está vacía");
            }

            MovieCache m = new MovieCache();
            m.setTmdbId(detail.getId());
            m.setTitle(detail.getTitle());
            m.setOverview(detail.getOverview());
            m.setPosterPath(detail.getPosterPath());

            if (detail.getReleaseDate() != null && !detail.getReleaseDate().isBlank()) {
                try {
                    m.setReleaseDate(LocalDate.parse(detail.getReleaseDate()));
                } catch (Exception ex) {
                    throw new ExternalServiceException("TMDB devolvió un formato de fecha distinto");
                }
            }

            if (detail.getGenres() != null) {
                List<String> genres = detail.getGenres().stream().map(g -> g.getName()).toList();
                m.setGenres(genres);
            }

            return movieRepo.save(m);
        });
    }
}
