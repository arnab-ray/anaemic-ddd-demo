package io.demo.catalog.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AddBookDTO(String title, String isbn, Integer priceInPaise) {}
