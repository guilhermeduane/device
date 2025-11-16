package com.oneglobal.chalenge.entity.dto;

import com.oneglobal.chalenge.entity.enumerator.DeviceState;

import java.time.LocalDateTime;

public record DeviceResponseDTO(
        Long id,
        String name,
        String brand,
        DeviceState state,
        LocalDateTime creationTime
) {}
