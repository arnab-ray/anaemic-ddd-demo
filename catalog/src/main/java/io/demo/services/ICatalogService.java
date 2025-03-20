package io.demo.services;

import io.demo.catalog.models.BookDetailsDTO;

public interface ICatalogService {
    BookDetailsDTO searchByIsbn(String isbn);

    BookDetailsDTO searchById(String listingId);

    void addToCatalog(String title, String isbn, Integer price);
}
