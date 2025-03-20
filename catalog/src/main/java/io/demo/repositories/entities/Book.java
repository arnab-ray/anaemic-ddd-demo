package io.demo.repositories.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book extends Listing {
    private String title;

    @Column(name = "price")
    private Integer priceInPaise;

    private String isbn;

    public String getListingId() {
        return super.getId();
    }
}
