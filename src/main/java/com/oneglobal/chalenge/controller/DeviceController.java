package com.oneglobal.chalenge.controller;

import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.dto.DeviceRequestDTO;
import com.oneglobal.chalenge.entity.dto.DeviceResponseDTO;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import com.oneglobal.chalenge.mapper.DeviceMapper;
import com.oneglobal.chalenge.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@Tag(name = "Devices", description = "Endpoints for managing device inventory")
public class DeviceController {

    private final DeviceService service;
    private final DeviceMapper mapper;

    public DeviceController(DeviceService service, DeviceMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @Operation(summary = "Create a new device")
    @ApiResponse(responseCode = "201", description = "Device created successfully")
    @PostMapping
    public ResponseEntity<DeviceResponseDTO> create(@RequestBody DeviceRequestDTO dto) {
        Device newDevice = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDTO(newDevice));
    }

    @Operation(summary = "List all devices")
    @ApiResponse(responseCode = "200", description = "List of all devices")
    @GetMapping
    public ResponseEntity<List<DeviceResponseDTO>> getAll() {
        List<Device> devices = service.findAll();
        return ResponseEntity.ok(mapper.toResponseDTOList(devices));
    }

    @Operation(summary = "Get a single device by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found"),
            @ApiResponse(responseCode = "404", description = "Device not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(mapper::toResponseDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find devices by brand")
    @ApiResponse(responseCode = "200", description = "List of devices matching the brand")
    @GetMapping("/search/brand")
    public ResponseEntity<List<DeviceResponseDTO>> getByBrand(@RequestParam String brand) {
        List<Device> devices = service.findByBrand(brand);
        return ResponseEntity.ok(mapper.toResponseDTOList(devices));
    }

    @Operation(summary = "Find devices by state")
    @ApiResponse(responseCode = "200", description = "List of devices matching the state")
    @GetMapping("/search/state")
    public ResponseEntity<List<DeviceResponseDTO>> getByState(@RequestParam DeviceState state) {
        List<Device> devices = service.findByState(state);
        return ResponseEntity.ok(mapper.toResponseDTOList(devices));
    }

    @Operation(summary = "Fully update an existing device",
            description = "Replaces all fields. Note: Name and Brand cannot be updated if the device is IN_USE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Update violates business rules (e.g., updating Name/Brand while IN_USE)")
    })
    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> update(@PathVariable Long id, @RequestBody DeviceRequestDTO dto) {
        try {
            Device updatedDevice = service.update(id, dto);
            return ResponseEntity.ok(mapper.toResponseDTO(updatedDevice));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Partially update an existing device",
            description = "Updates only provided fields. Note: Name and Brand cannot be updated if the device is IN_USE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Update violates business rules (e.g., updating Name/Brand while IN_USE)")
    })
    @PatchMapping("/{id}")
    public ResponseEntity<DeviceResponseDTO> patch(@PathVariable Long id, @RequestBody DeviceRequestDTO dto) {
        try {
            Device patchedDevice = service.patch(id, dto);
            return ResponseEntity.ok(mapper.toResponseDTO(patchedDevice));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Delete a device by its ID",
            description = "Deletes a device. Note: Devices in the IN_USE state cannot be deleted.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Device deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Device not found"),
            @ApiResponse(responseCode = "409", description = "Conflict: Cannot delete a device that is IN_USE")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}