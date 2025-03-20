package io.demo.repositories.entities;

import io.demo.models.OrderItemState;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ff_order_items")
public class FulfilOrderItem extends BaseEntity {
    @Column(name = "fulfil_order_id")
    private String fulfilOrderId;

    @Column(name = "order_item_id")
    private String orderItemId;

    @Column(name = "price")
    private Integer priceInPaise;

    private Integer quantity;

    @Column(name = "listing_id")
    private String listingId;

    private String warehouse;

    @Enumerated(value = EnumType.STRING)
    private OrderItemState status;
}
