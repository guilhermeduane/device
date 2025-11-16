package com.oneglobal.chalenge.service;

import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.dto.DeviceRequestDTO;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import com.oneglobal.chalenge.mapper.DeviceMapper;
import com.oneglobal.chalenge.repository.DeviceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    @Mock
    private DeviceRepository repository;

    @Mock
    private DeviceMapper mapper;

    @InjectMocks
    private DeviceService service;

    @Test
    @DisplayName("Should create a new device")
    void create_ShouldSaveDevice() {
        DeviceRequestDTO dto = new DeviceRequestDTO("iPhone 15", "Apple", DeviceState.AVAILABLE);
        Device device = new Device();

        when(mapper.toEntity(dto)).thenReturn(device);
        when(repository.save(device)).thenReturn(device);

        service.create(dto);

        verify(repository, times(1)).save(device);
        verify(mapper, times(1)).toEntity(dto);
    }

    @Test
    @DisplayName("Should NOT delete device when state is IN_USE")
    void delete_WhenInUse_ShouldThrowException() {
        Long deviceId = 1L;
        Device deviceInUse = new Device();
        deviceInUse.setState(DeviceState.IN_USE);

        when(repository.findById(deviceId)).thenReturn(Optional.of(deviceInUse));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.delete(deviceId);
        });

        assertEquals("Cannot delete a device that is IN_USE", exception.getMessage());
        verify(repository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Should delete device when state is AVAILABLE")
    void delete_WhenAvailable_ShouldSucceed() {
        Long deviceId = 1L;
        Device deviceAvailable = new Device();
        deviceAvailable.setState(DeviceState.AVAILABLE);

        when(repository.findById(deviceId)).thenReturn(Optional.of(deviceAvailable));

        service.delete(deviceId);

        verify(repository, times(1)).deleteById(deviceId);
    }

    @Test
    @DisplayName("Should NOT update name or brand when state is IN_USE (PUT)")
    void update_WhenInUseAndNameChanged_ShouldThrowException() {
        Long deviceId = 1L;
        DeviceRequestDTO dto = new DeviceRequestDTO("New Name", "New Brand", DeviceState.IN_USE);

        Device deviceInDb = new Device();
        deviceInDb.setName("Old Name");
        deviceInDb.setBrand("Old Brand");
        deviceInDb.setState(DeviceState.IN_USE);

        when(repository.findById(deviceId)).thenReturn(Optional.of(deviceInDb));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.update(deviceId, dto);
        });

        assertEquals("Cannot update name or brand when device is IN_USE", exception.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Should update state when state is IN_USE (PUT)")
    void update_WhenInUseAndOnlyStateChanged_ShouldSucceed() {
        Long deviceId = 1L;
        DeviceRequestDTO dto = new DeviceRequestDTO("Old Name", "Old Brand", DeviceState.INACTIVE);

        Device deviceInDb = new Device();
        deviceInDb.setName("Old Name");
        deviceInDb.setBrand("Old Brand");
        deviceInDb.setState(DeviceState.IN_USE);

        when(repository.findById(deviceId)).thenReturn(Optional.of(deviceInDb));

        service.update(deviceId, dto);

        verify(mapper).updateEntityFromDto(dto, deviceInDb);
        verify(repository).save(deviceInDb);
    }

    @Test
    @DisplayName("Should NOT update name when state is IN_USE (PATCH)")
    void patch_WhenInUseAndNameChanged_ShouldThrowException() {
        Long deviceId = 1L;
        DeviceRequestDTO dto = new DeviceRequestDTO("New Name", null, null);

        Device deviceInDb = new Device();
        deviceInDb.setName("Old Name");
        deviceInDb.setBrand("Old Brand");
        deviceInDb.setState(DeviceState.IN_USE);

        when(repository.findById(deviceId)).thenReturn(Optional.of(deviceInDb));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            service.patch(deviceId, dto);
        });

        assertEquals("Cannot update name or brand when device is IN_USE", exception.getMessage());
        verify(repository, never()).save(any());
    }
}