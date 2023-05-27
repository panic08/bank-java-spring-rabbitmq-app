package ru.panic.template.endpoint;

import jakarta.xml.bind.JAXBElement;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ru.panic.template.dto.*;
import ru.panic.template.service.AuthorizeService;

import javax.xml.namespace.QName;

@Endpoint
@RestController
@Slf4j
public class AuthorizeEndpoint {
    public AuthorizeEndpoint(AuthorizeService authorizeService) {
        this.authorizeService = authorizeService;
    }

    private final AuthorizeService authorizeService;
    private final String SIGN_IN_NAMESPACE_URI = "http://localhost/SignInEndpoint";
    private final String PRE_SIGN_IN_NAMESPACE_URI = "http://localhost/PreSignInEndpoint";
    private final String SIGN_UP_NAMESPACE_URI = "http://localhost/SignUpEndpoint";
    private final String PROVIDER_NAMESPACE_URI = "http://localhost/ProviderEndpoint";


    @PayloadRoot(namespace = SIGN_IN_NAMESPACE_URI, localPart = "SignInRequest")
    @ResponsePayload
    private JAXBElement<AuthorizeResponseDto> signIn(@RequestPayload JAXBElement<SignInRequestDto> request){
        log.info("Received request for method: signIn");
        return new JAXBElement<>(new QName(SIGN_IN_NAMESPACE_URI, "SignInResponse"), AuthorizeResponseDto.class, authorizeService.signIn(request.getValue()));
    }
    @PayloadRoot(namespace = SIGN_UP_NAMESPACE_URI, localPart = "SignUpRequest")
    @ResponsePayload
    private JAXBElement<AuthorizeResponseDto> signUp(@RequestPayload JAXBElement<SignUpRequestDto> request){
        log.info("Received request for method: signUp");
        return new JAXBElement<>(new QName(SIGN_IN_NAMESPACE_URI, "SignUpResponse"), AuthorizeResponseDto.class, authorizeService.signUp(request.getValue()));
    }
    @PayloadRoot(namespace = PROVIDER_NAMESPACE_URI, localPart = "ProviderRequest")
    @ResponsePayload
    private JAXBElement<ProviderResponseDto> getInfoByJwt(@RequestPayload JAXBElement<ProviderRequestDto> request) {
        log.info("Received request for method: getInfoByJwt");
        return new JAXBElement<>(new QName(PROVIDER_NAMESPACE_URI, "ProviderResponse"), ProviderResponseDto.class, authorizeService.getInfoByJwt(request.getValue()));
    }
    @PayloadRoot(namespace = PRE_SIGN_IN_NAMESPACE_URI, localPart = "PreSignInRequest")
    @ResponsePayload
    private JAXBElement<PreSignInResponseDto> preSignIn(@RequestPayload JAXBElement<SignInRequestDto> request){
        return new JAXBElement<>(new QName(PRE_SIGN_IN_NAMESPACE_URI, "PreSignInResponse"), PreSignInResponseDto.class, authorizeService.preSignIn(request.getValue()));
    }


}
