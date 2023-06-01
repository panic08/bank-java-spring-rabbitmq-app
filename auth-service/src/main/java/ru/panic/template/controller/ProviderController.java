package ru.panic.template.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.ProviderRequestDto;
import ru.panic.template.dto.ProviderResponseDto;
import ru.panic.template.service.impl.AuthorizeServiceImpl;

@RestController
@RequestMapping("/api/v2")
@Slf4j
public class ProviderController {
    public ProviderController(AuthorizeServiceImpl authorizeServiceImpl) {
        this.authorizeServiceImpl = authorizeServiceImpl;
    }
    private final AuthorizeServiceImpl authorizeServiceImpl;
    /*
    Dear caretakers, unfortunately I could not configure WebServiceTemplate, so...
     */
    @PostMapping("/getInfoByJwt")
    private ProviderResponseDto getInfoByJwt(@RequestBody ProviderRequestDto request){
        log.info("Get request on endpoint: getInfoByJwt");
        return authorizeServiceImpl.getInfoByJwt(request);
    }
}
