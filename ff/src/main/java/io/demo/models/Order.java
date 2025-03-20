package io.demo.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {
    private String orderId;
    private String addressId;
    private List<OrderItem> orderItems;
    private PaymentDetails paymentDetails;
    private String offerId;
    private String customerId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentDetails {
        private String paymentMode;
        private String cardNumberMasked;
        private String accountNumberMasked;
    }
}
