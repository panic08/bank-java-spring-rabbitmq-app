package ru.panic.template.service;

import ru.panic.template.dto.factory.QiwiRequestDto;

public interface WebHookService {
    void handleQiwiWebhook(QiwiRequestDto request);
}
