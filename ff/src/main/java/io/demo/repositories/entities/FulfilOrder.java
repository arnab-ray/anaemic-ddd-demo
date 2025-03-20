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
@Table(name = "ff_orders")
public class FulfilOrder extends BaseEntity {
    @Column(name = "order_id")
    private String orderId;

    @Column(name = "fulfil_order_id")
    private String fulfilOrderId;

    @Column(name = "address_id")
    private String addressId;
}
