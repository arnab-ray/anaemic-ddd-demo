package io.demo.repositories.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "inventories")
public class Inventory extends BaseEntity {
    // TODO: What is the point of such data containers? Are we leveraging the power of OOP appropriately?
    @Column(name = "listing_id")
    private String listingId;

    @Column(name = "wh_id")
    private String warehouseId;

    @Column(name = "total_quantity")
    private Long totalQuantity;

    @Column(name = "available_quantity")
    private Long availableQuantity;
}
