package ru.panic.template.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class QiwiTransferRequestDto {
    private Long id;
    private Sum sum;
    private PaymentMethod paymentMethod;
    private String comment;
    private Fields fields;
    @AllArgsConstructor
    @Data
    public static class Sum{
        private Double amount;
        private Integer currency;
    }
    @AllArgsConstructor
    @Data
    public static class PaymentMethod{
        private String type;
        private Integer accountId;
    }
    @AllArgsConstructor
    @Data
    public static class Fields{
        private String account;
    }

}
