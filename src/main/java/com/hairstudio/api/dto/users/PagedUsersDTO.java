package com.hairstudio.api.dto.users;

import java.util.List;

public record PagedUsersDTO(long totalCount, List<EmployeeAdminDTO> employees) {}
