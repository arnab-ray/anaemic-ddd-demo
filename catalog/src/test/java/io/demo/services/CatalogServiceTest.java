package io.demo.services;

import io.demo.exceptions.NotFoundException;
import io.demo.repositories.BookRepository;
import io.demo.repositories.entities.Book;
import io.demo.utils.BookUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CatalogServiceTest {
    private final BookRepository bookRepository = mock(BookRepository.class);
    private final BookUtils bookUtils = new BookUtils();

    private final ICatalogService catalogService = new CatalogService(bookRepository, bookUtils);

    @Nested
    class SearchByIsbn {
        @Test
        void testWithInvalidIsbn() {
            Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> catalogService.searchByIsbn("123"));
            assertThat(e.getMessage()).isEqualTo("Invalid isbn");
        }

        @Test
        void testWhenBookIsAbsent() {
            when(bookRepository.findByIsbn("0990582930")).thenReturn(Optional.empty());
            Exception e = Assertions.assertThrows(NotFoundException.class, () -> catalogService.searchByIsbn("0990582930"));
            assertThat(e.getMessage()).isEqualTo("Book not found");
        }

        @Test
        void testWhenBookIsPresent() {
            when(bookRepository.findByIsbn("0990582930")).thenReturn(Optional.of(getBook()));
            var response = catalogService.searchByIsbn("0990582930");
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("listingId")
                    .isEqualTo(Book.builder().isbn("0990582930").title("Flatland").priceInPaise(50000).build());
        }
    }

    @Nested
    class SearchById {
        @Test
        void testWhenBookIsAbsent() {
            when(bookRepository.findById("23")).thenReturn(Optional.empty());
            Exception e = Assertions.assertThrows(NotFoundException.class, () -> catalogService.searchById("23"));
            assertThat(e.getMessage()).isEqualTo("Book not found");
        }

        @Test
        void testWhenBookIsPresent() {
            when(bookRepository.findById("23")).thenReturn(Optional.of(getBook()));
            var response = catalogService.searchById("23");
            assertThat(response)
                    .usingRecursiveComparison()
                    .ignoringFields("listingId")
                    .isEqualTo(Book.builder().isbn("0990582930").title("Flatland").priceInPaise(50000).build());
        }
    }

    @Nested
    class AddToCatalog {
        @Test
        void testWithInvalidIsbn() {
            Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> catalogService.addToCatalog("Flatland", "123", 50000));
            assertThat(e.getMessage()).isEqualTo("Invalid isbn");
        }

        @Test
        void testWhenBookIsPresent() {
            when(bookRepository.findByIsbn("0990582930")).thenReturn(Optional.of(getBook()));

            catalogService.addToCatalog("Flatland", "0990582930", 50000);
            verify(bookRepository, times(0)).save(eq(getBook()));
        }

        @Test
        void testWhenBookIsAbsent() {
            when(bookRepository.findByIsbn("0990582930")).thenReturn(Optional.empty());

            catalogService.addToCatalog("Flatland", "0990582930", 50000);
            verify(bookRepository, times(1)).save(eq(getBook()));
        }
    }

    private Book getBook() {
        return Book.builder().isbn("0990582930").title("Flatland").priceInPaise(50000).build();
    }
}
