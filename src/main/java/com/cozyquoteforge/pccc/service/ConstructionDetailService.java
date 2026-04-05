package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ConstructionDetailDto;
import com.cozyquoteforge.pccc.entity.*;
import com.cozyquoteforge.pccc.repository.ConstructionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConstructionDetailService {
    private final ConstructionRepository constructionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConstructionDetailDto getConstructionDetails(UUID constructionId) {
        Construction construction = constructionRepository.findByIdWithDetails(constructionId)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + constructionId));

        return toDetailDto(construction);
    }

    public ConstructionDetailDto saveConstructionDetails(UUID constructionId, ConstructionDetailDto dto) {
        Construction construction = constructionRepository.findById(constructionId)
                .orElseThrow(() -> new RuntimeException("Constructions not found with id: " + constructionId));

        // Update basic information
        construction.setName(dto.getProjectName());
        construction.setMaterialPercent(dto.getMaterialPercent());
        construction.setLaborPercent(dto.getLaborPercent());

        // Clear all existing details
        construction.getWorkshops().clear();
        construction.getSections().clear();

        // Rebuild workshops from DTO
        if (dto.getWorkshops() != null) {
            for (ConstructionDetailDto.WorkshopDto workshopDto : dto.getWorkshops()) {
                ConstructionWorkshop workshop = ConstructionWorkshop.builder()
                        .id(workshopDto.getId() != null ? workshopDto.getId() : UUID.randomUUID())
                        .orderId(workshopDto.getOrderId())
                        .name(workshopDto.getName())
                        .construction(construction)
                        .build();
                construction.getWorkshops().add(workshop);
            }
        }

        // Rebuild sections and rows from DTO
        if (dto.getSections() != null) {
            for (ConstructionDetailDto.SectionDto sectionDto : dto.getSections()) {
                ConstructionSection section = ConstructionSection.builder()
                        .id(sectionDto.getId() != null ? sectionDto.getId() : UUID.randomUUID())
                        .name(sectionDto.getName())
                        .orderId(sectionDto.getOrderId())
                        .construction(construction)
                        .build();

                // Serialize rows to JSON
                if (sectionDto.getRows() != null && !sectionDto.getRows().isEmpty()) {
                    try {
                        section.setRows(objectMapper.writeValueAsString(sectionDto.getRows()));
                    } catch (Exception e) {
                        section.setRows("[]");
                    }
                } else {
                    section.setRows("[]");
                }

                construction.getSections().add(section);
            }
        }

        Construction saved = constructionRepository.save(construction);
        return toDetailDto(saved);
    }

    public ConstructionDetailDto createConstructionDetails(ConstructionDetailDto dto) {
        Construction construction = new Construction();
        // Update basic information
        construction.setName(dto.getProjectName());
        construction.setMaterialPercent(dto.getMaterialPercent());
        construction.setLaborPercent(dto.getLaborPercent());

        // Rebuild workshops from DTO
        if (dto.getWorkshops() != null) {
            for (ConstructionDetailDto.WorkshopDto workshopDto : dto.getWorkshops()) {
                ConstructionWorkshop workshop = ConstructionWorkshop.builder()
                        .id(workshopDto.getId() != null ? workshopDto.getId() : UUID.randomUUID())
                        .orderId(workshopDto.getOrderId())
                        .name(workshopDto.getName())
                        .construction(construction)
                        .build();
                construction.getWorkshops().add(workshop);
            }
        }

        // Rebuild sections and rows from DTO
        if (dto.getSections() != null) {
            for (ConstructionDetailDto.SectionDto sectionDto : dto.getSections()) {
                ConstructionSection section = ConstructionSection.builder()
                        .id(sectionDto.getId() != null ? sectionDto.getId() : UUID.randomUUID())
                        .name(sectionDto.getName())
                        .orderId(sectionDto.getOrderId())
                        .construction(construction)
                        .build();

                // Serialize rows to JSON
                if (sectionDto.getRows() != null && !sectionDto.getRows().isEmpty()) {
                    try {
                        section.setRows(objectMapper.writeValueAsString(sectionDto.getRows()));
                    } catch (Exception e) {
                        section.setRows("[]");
                    }
                } else {
                    section.setRows("[]");
                }

                construction.getSections().add(section);
            }
        }

        Construction saved = constructionRepository.save(construction);
        return toDetailDto(saved);
    }

    private ConstructionDetailDto toDetailDto(Construction construction) {
        List<ConstructionDetailDto.WorkshopDto> workshopDtos = construction.getWorkshops().stream()
                .map(w -> ConstructionDetailDto.WorkshopDto.builder()
                        .id(w.getId())
                        .name(w.getName())
                        .orderId(w.getOrderId())
                        .build())
                .collect(Collectors.toList());

        List<ConstructionDetailDto.SectionDto> sectionDtos = construction.getSections().stream()
                .map(s -> {
                    List<ConstructionDetailDto.RowDto> rowDtos = new ArrayList<>();
                    
                    // Deserialize rows from JSON
                    if (s.getRows() != null && !s.getRows().isEmpty()) {
                        try {
                            rowDtos = objectMapper.readValue(s.getRows(), 
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, ConstructionDetailDto.RowDto.class));
                        } catch (Exception e) {
                            rowDtos = new ArrayList<>();
                        }
                    }

                    List<UUID> rowIds = rowDtos.stream()
                            .filter(r -> r.getId() != null)
                            .map(r -> UUID.fromString(r.getId()))
                            .collect(Collectors.toList());

                    return ConstructionDetailDto.SectionDto.builder()
                            .id(s.getId())
                            .name(s.getName())
                            .orderId(s.getOrderId())
                            .rows(rowDtos)
                            .idSections(rowIds.stream()
                                    .map(UUID::toString)
                                    .collect(Collectors.joining(",")))
                            .build();
                })
                .collect(Collectors.toList());

        return ConstructionDetailDto.builder()
                .id(construction.getId())
                .projectName(construction.getName())
                .materialPercent(construction.getMaterialPercent())
                .laborPercent(construction.getLaborPercent())
                .workshops(workshopDtos)
                .sections(sectionDtos)
                .build();
    }
}
