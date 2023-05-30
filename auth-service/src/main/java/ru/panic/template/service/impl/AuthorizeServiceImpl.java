package ru.panic.template.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.panic.security.JwtUtil;
import ru.panic.template.api.MessenteApi;
import ru.panic.template.dto.*;
import ru.panic.template.entity.User;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.exception.InvalidJwtException;
import ru.panic.template.exception.TimeNotElapsedException;
import ru.panic.template.repository.AuthorizeSmsCodeVerifierHashRepository;
import ru.panic.template.repository.UserRepository;
import ru.panic.template.service.AuthorizeService;
import ru.panic.template.service.hash.AuthorizeSmsCodeVerifierHash;
import ru.panic.util.CodeGeneratorUtil;
import ru.panic.util.PhoneNumberValidatorUtil;

@Service
@Slf4j
public class AuthorizeServiceImpl implements AuthorizeService {
    public AuthorizeServiceImpl(JwtUtil jwtUtil, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, MessenteApi messenteApi, AuthorizeSmsCodeVerifierHashRepository authorizeSmsCodeVerifierHashRepository, CodeGeneratorUtil codeGeneratorUtil, PhoneNumberValidatorUtil phoneNumberValidatorUtil) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.messenteApi = messenteApi;
        this.authorizeSmsCodeVerifierHashRepository = authorizeSmsCodeVerifierHashRepository;
        this.codeGeneratorUtil = codeGeneratorUtil;
        this.phoneNumberValidatorUtil = phoneNumberValidatorUtil;
    }

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final MessenteApi messenteApi;
    private final AuthorizeSmsCodeVerifierHashRepository authorizeSmsCodeVerifierHashRepository;
    private final CodeGeneratorUtil codeGeneratorUtil;
    private final PhoneNumberValidatorUtil phoneNumberValidatorUtil;
    @Override
    public AuthorizeResponseDto signIn(SignInRequestDto request){
        log.info("Received signIn request for username {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null){
            log.warn("Invalid credentials for username {}", request.getUsername());
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        if (!bCryptPasswordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            log.warn("Invalid credentials for username {}", request.getUsername());
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        if (!authorizeSmsCodeVerifierHashRepository.findById(request.getUsername())
                .orElseThrow().getCode().equals(request.getCode())){
            log.warn("Invalid credentials for username {}", request.getUsername());
            throw new InvalidCredentialsException("Неверный смс код");
        }

        String generatedToken = jwtUtil.generateToken(user);

        AuthorizeResponseDto authorizeResponseDto = new AuthorizeResponseDto();
        authorizeResponseDto.setStatus(200);
        authorizeResponseDto.setUsername(request.getUsername());
        authorizeResponseDto.setJwtToken(generatedToken);

        log.info("Authorized user {} with token {}", request.getUsername(), generatedToken);

        return authorizeResponseDto;
    }
    @Override
    public AuthorizeResponseDto signUp(SignUpRequestDto request){

        boolean isExists = userRepository.existsUserByUsername(request.getUsername());
        log.info("Received signUp request for username {}", request.getUsername());
        if (isExists){
            throw new InvalidCredentialsException("Данный пользователь уже существует");
        }
        if (!phoneNumberValidatorUtil.validate(request.getUsername())){
            throw new InvalidCredentialsException("Неверный номер телефона");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setRub_balance(0D);
        user.setEur_balance(0D);
        user.setUsd_balance(0D);
        user.setSecure3D(true);
        user.setTimestamp(System.currentTimeMillis());
        user.setIpAddress(request.getIpAddress());
        user.setIsNonLocked(false);
        userRepository.save(user);

        AuthorizeResponseDto authorizeResponseDto = new AuthorizeResponseDto();
        authorizeResponseDto.setStatus(200);
        authorizeResponseDto.setUsername(request.getUsername());
        authorizeResponseDto.setJwtToken(jwtUtil.generateToken(user));
        return authorizeResponseDto;
    }
    @Override
    public ProviderResponseDto getInfoByJwt(ProviderRequestDto request){
        if (jwtUtil.isJwtValid(request.getJwtToken()) && !jwtUtil.isTokenExpired(request.getJwtToken())){
            User user = userRepository.findByUsername(jwtUtil.extractUsername(request.getJwtToken()));

            ProviderResponseDto providerResponseDto = new ProviderResponseDto();
            providerResponseDto.setStatus(200);
            providerResponseDto.setUsername(user.getUsername());
            providerResponseDto.setRub_balance(user.getRub_balance());
            providerResponseDto.setEur_balance(user.getEur_balance());
            providerResponseDto.setUsd_balance(user.getUsd_balance());
            providerResponseDto.setSecure3D(user.getSecure3D());
            providerResponseDto.setJwtToken(request.getJwtToken());

            return providerResponseDto;
        }else{
            throw new InvalidJwtException("Некорректный JWT токен");
        }
    }
    @Override
    public PreSignInResponseDto preSignIn(SignInRequestDto request){
        log.info("Received preSignIn request for username {}", request.getUsername());

        User user = userRepository.findByUsername(request.getUsername());
        if (user == null){
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        if (!bCryptPasswordEncoder.matches(
                request.getPassword(), user.getPassword())) {
            log.warn("Invalid credentials for username {}", request.getUsername());
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        AuthorizeSmsCodeVerifierHash authorizeSmsCodeVerifierHash = new AuthorizeSmsCodeVerifierHash();
        authorizeSmsCodeVerifierHash.setUsername(request.getUsername());
        authorizeSmsCodeVerifierHash.setCode(codeGeneratorUtil.generateRandomNumber());
        authorizeSmsCodeVerifierHash.setLevel(0);
        authorizeSmsCodeVerifierHash.setTimestamp(System.currentTimeMillis());

        AuthorizeSmsCodeVerifierHash authorizeSmsCodeVerifierHash1 = authorizeSmsCodeVerifierHashRepository.findById(request.getUsername()).orElse(null);

        if (authorizeSmsCodeVerifierHash1 != null){
            log.warn("AuthorizeSmsCode object was founded {}", authorizeSmsCodeVerifierHash1.getUsername());
            authorizeSmsCodeVerifierHash.setLevel(authorizeSmsCodeVerifierHash1.getLevel()+1);
            authorizeSmsCodeVerifierHashRepository.deleteById(request.getUsername());
        }

        long timeLeft = System.currentTimeMillis() - authorizeSmsCodeVerifierHash.getTimestamp();
        switch (authorizeSmsCodeVerifierHash.getLevel()){
            case 1 -> {
                if (timeLeft <= 90000){
                    throw new TimeNotElapsedException(
                            "Подождите, до повторной отправки уведомления осталось: " +
                            timeLeft);
                }
            }
            case 2 -> {
                if (timeLeft <= 180000){
                    throw new TimeNotElapsedException(
                            "Подождите, до повторной отправки уведомления осталось: " +
                                    timeLeft);
                }
            }
            case 3 -> {
                if (timeLeft <= 360000){
                    throw new TimeNotElapsedException(
                            "Подождите, до повторной отправки уведомления осталось: " +
                                    timeLeft);
                }
            }
        }

        authorizeSmsCodeVerifierHashRepository.save(authorizeSmsCodeVerifierHash);

        messenteApi.sendSms(request.getUsername(), "Ваш смс код для подтверждения авторизации: " + authorizeSmsCodeVerifierHash.getCode());
        PreSignInResponseDto preSignInResponseDto = new PreSignInResponseDto();
        preSignInResponseDto.setStatus(200);
        preSignInResponseDto.setUsername(request.getUsername());
        return preSignInResponseDto;
    }
}
