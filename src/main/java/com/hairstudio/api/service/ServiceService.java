package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.services.ServiceCreateDTO;
import com.hairstudio.api.dto.services.ServiceDropdownDTO;
import com.hairstudio.api.dto.services.PagedServicesDTO;
import com.hairstudio.api.dto.services.ServicesByGenderDTO;
import com.hairstudio.api.dto.services.ServiceUpdateDTO;

import java.util.List;

public interface ServiceService {

    ResultWithoutValue createService(ServiceCreateDTO dto, Short tokenUserId);

    ResultWithValue<List<ServiceDropdownDTO>> getServicesForDropdown(Short tokenUserId);

    ResultWithValue<PagedServicesDTO> getAllServices(int page, int rowsPerPage);

    ResultWithValue<ServicesByGenderDTO> getServicesByGender();

    ResultWithoutValue updateService(Short serviceId, ServiceUpdateDTO dto, Short tokenUserId);

    ResultWithoutValue deleteService(Short serviceId, Short tokenUserId);

}
