package ru.panic.template.service;

import ru.panic.template.dto.WithdrawalRequestDto;

public interface WithdrawalService {
    void handleWithdrawal(WithdrawalRequestDto request);
}
