package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.brands.BrandCreateDTO;
import com.hairstudio.api.dto.brands.BrandUpdateDTO;
import com.hairstudio.api.dto.brands.BrandDTO;
import com.hairstudio.api.dto.brands.PagedBrandsDTO;
import com.hairstudio.api.errors.BrandErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.Brand;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.repository.BrandRepository;
import com.hairstudio.api.repository.MessageRepository;
import com.hairstudio.api.repository.UserRepository;
import com.hairstudio.api.security.CurrentUserContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class BrandServiceImpl implements BrandService {

    private final CurrentUserContext currentUserContext;
    private final BrandRepository brandRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public ResultWithValue<PagedBrandsDTO> getPagedBrands(int page, int rowsPerPage) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var pageable = PageRequest.of(page - 1, rowsPerPage);
        var brandsPage = brandRepository.findByIsActiveTrue(pageable);

        var result = brandsPage.getContent().stream()
                .map(b -> new BrandDTO(b.getBrandId(), b.getName()))
                .collect(Collectors.toList());

        return ResultWithValue.success(new PagedBrandsDTO(brandsPage.getTotalElements(), result));
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_BRAND")
    public ResultWithoutValue createBrand(BrandCreateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        if (brandRepository.existsByName(dto.getName())) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_EXISTS);
        }

        var brand = Brand.builder()
                    .name(dto.getName())
                    .isActive(true)
                    .build();
        brandRepository.save(brand);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s created a brand %s.", user.getFirstName(), user.getLastName(), dto.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_BRAND")
    public ResultWithoutValue updateBrand(Short brandId, BrandUpdateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var brandOpt = brandRepository.findById(brandId);
        if (brandOpt.isEmpty() || !brandOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_NOT_FOUND);
        }

        var brand = brandOpt.get();
        brand.setName(dto.getName());
        brandRepository.save(brand);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s updated a brand %s.", user.getFirstName(), user.getLastName(), dto.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_BRAND")
    public ResultWithoutValue deleteBrand(Short brandId) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var brandCheckOpt = brandRepository.getBrandWithCheck(brandId);
        if (brandCheckOpt.isEmpty()) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_NOT_FOUND);
        }
        var brandWithCheck = brandCheckOpt.get();
        if (brandWithCheck.hasActiveProducts()) {
            return ResultWithoutValue.failure(BrandErrors.BRAND_HAS_PRODUCT);
        }

        var brand = brandWithCheck.brand();
        brand.setIsActive(false);
        brandRepository.save(brand);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s deleted a brand %s.", user.getFirstName(), user.getLastName(), brand.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<BrandDTO>> getBrandsForDropdown() {
        var brands = brandRepository.findByIsActiveTrue()
                .stream()
                .map(b -> new BrandDTO(b.getBrandId(), b.getName()))
                .collect(Collectors.toList());
        return ResultWithValue.success(brands);
    }
}