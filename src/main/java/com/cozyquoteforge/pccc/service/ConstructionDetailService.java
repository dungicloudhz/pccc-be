package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ConstructionDetailDto;
import com.cozyquoteforge.pccc.entity.*;
import com.cozyquoteforge.pccc.repository.ConstructionRepository;
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

    public ConstructionDetailDto getConstructionDetails(UUID constructionId) {
        Construction construction = constructionRepository.findByIdWithDetails(constructionId)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + constructionId));

        return toDetailDto(construction);
    }

    public ConstructionDetailDto saveConstructionDetails(UUID constructionId, ConstructionDetailDto dto) {
        Construction construction = constructionRepository.findById(constructionId)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + constructionId));

        // Update basic information
        construction.setName(dto.getProjectName());
        construction.setMaterialPercent(dto.getMaterialPercent());
        construction.setLaborPercent(dto.getLaborPercent());

        // Clear and rebuild workshops
        construction.getWorkshops().clear();
        if (dto.getWorkshops() != null) {
            for (ConstructionDetailDto.WorkshopDto workshopDto : dto.getWorkshops()) {
                ConstructionWorkshop workshop = ConstructionWorkshop.builder()
                        .id(workshopDto.getId() != null ? workshopDto.getId() : UUID.randomUUID())
                        .name(workshopDto.getName())
                        .construction(construction)
                        .build();
                construction.getWorkshops().add(workshop);
            }
        }

        // Clear and rebuild sections with rows
        construction.getSections().clear();
        if (dto.getSections() != null) {
            for (ConstructionDetailDto.SectionDto sectionDto : dto.getSections()) {
                ConstructionSection section = ConstructionSection.builder()
                        .id(sectionDto.getId() != null ? sectionDto.getId() : UUID.randomUUID())
                        .name(sectionDto.getName())
                        .construction(construction)
                        .build();

                if (sectionDto.getRows() != null) {
                    for (ConstructionDetailDto.RowDto rowDto : sectionDto.getRows()) {
                        ConstructionRow row = ConstructionRow.builder()
                                .id(rowDto.getId() != null ? rowDto.getId() : UUID.randomUUID())
                                .productId(rowDto.getProductId())
                                .code(rowDto.getCode())
                                .note(rowDto.getNote())
                                .totalCable(rowDto.getTotalCable())
                                .lossPercent(rowDto.getLossPercent())
                                .materialPrice(rowDto.getMaterialPrice())
                                .laborPrice(rowDto.getLaborPrice())
                                .section(section)
                                .build();

                        // Add workshop values
                        if (rowDto.getWorkshopValues() != null) {
                            for (Map.Entry<UUID, java.math.BigDecimal> entry : rowDto.getWorkshopValues().entrySet()) {
                                ConstructionRowWorkshopValue.ConstructionRowWorkshopValueId id =
                                        new ConstructionRowWorkshopValue.ConstructionRowWorkshopValueId(
                                                row.getId(), entry.getKey());

                                ConstructionWorkshop workshop = construction.getWorkshops().stream()
                                        .filter(w -> w.getId().equals(entry.getKey()))
                                        .findFirst()
                                        .orElse(null);

                                if (workshop != null) {
                                    ConstructionRowWorkshopValue value = ConstructionRowWorkshopValue.builder()
                                            .id(id)
                                            .row(row)
                                            .workshop(workshop)
                                            .value(entry.getValue())
                                            .build();
                                    row.getWorkshopValues().add(value);
                                }
                            }
                        }

                        section.getRows().add(row);
                    }
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
                        .build())
                .collect(Collectors.toList());

        List<ConstructionDetailDto.SectionDto> sectionDtos = construction.getSections().stream()
                .map(s -> ConstructionDetailDto.SectionDto.builder()
                        .id(s.getId())
                        .name(s.getName())
                        .rows(s.getRows().stream()
                                .map(row -> {
                                    Map<UUID, java.math.BigDecimal> workshopValues = new HashMap<>();
                                    for (ConstructionRowWorkshopValue value : row.getWorkshopValues()) {
                                        workshopValues.put(value.getWorkshop().getId(), value.getValue());
                                    }

                                    return ConstructionDetailDto.RowDto.builder()
                                            .id(row.getId())
                                            .productId(row.getProductId())
                                            .code(row.getCode())
                                            .note(row.getNote())
                                            .totalCable(row.getTotalCable())
                                            .lossPercent(row.getLossPercent())
                                            .materialPrice(row.getMaterialPrice())
                                            .laborPrice(row.getLaborPrice())
                                            .workshopValues(workshopValues)
                                            .build();
                                })
                                .collect(Collectors.toList()))
                        .build())
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
