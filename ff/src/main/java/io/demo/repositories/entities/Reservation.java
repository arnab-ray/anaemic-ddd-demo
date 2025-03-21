package io.demo.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reservations")
public class Reservation extends BaseEntity {
    @Column(name = "listing_id")
    private String listingId;

    @Column(name = "wh_id")
    private String warehouseId;

    @Column(name = "order_item_id")
    private String orderItemId;

    private Integer quantity;
}
