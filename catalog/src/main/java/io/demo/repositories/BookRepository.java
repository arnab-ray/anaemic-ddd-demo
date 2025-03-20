package io.demo.repositories;

import io.demo.repositories.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findById(String bookId);

    Optional<Book> findByIsbn(String isbn);
}
