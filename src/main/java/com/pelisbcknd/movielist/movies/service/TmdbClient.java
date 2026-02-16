package com.pelisbcknd.movielist.movies.service;

import com.pelisbcknd.movielist.common.error.BadRequestException;
import com.pelisbcknd.movielist.common.error.ExternalServiceException;
import com.pelisbcknd.movielist.common.error.NotFoundException;
import com.pelisbcknd.movielist.movies.dto.TmdbMovieDetail;
import com.pelisbcknd.movielist.movies.dto.TmdbSearchResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TmdbClient {

    private final RestClient restClient;
    private final String apiKey;
    private final String baseUrl;

    public TmdbClient(
            RestClient restClient,
            @Value("${tmdb.apiKey}") String apiKey,
            @Value("${tmdb.baseUrl}") String baseUrl
    ) {
        this.restClient = restClient;
        this.apiKey = apiKey;
        this.baseUrl = baseUrl;
    }

    public TmdbSearchResponse searchMovies(String query, int page) {
        if (query == null || query.isBlank()) {
            throw new BadRequestException("Escribe algo");
        }
        if (page <= 0) {
            throw new BadRequestException("page debe ser >= 1");
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/search/movie")
                .queryParam("api_key", apiKey)
                .queryParam("query", query)
                .queryParam("page", page)
                .toUriString();

        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(TmdbSearchResponse.class);
        } catch (RestClientResponseException e) {
            throw new ExternalServiceException(buildTmdbErrorMessage("TMDB falló", e));
        } catch (Exception e) {
            throw new ExternalServiceException("No hay conexión con TMDB");
        }
    }

    public TmdbMovieDetail getMovieDetail(int tmdbId) {
        if (tmdbId <= 0) {
            throw new BadRequestException("tmdbId must be a positive integer");
        }

        String url = UriComponentsBuilder.fromHttpUrl(baseUrl)
                .path("/movie/{id}")
                .queryParam("api_key", apiKey)
                .buildAndExpand(tmdbId)
                .toUriString();

        try {
            return restClient.get()
                    .uri(url)
                    .retrieve()
                    .body(TmdbMovieDetail.class);
        } catch (RestClientResponseException e) {

            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new NotFoundException("No se encontró la película en TMDB (tmdbId=" + tmdbId + ")");
            }

            throw new ExternalServiceException(buildTmdbErrorMessage("Ha fallado los detalles de la película de TMDB", e));
        } catch (Exception e) {
            throw new ExternalServiceException("TMDB no está disponible");
        }
    }

    private String buildTmdbErrorMessage(String prefix, RestClientResponseException e) {
        return prefix + " (status=" + e.getRawStatusCode() + ")";
    }
}