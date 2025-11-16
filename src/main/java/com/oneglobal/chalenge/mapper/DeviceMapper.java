package com.oneglobal.chalenge.mapper;


import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.dto.DeviceRequestDTO;
import com.oneglobal.chalenge.entity.dto.DeviceResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface DeviceMapper {

    Device toEntity(DeviceRequestDTO dto);

    DeviceResponseDTO toResponseDTO(Device entity);

    List<DeviceResponseDTO> toResponseDTOList(List<Device> entities);

    void updateEntityFromDto(DeviceRequestDTO dto, @MappingTarget Device entity);
}
