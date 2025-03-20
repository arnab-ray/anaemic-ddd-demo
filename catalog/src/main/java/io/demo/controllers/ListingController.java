package io.demo.controllers;

import io.demo.catalog.models.AddBookDTO;
import io.demo.catalog.models.BookDetailsDTO;
import io.demo.services.ICatalogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/listing/v1")
public class ListingController {

    private final ICatalogService catalogService;

    @Autowired
    public ListingController(ICatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/book")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    void addBook(@RequestBody AddBookDTO addBookDTO) {
        catalogService.addToCatalog(addBookDTO.title(), addBookDTO.isbn(), addBookDTO.priceInPaise());
    }

    @GetMapping("/book")
    BookDetailsDTO get(
            @RequestParam(required = false) String listingId,
            @RequestParam(required = false) String isbn) {
        return listingId != null ? catalogService.searchById(listingId) : catalogService.searchByIsbn(isbn);
    }
}
