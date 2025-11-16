package com.oneglobal.chalenge.service;

import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.dto.DeviceRequestDTO;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import com.oneglobal.chalenge.mapper.DeviceMapper;
import com.oneglobal.chalenge.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository repository;
    private final DeviceMapper mapper;

    public DeviceService(DeviceRepository repository, DeviceMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public Device create(DeviceRequestDTO dto) {
        Device device = mapper.toEntity(dto);
        return repository.save(device);
    }

    public List<Device> findAll() {
        return repository.findAll();
    }

    public Optional<Device> findById(Long id) {
        return repository.findById(id);
    }

    public List<Device> findByBrand(String brand) {
        return repository.findByBrand(brand);
    }

    public List<Device> findByState(DeviceState state) {
        return repository.findByState(state);
    }

    @Transactional
    public Device update(Long id, DeviceRequestDTO dto) {
        Device deviceExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (deviceExistente.getState() == DeviceState.IN_USE) {
            if (!deviceExistente.getName().equals(dto.name()) ||
                    !deviceExistente.getBrand().equals(dto.brand())) {
                throw new IllegalStateException("Cannot update name or brand when device is IN_USE");
            }
        }

        mapper.updateEntityFromDto(dto, deviceExistente);
        return repository.save(deviceExistente);
    }

    @Transactional
    public Device patch(Long id, DeviceRequestDTO dto) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        boolean isTryingToChangeRestrictedFields =
                (dto.name() != null && !dto.name().equals(device.getName())) ||
                        (dto.brand() != null && !dto.brand().equals(device.getBrand()));

        if (device.getState() == DeviceState.IN_USE && isTryingToChangeRestrictedFields) {
            throw new IllegalStateException("Cannot update name or brand when device is IN_USE");
        }

        mapper.updateEntityFromDto(dto, device);
        return repository.save(device);
    }

    public void delete(Long id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (device.getState() == DeviceState.IN_USE) {
            throw new IllegalStateException("Cannot delete a device that is IN_USE");
        }

        repository.deleteById(id);
    }
}