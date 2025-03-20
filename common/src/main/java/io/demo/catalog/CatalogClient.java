package io.demo.catalog;

import io.demo.catalog.models.BookDetailsDTO;

public interface CatalogClient {
    BookDetailsDTO getBookDetails(String isbn);

    BookDetailsDTO getBookDetailsByListingId(String listingId);
}
