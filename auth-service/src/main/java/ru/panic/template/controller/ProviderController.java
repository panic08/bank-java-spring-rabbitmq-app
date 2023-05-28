package ru.panic.template.controller;

import org.springframework.web.bind.annotation.*;
import ru.panic.template.dto.ProviderRequestDto;
import ru.panic.template.dto.ProviderResponseDto;
import ru.panic.template.service.impl.AuthorizeServiceImpl;

@RestController
@RequestMapping("/api/v2")
@CrossOrigin
public class ProviderController {
    public ProviderController(AuthorizeServiceImpl authorizeServiceImpl) {
        this.authorizeServiceImpl = authorizeServiceImpl;
    }
    private final AuthorizeServiceImpl authorizeServiceImpl;
    /*
    Dear caretakers, unfortunately I could not configure WebServiceTemplate, so...
     */
    @GetMapping("/getInfoByJwt")
    @ResponseBody
    private ProviderResponseDto getInfoByJwt(@RequestBody ProviderRequestDto request){
        return authorizeServiceImpl.getInfoByJwt(request);
    }
}
