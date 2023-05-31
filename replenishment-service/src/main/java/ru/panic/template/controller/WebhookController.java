package ru.panic.template.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.panic.template.dto.factory.QiwiRequestDto;
import ru.panic.template.service.impl.WebHookServiceImpl;

@RestController
@RequestMapping("/api/v1")
public class WebhookController {
    public WebhookController(WebHookServiceImpl webHookService) {
        this.webHookService = webHookService;
    }

    private final WebHookServiceImpl webHookService;
    @PostMapping("/qiwi")
    private HttpStatus getQiwiWebhook(@RequestBody QiwiRequestDto request){
        webHookService.handleQiwiWebhook(request);
        return HttpStatus.OK;
    }
}
