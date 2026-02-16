package com.pelisbcknd.movielist.movies.service;

import com.pelisbcknd.movielist.common.dto.PageResponse;
import com.pelisbcknd.movielist.common.error.ConflictException;
import com.pelisbcknd.movielist.common.error.NotFoundException;
import com.pelisbcknd.movielist.movies.dto.CreateMovieRequest;
import com.pelisbcknd.movielist.movies.dto.PatchMovieRequest;
import com.pelisbcknd.movielist.movies.dto.UpdateMovieRequest;
import com.pelisbcknd.movielist.movies.model.MovieCache;
import com.pelisbcknd.movielist.movies.repository.MovieCacheRepository;
import com.pelisbcknd.movielist.users.repository.UserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class MovieCrudService {

    private final MovieCacheRepository repo;
    private final MongoTemplate mongoTemplate;
    private final UserRepository userRepo;

    public MovieCrudService(MovieCacheRepository repo, MongoTemplate mongoTemplate, UserRepository userRepo) {
        this.repo = repo;
        this.mongoTemplate = mongoTemplate;
        this.userRepo = userRepo;
    }

    public PageResponse<MovieCache> list(
            int page,
            int size,
            String title,
            Integer year,
            String genresParam
    ) {
        Query query = new Query();

        // title
        if (title != null && !title.isBlank()) {
            query.addCriteria(Criteria.where("title").regex(title, "i"));
        }

        // year
        if (year != null) {
            LocalDate start = LocalDate.of(year, 1, 1);
            LocalDate end = LocalDate.of(year + 1, 1, 1);
            query.addCriteria(Criteria.where("releaseDate").gte(start).lt(end));
        }

        // genres
        if (genresParam != null && !genresParam.isBlank()) {
            List<String> genres = Arrays.stream(genresParam.split(","))
                    .map(String::trim)
                    .toList();

            query.addCriteria(Criteria.where("genres").in(genres));
        }

        long total = mongoTemplate.count(query, MovieCache.class);

        query.with(Sort.by(Sort.Direction.DESC, "updatedAt"));
        query.skip((long) page * size).limit(size);

        List<MovieCache> content = mongoTemplate.find(query, MovieCache.class);

        int totalPages = (int) Math.ceil((double) total / size);

        return new PageResponse<>(content, page, size, total, totalPages);
    }


    public MovieCache getByTmdbId(int tmdbId) {
        return repo.findByTmdbId(tmdbId)
                .orElseThrow(() -> new NotFoundException("No se encontró película"));
    }

    public MovieCache create(CreateMovieRequest req) {
        if (repo.existsByTmdbId(req.getTmdbId())) {
            throw new ConflictException("Película ya existente");
        }
        MovieCache m = new MovieCache();
        m.setTmdbId(req.getTmdbId());
        applyAllFields(m, req.getTitle(), req.getOverview(), req.getReleaseDate(), req.getGenres(), req.getPosterPath());
        return repo.save(m);
    }

    public MovieCache update(int tmdbId, UpdateMovieRequest req) {
        MovieCache m = repo.findByTmdbId(tmdbId)
                .orElseThrow(() -> new NotFoundException("No se encontró película"));
        applyAllFields(m, req.getTitle(), req.getOverview(), req.getReleaseDate(), req.getGenres(), req.getPosterPath());
        return repo.save(m);
    }

    public MovieCache patch(int tmdbId, PatchMovieRequest req) {
        MovieCache m = repo.findByTmdbId(tmdbId).orElseThrow(() -> new NotFoundException("No se encontró película"));

        if (req.getTitle() != null) m.setTitle(req.getTitle());
        if (req.getOverview() != null) m.setOverview(req.getOverview());
        if (req.getPosterPath() != null) m.setPosterPath(req.getPosterPath());
        if (req.getGenres() != null) m.setGenres(req.getGenres());
        if (req.getReleaseDate() != null) {
            if (req.getReleaseDate().isBlank()) m.setReleaseDate(null);
            else m.setReleaseDate(LocalDate.parse(req.getReleaseDate()));
        }

        return repo.save(m);
    }

    public void delete(int tmdbId) {
        if (!repo.existsByTmdbId(tmdbId)) {
            throw new NotFoundException("No se encontró película");
        }

        // Impedir borrado si está referenciada por algún usuario
        boolean referenced = userRepo.existsByPendingTmdbId(tmdbId) || userRepo.existsByWatchedTmdbId(tmdbId);

        if (referenced) {
            throw new ConflictException("No se puede borrar la película porque está referenciada en las listas de otro usuario");
        }

        repo.deleteByTmdbId(tmdbId);
    }

    private void applyAllFields(MovieCache m, String title, String overview, String releaseDate, java.util.List<String> genres, String posterPath) {
        m.setTitle(title);
        m.setOverview(overview);
        m.setPosterPath(posterPath);
        m.setGenres(genres);

        if (releaseDate != null && !releaseDate.isBlank()) {
            m.setReleaseDate(LocalDate.parse(releaseDate));
        } else {
            m.setReleaseDate(null);
        }
    }
}