package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.services.ServiceCreateDTO;
import com.hairstudio.api.dto.services.ServiceDropdownDTO;
import com.hairstudio.api.dto.services.PagedServicesDTO;
import com.hairstudio.api.dto.services.ServiceDTO;
import com.hairstudio.api.dto.services.ServicesByGenderDTO;
import com.hairstudio.api.dto.services.ServiceSummaryDTO;
import com.hairstudio.api.dto.services.ServiceUpdateDTO;
import com.hairstudio.api.errors.ServiceErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.model.enums.RoleEnum;
import com.hairstudio.api.repository.MessageRepository;
import com.hairstudio.api.repository.ServiceRepository;
import com.hairstudio.api.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Value("${app.status.disable}")
    private String disableStatus;

    @Override
    @Transactional
    @Auditable(action = "CREATE_SERVICE")
    public ResultWithoutValue createService(ServiceCreateDTO dto, Short tokenUserId) {
        var userOpt = userRepository.findById(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        com.hairstudio.api.model.entity.Service service = new com.hairstudio.api.model.entity.Service();
        service.setDescription(dto.getDescription());
        service.setPrice(dto.getPrice());
        service.setDiscount(dto.getDiscount());
        service.setDurationMinutes(dto.getDurationMinutes());
        service.setName(dto.getName());
        service.setGenderId(dto.getGenderId());
        try {
            service.setImage(dto.getImage().getBytes());
        } catch (IOException e) {
            log.error("Failed to read service image for service name={}", service.getName(), e);
            return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
        }
        service.setSequenceNumber(dto.getSequenceNumber());
        service.setIsActive(true);
        service.setCreatedAt(Instant.now());
        serviceRepository.save(service);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " created a service " + dto.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<ServiceDropdownDTO>> getServicesForDropdown(Short tokenUserId) {
        var userOpt = userRepository.findById(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        boolean isAdminOrEmployee = user.getRoles().stream()
                .anyMatch(r -> r.getRoleId() == RoleEnum.ADMINISTRATOR.getId() ||
                        r.getRoleId() == RoleEnum.EMPLOYEE.getId());

        var activeServicesStream = serviceRepository.findByIsActiveTrueOrderBySequenceNumberAsc().stream();
        if (!isAdminOrEmployee) {
            activeServicesStream = activeServicesStream
                    .filter(service -> !disableStatus.equals(service.getName()));
        }

        List<ServiceDropdownDTO> services = activeServicesStream
                .map(s -> new ServiceDropdownDTO(s.getServiceId(), s.getName(), s.getDurationMinutes()))
                .toList();

        return ResultWithValue.success(services);
    }

    @Override
    public ResultWithValue<PagedServicesDTO> getAllServices(int page, int rowsPerPage) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var pageable = PageRequest.of(page - 1, rowsPerPage);
        var servicesPage = serviceRepository.findByIsActiveTrue(pageable);

        var list = servicesPage.stream()
                .filter(s -> !disableStatus.equalsIgnoreCase(s.getName()))
                .map(s -> new ServiceDTO(
                        s.getServiceId(),
                        s.getName(),
                        s.getDescription(),
                        s.getPrice(),
                        s.getDiscount(),
                        s.getDurationMinutes(),
                        s.getGender().getGenderId(),
                        s.getImage(),
                        s.getSequenceNumber()
                ))
                .toList();

        return ResultWithValue.success(new PagedServicesDTO(serviceRepository.countByIsActiveTrue(), list));
    }

    @Override
    public ResultWithValue<ServicesByGenderDTO> getServicesByGender() {
        List<ServiceSummaryDTO> all = serviceRepository.findByIsActiveTrue().stream()
                .filter(s -> !disableStatus.equalsIgnoreCase(s.getName()))
                .sorted(Comparator.comparing(s -> s.getGender().getName()))
                .map(s -> new ServiceSummaryDTO(
                        s.getServiceId(),
                        s.getName(),
                        s.getDescription(),
                        s.getPrice(),
                        s.getGender().getName(),
                        s.getImage()
                ))
                .toList();

        var grouped = all.stream()
                .collect(Collectors.groupingBy(ServiceSummaryDTO::gender));

        List<ServiceSummaryDTO> maleServices = grouped.getOrDefault("Male", List.of());
        List<ServiceSummaryDTO> femaleServices = grouped.getOrDefault("Female", List.of());

        ServicesByGenderDTO servicesByGenderDTO = new ServicesByGenderDTO(maleServices, femaleServices);

        return ResultWithValue.success(servicesByGenderDTO);
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_SERVICE")
    public ResultWithoutValue updateService(Short serviceId, ServiceUpdateDTO dto, Short tokenUserId) {
        var userOpt = userRepository.findById(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var existingService = serviceRepository.findById(serviceId).orElse(null);
        if (existingService == null || !existingService.getIsActive() ||
                disableStatus.equalsIgnoreCase(existingService.getName()))
            return ResultWithoutValue.failure(ServiceErrors.SERVICE_NOT_FOUND);

        existingService.setDescription(dto.getDescription());
        existingService.setPrice(dto.getPrice());
        existingService.setDiscount(dto.getDiscount());
        existingService.setDurationMinutes(dto.getDurationMinutes());
        existingService.setName(dto.getName());
        existingService.setGenderId(dto.getGenderId());
        existingService.setSequenceNumber(dto.getSequenceNumber());
        if (dto.getImage() != null)
            try {
                existingService.setImage(dto.getImage().getBytes());
            } catch (IOException e) {
                log.error("Failed to read service image for service name={}", existingService.getName(), e);
                return ResultWithoutValue.failure(ValidationErrors.IMAGE_READ_ERROR);
            }
        serviceRepository.save(existingService);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " updated a service " + dto.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_SERVICE")
    public ResultWithoutValue deleteService(Short serviceId, Short tokenUserId) {
        var userOpt = userRepository.findById(tokenUserId);
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var service = serviceRepository.findById(serviceId).orElse(null);
        if (service == null || !service.getIsActive() ||
                disableStatus.equalsIgnoreCase(service.getName()))
            return ResultWithoutValue.failure(ServiceErrors.SERVICE_NOT_FOUND);

        service.setIsActive(false);
        serviceRepository.save(service);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content("User " + user.getFirstName() + " " + user.getLastName() +
                        " deleted a service " + service.getName() + ".").build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }
}
