package com.oneglobal.chalenge.entity.dto;

import com.oneglobal.chalenge.entity.enumerator.DeviceState;

public record DeviceRequestDTO(
        String name,
        String brand,
        DeviceState state
) {}
