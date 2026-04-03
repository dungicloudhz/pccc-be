package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.PartTypeDto;
import com.cozyquoteforge.pccc.entity.PartType;
import com.cozyquoteforge.pccc.repository.PartTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PartTypeService {
    private final PartTypeRepository partTypeRepository;

    public List<PartTypeDto> getAllPartTypes() {
        return partTypeRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public PartTypeDto getPartTypeById(UUID id) {
        return partTypeRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("PartType not found with id: " + id));
    }

    public PartTypeDto createPartType(PartTypeDto dto) {
        PartType partType = PartType.builder()
                .name(dto.getName())
                .build();
        PartType saved = partTypeRepository.save(partType);
        return toDto(saved);
    }

    public PartTypeDto updatePartType(UUID id, PartTypeDto dto) {
        PartType partType = partTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("PartType not found with id: " + id));
        partType.setName(dto.getName());
        PartType updated = partTypeRepository.save(partType);
        return toDto(updated);
    }

    public void deletePartType(UUID id) {
        partTypeRepository.deleteById(id);
    }

    private PartTypeDto toDto(PartType partType) {
        return PartTypeDto.builder()
                .id(partType.getId())
                .name(partType.getName())
                .createdAt(partType.getCreatedAt())
                .updatedAt(partType.getUpdatedAt())
                .build();
    }
}
