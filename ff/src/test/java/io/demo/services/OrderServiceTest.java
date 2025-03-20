package io.demo.services;

import io.demo.catalog.CatalogClient;
import io.demo.catalog.models.BookDetailsDTO;
import io.demo.exceptions.NotFoundException;
import io.demo.models.Order;
import io.demo.models.OrderItem;
import io.demo.publishers.KafkaProducer;
import io.demo.repositories.FFOrderItemRepository;
import io.demo.repositories.FFOrderRepository;
import io.demo.repositories.InventoryRepository;
import io.demo.repositories.ReservationRepository;
import io.demo.repositories.entities.FulfilOrder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static io.demo.models.Constants.FF_TOPIC;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class OrderServiceTest {
    private final FFOrderRepository ffOrderRepository = mock(FFOrderRepository.class);
    private final FFOrderItemRepository ffOrderItemRepository = mock(FFOrderItemRepository.class);
    private final KafkaProducer kafkaProducer = mock(KafkaProducer.class);
    private final InventoryRepository inventoryRepository = mock(InventoryRepository.class);
    private final ReservationRepository reservationRepository = mock(ReservationRepository.class);
    private final IWarehouseService warehouseService = new WarehouseService(inventoryRepository, reservationRepository);
    private final CatalogClient catalogClient = mock(CatalogClient.class);

    private final IOrderService orderService =
            new OrderService(ffOrderRepository, ffOrderItemRepository, kafkaProducer, warehouseService, catalogClient);

    @Nested
    class AcceptOrder {
        @Test
        void testWhenOrderIsPresent() {
            when(ffOrderRepository.findByOrderId("OD123")).thenReturn(Optional.of(getFulfilOrder()));

            orderService.acceptOrder(getOrder());
            verify(ffOrderRepository, times(0)).save(any());
            verify(ffOrderItemRepository, times(0)).save(any());
            verify(kafkaProducer, times(0)).sendMessage(any(), any(), any());
        }

        @Test
        void testWhenOrderIsAbsent() {
            when(ffOrderRepository.findByOrderId("OD123")).thenReturn(Optional.empty());

            orderService.acceptOrder(getOrder());

            // TODO: No easy way to test since fulfilOrderId is unknown
            verify(ffOrderRepository, times(1)).save(any(FulfilOrder.class));
            verify(ffOrderItemRepository, times(1)).saveAll(anyList());
            verify(kafkaProducer, times(1)).sendMessage(eq(FF_TOPIC), anyString(), anyString());
        }
    }

    @Nested
    class FulfillOrder {
        @BeforeEach
        void setUp() {
            when(catalogClient.getBookDetailsByListingId("LST123"))
                    .thenReturn(new BookDetailsDTO("LST123", "Flatland", "0990582930", 50000));
        }

        @Test
        void testWhenFulfilOrderIsAbsent() {
            when(ffOrderRepository.findById(123L)).thenReturn(Optional.empty());

            Exception e = Assertions.assertThrows(NotFoundException.class, () -> orderService.fulfillOrder(123L));
            assertThat(e.getMessage()).isEqualTo("fulfil order is absent!");
        }

        @Test
        void testWhenFulfilOrderItemsAreAbsent() {
            when(ffOrderRepository.findById(123L)).thenReturn(Optional.of(getFulfilOrder()));
            when(ffOrderItemRepository.findByFulfilOrderId("123")).thenReturn(List.of());

            Exception e = Assertions.assertThrows(NotFoundException.class, () -> orderService.fulfillOrder(123L));
            assertThat(e.getMessage()).isEqualTo("fulfil order items not found!");
        }
    }

    private FulfilOrder getFulfilOrder() {
        return FulfilOrder.builder()
                .fulfilOrderId("FF123")
                .orderId("OD123")
                .addressId("ADDR23")
                .build();
    }

    private Order getOrder() {
        return Order.builder()
                .orderId("OD123")
                .addressId("ADDR23")
                .orderItems(
                        List.of(
                                OrderItem.builder()
                                        .orderItemId("O1")
                                        .warehouseId("WH1")
                                        .listingId("LST123")
                                        .priceInPaise(34500)
                                        .quantity(23)
                                        .build(),
                                OrderItem.builder()
                                        .orderItemId("O2")
                                        .warehouseId("WH1")
                                        .listingId("LST124")
                                        .priceInPaise(54500)
                                        .quantity(12)
                                        .build()
                        ))
                .build();
    }
}
