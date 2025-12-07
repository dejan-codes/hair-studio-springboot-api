package com.hairstudio.api.dto.services;

import java.util.List;

public record ServicesByGenderDTO(List<ServiceSummaryDTO> maleServices, List<ServiceSummaryDTO> femaleServices) {}