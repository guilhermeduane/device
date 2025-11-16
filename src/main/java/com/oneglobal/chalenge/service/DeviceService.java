package com.oneglobal.chalenge.service;

import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import com.oneglobal.chalenge.repository.DeviceRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {

    private final DeviceRepository repository;

    public DeviceService(DeviceRepository repository) {
        this.repository = repository;
    }

    public Device create(Device device) {
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
    public Device update(Long id, Device deviceAtualizado) {
        Device deviceExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        if (deviceExistente.getState() == DeviceState.IN_USE) {
            if (!deviceExistente.getName().equals(deviceAtualizado.getName()) ||
                    !deviceExistente.getBrand().equals(deviceAtualizado.getBrand())) {
                throw new IllegalStateException("Cannot update name or brand when device is IN_USE");
            }
        }

        deviceExistente.setName(deviceAtualizado.getName());
        deviceExistente.setBrand(deviceAtualizado.getBrand());
        deviceExistente.setState(deviceAtualizado.getState());
        return repository.save(deviceExistente);
    }

    @Transactional
    public Device patch(Long id, Device updates) {
        Device deviceExistente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Device not found"));

        boolean isTryingToChangeRestrictedFields =
                (updates.getName() != null && !updates.getName().equals(deviceExistente.getName())) ||
                        (updates.getBrand() != null && !updates.getBrand().equals(deviceExistente.getBrand()));

        if (deviceExistente.getState() == DeviceState.IN_USE && isTryingToChangeRestrictedFields) {
            throw new IllegalStateException("Cannot update name or brand when device is IN_USE");
        }

        if (updates.getName() != null) deviceExistente.setName(updates.getName());
        if (updates.getBrand() != null) deviceExistente.setBrand(updates.getBrand());
        if (updates.getState() != null) deviceExistente.setState(updates.getState());

        return repository.save(deviceExistente);
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
