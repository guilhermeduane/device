package com.oneglobal.chalenge.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oneglobal.chalenge.entity.Device;
import com.oneglobal.chalenge.entity.dto.DeviceRequestDTO;
import com.oneglobal.chalenge.entity.dto.DeviceResponseDTO;
import com.oneglobal.chalenge.entity.enumerator.DeviceState;
import com.oneglobal.chalenge.mapper.DeviceMapper;
import com.oneglobal.chalenge.service.DeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class DeviceControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DeviceService service;

    @Mock
    private DeviceMapper mapper;

    @InjectMocks
    private DeviceController controller;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void create_ShouldReturn201Created() throws Exception {
        DeviceRequestDTO requestDto = new DeviceRequestDTO("Pixel 8", "Google", DeviceState.AVAILABLE);
        Device savedDevice = new Device();
        DeviceResponseDTO responseDto = new DeviceResponseDTO(
                1L, "Pixel 8", "Google", DeviceState.AVAILABLE, LocalDateTime.now()
        );

        when(service.create(any())).thenReturn(savedDevice);
        when(mapper.toResponseDTO(savedDevice)).thenReturn(responseDto);

        mockMvc.perform(post("/devices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Pixel 8"));
    }

    @Test
    void getById_WhenFound_ShouldReturn200OK() throws Exception {
        Device device = new Device();
        DeviceResponseDTO responseDto = new DeviceResponseDTO(
                1L, "Pixel 8", "Google", DeviceState.AVAILABLE, LocalDateTime.now()
        );

        when(service.findById(1L)).thenReturn(Optional.of(device));
        when(mapper.toResponseDTO(device)).thenReturn(responseDto);

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void getById_WhenNotFound_ShouldReturn404() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/devices/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturn200OK() throws Exception {
        DeviceResponseDTO responseDto = new DeviceResponseDTO(
                1L, "Pixel 8", "Google", DeviceState.AVAILABLE, LocalDateTime.now()
        );

        when(service.findAll()).thenReturn(List.of(new Device()));
        when(mapper.toResponseDTOList(any())).thenReturn(List.of(responseDto));

        mockMvc.perform(get("/devices"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void delete_WhenInUse_ShouldReturn409Conflict() throws Exception {
        doThrow(new IllegalStateException("Cannot delete a device that is IN_USE"))
                .when(service).delete(1L);

        mockMvc.perform(delete("/devices/1"))
                .andExpect(status().isConflict());
    }

    @Test
    void delete_WhenSuccessful_ShouldReturn204NoContent() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/devices/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_WhenInUse_ShouldReturn409Conflict() throws Exception {
        DeviceRequestDTO requestDto =
                new DeviceRequestDTO("New Name", "Google", DeviceState.IN_USE);

        when(service.update(any(), any()))
                .thenThrow(new IllegalStateException("Cannot update name or brand when device is IN_USE"));

        mockMvc.perform(put("/devices/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isConflict());
    }
}
