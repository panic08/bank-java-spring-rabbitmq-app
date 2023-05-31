package ru.panic.template.service;

import ru.panic.template.dto.ReplenishmentRequestDto;

public interface ReplenishmentService {
    void handleReplenishment(ReplenishmentRequestDto request);
}
