package ru.panic.template.endpoint;

import jakarta.xml.bind.JAXBElement;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.panic.template.dto.PreWithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalResponseDto;
import ru.panic.template.service.impl.WithdrawalServiceImpl;

import javax.xml.namespace.QName;

@Endpoint
@RestController
public class WithdrawalEndpoint {
    public WithdrawalEndpoint(WithdrawalServiceImpl withdrawalService) {
        this.withdrawalService = withdrawalService;
    }
    private static final String WITHDRAWAL_NAMESPACE_URI = "http://localhost/WithdrawalEndpoint";
    private static final String PRE_WITHDRAWAL_NAMESPACE_URI = "http://localhost/PreWithdrawalEndpoint";

    private final WithdrawalServiceImpl withdrawalService;
    @PayloadRoot(namespace = WITHDRAWAL_NAMESPACE_URI, localPart = "WithdrawalRequest")
    @ResponsePayload
    private JAXBElement<WithdrawalResponseDto> withdrawalEndpoint(@RequestPayload JAXBElement<WithdrawalRequestDto> request){
        return new JAXBElement<>(new QName(WITHDRAWAL_NAMESPACE_URI, "WithdrawalResponse"), WithdrawalResponseDto.class, withdrawalService.handleWithdrawal(request.getValue()));
    }
    @PayloadRoot(namespace = PRE_WITHDRAWAL_NAMESPACE_URI, localPart = "PreWithdrawalRequest")
    @ResponsePayload
    private JAXBElement<WithdrawalResponseDto> preWithdrawalEndpoint(@RequestPayload JAXBElement<PreWithdrawalRequestDto> request){
        return new JAXBElement<>(new QName(WITHDRAWAL_NAMESPACE_URI, "PreWithdrawalResponse"), WithdrawalResponseDto.class, withdrawalService.handleSuccessWithdrawal(request.getValue()));
    }
}
