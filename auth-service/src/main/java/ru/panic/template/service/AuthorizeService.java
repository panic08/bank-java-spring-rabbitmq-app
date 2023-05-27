package ru.panic.template.service;

import ru.panic.template.dto.*;

public interface AuthorizeService {
    AuthorizeResponseDto signIn(SignInRequestDto request);
    AuthorizeResponseDto signUp(SignUpRequestDto request);
    PreSignInResponseDto preSignIn(SignInRequestDto request);
    ProviderResponseDto getInfoByJwt(ProviderRequestDto request);
}
