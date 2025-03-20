package io.demo.services;

import io.demo.catalog.models.BookDetailsDTO;
import io.demo.repositories.entities.Book;

public class BookDetailsMapper {
    public static BookDetailsDTO getBookDetails(Book book) {
        return new BookDetailsDTO(book.getListingId(), book.getTitle(), book.getIsbn(), book.getPriceInPaise());
    }
}
