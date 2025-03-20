package io.demo.services;

import io.demo.catalog.models.BookDetailsDTO;
import io.demo.exceptions.NotFoundException;
import io.demo.repositories.BookRepository;
import io.demo.repositories.entities.Book;
import io.demo.utils.BookUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatalogService implements ICatalogService {
    private final BookRepository bookRepository;
    private final BookUtils bookUtils;

    @Autowired
    public CatalogService(BookRepository bookRepository, BookUtils bookUtils) {
        this.bookRepository = bookRepository;
        this.bookUtils = bookUtils;
    }

    @Override
    public BookDetailsDTO searchByIsbn(String isbn) {
        var isValidIsbn = bookUtils.isValid(isbn);
        if (!isValidIsbn) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        var maybeBook = bookRepository.findByIsbn(isbn);

        if (maybeBook.isEmpty()) {
            throw new NotFoundException("Book not found");
        }

        return BookDetailsMapper.getBookDetails(maybeBook.get());
    }

    @Override
    public BookDetailsDTO searchById(String listingId) {
        var maybeBook = bookRepository.findById(listingId);

        if (maybeBook.isEmpty()) {
            throw new NotFoundException("Book not found");
        }

        return BookDetailsMapper.getBookDetails(maybeBook.get());
    }

    @Override
    public void addToCatalog(String title, String isbn, Integer price) {
        var isValidIsbn = bookUtils.isValid(isbn);
        if (!isValidIsbn) {
            throw new IllegalArgumentException("Invalid isbn");
        }

        var maybeBook = bookRepository.findByIsbn(isbn);
        if (maybeBook.isEmpty()) {
            Book book = Book.builder().title(title).isbn(isbn).priceInPaise(price).build();
            bookRepository.save(book);
        }
    }
}
