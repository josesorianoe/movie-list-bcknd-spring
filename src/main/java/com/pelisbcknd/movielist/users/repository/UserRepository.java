package com.pelisbcknd.movielist.users.repository;

import com.pelisbcknd.movielist.users.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByPendingTmdbId(int tmdbId);

    boolean existsByWatchedTmdbId(int tmdbId);
}