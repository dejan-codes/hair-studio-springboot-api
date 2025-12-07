package com.hairstudio.api.dto.services;

import java.util.List;

public record PagedServicesDTO(long totalCount, List<ServiceDTO> services) {}