package ru.panic.template.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.panic.security.JwtUtil;
import ru.panic.template.dto.AuthorizeRequestDto;
import ru.panic.template.dto.AuthorizeResponseDto;
import ru.panic.template.entity.User;
import ru.panic.template.exception.InvalidCredentialsException;
import ru.panic.template.repository.UserRepository;

@Service
@Slf4j
public class AuthorizeService {
    public AuthorizeService(JwtUtil jwtUtil, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthorizeResponseDto signIn(AuthorizeRequestDto request){
        log.info("Received authorize request for username {}", request.getUsername());

        User user1 = userRepository.findByUsername(request.getUsername());
        if (user1 == null){
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }
        if (!bCryptPasswordEncoder.matches(
                request.getPassword(), user1.getPassword())) {
            log.warn("Invalid credentials for username {}", request.getUsername());
            throw new InvalidCredentialsException("Неверный логин или пароль");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        String generatedToken = jwtUtil.generateToken(user);

        AuthorizeResponseDto authorizeResponseDto = new AuthorizeResponseDto();
        authorizeResponseDto.setUsername(request.getUsername());
        authorizeResponseDto.setJwtToken(generatedToken);

        log.info("Authorized user {} with token {}", request.getUsername(), generatedToken);

        return authorizeResponseDto;
    }
    public AuthorizeResponseDto signUp(AuthorizeRequestDto request){
        boolean isExists = userRepository.existsUserByUsername(request.getUsername());
        if (isExists){
            throw new InvalidCredentialsException("Данный пользователь уже существует");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        user.setTimestamp(System.currentTimeMillis());
        user.setIpAddress(request.getIpAddress());
        user.setIsNonLocked(false);
        userRepository.save(user);

        AuthorizeResponseDto authorizeResponseDto = new AuthorizeResponseDto();
        authorizeResponseDto.setUsername(request.getUsername());
        authorizeResponseDto.setJwtToken(jwtUtil.generateToken(user));
        return authorizeResponseDto;
    }
}
