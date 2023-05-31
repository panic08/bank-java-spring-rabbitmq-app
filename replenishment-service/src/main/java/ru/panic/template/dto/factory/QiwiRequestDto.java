package ru.panic.template.dto.factory;

import lombok.Data;

@Data
public class QiwiRequestDto {
    private String hookId;
    private String messageId;
    private Payment payment;
    private Boolean test;
    private String version;
    private String hash;
    @Data
    public static class Payment{
    private String txnId;
    private String account;
    private String signFields;
    private Integer personId;
    private String date;
    private String errorCode;
    private String type;
    private String status;
    private Integer provider;
    private String comment;
    private Sum sum;
    private Commission commission;
    private Total total;

    @Data
    public static class Sum {
        private Double amount;
        private Integer currency;
    }
    @Data
        public static class Commission{
        private Double amount;
        private Integer currency;
    }
    @Data
        public static class Total{
        private Double amount;
        private Integer currency;
    }
    }
}
