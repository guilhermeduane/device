package com.oneglobal.chalenge.repository;

import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeviceRepository extends JpaRepository<Device, Long> {
    List<Device> findByState(DeviceState state);
    List<Device> findByBrand(String brand);
}
