package ru.panic.template.service;

import ru.panic.template.dto.PreWithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalRequestDto;
import ru.panic.template.dto.WithdrawalResponseDto;

public interface WithdrawalService {
    WithdrawalResponseDto handleWithdrawal(WithdrawalRequestDto request);
    WithdrawalResponseDto handleSuccessWithdrawal(PreWithdrawalRequestDto request);
}
