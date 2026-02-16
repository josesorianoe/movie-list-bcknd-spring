package com.pelisbcknd.movielist.movies.repository;

import com.pelisbcknd.movielist.movies.model.MovieCache;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface MovieCacheRepository extends MongoRepository<MovieCache, String> {

    Optional<MovieCache> findByTmdbId(Integer tmdbId);

    boolean existsByTmdbId(Integer tmdbId);

    void deleteByTmdbId(Integer tmdbId);

    Page<MovieCache> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}