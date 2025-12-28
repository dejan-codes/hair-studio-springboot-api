package com.hairstudio.api.service;

import com.hairstudio.api.audit.Auditable;
import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.producttypes.PagedProductTypesDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeCreateDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeUpdateDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeDTO;
import com.hairstudio.api.errors.ProductTypeErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.ValidationErrors;
import com.hairstudio.api.model.entity.ProductType;
import com.hairstudio.api.model.entity.Message;
import com.hairstudio.api.repository.ProductTypeRepository;
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
public class ProductTypeServiceImpl implements ProductTypeService {

    private final CurrentUserContext currentUserContext;
    private final ProductTypeRepository productTypeRepository;
    private final UserRepository userRepository;
    private final MessageRepository messageRepository;

    @Override
    public ResultWithValue<PagedProductTypesDTO> getPagedProductTypes(int page, int rowsPerPage) {
        if (page < 1 || rowsPerPage < 1) {
            return ResultWithValue.failure(ValidationErrors.NUMBER_OF_PAGES);
        }

        var pageable = PageRequest.of(page - 1, rowsPerPage);
        var productTypesPage = productTypeRepository.findByIsActiveTrue(pageable);

        var result = productTypesPage.getContent().stream()
                .map(b -> new ProductTypeDTO(b.getProductTypeId(), b.getName()))
                .collect(Collectors.toList());

        return ResultWithValue.success(new PagedProductTypesDTO(productTypesPage.getTotalElements(), result));
    }

    @Override
    @Transactional
    @Auditable(action = "CREATE_PRODUCT_TYPE")
    public ResultWithoutValue createProductType(ProductTypeCreateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        if (productTypeRepository.existsByName(dto.getName())) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_EXISTS);
        }

        var productType = ProductType.builder()
                .name(dto.getName())
                .isActive(true)
                .build();
        productTypeRepository.save(productType);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s created a product type %s.", user.getFirstName(), user.getLastName(), dto.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "UPDATE_PRODUCT_TYPE")
    public ResultWithoutValue updateProductType(Short productTypeId, ProductTypeUpdateDTO dto) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var productTypeOpt = productTypeRepository.findById(productTypeId);
        if (productTypeOpt.isEmpty() || !productTypeOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_NOT_FOUND);
        }

        var productType = productTypeOpt.get();
        productType.setName(dto.getName());
        productTypeRepository.save(productType);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s updated a product type %s.", user.getFirstName(), user.getLastName(), dto.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    @Transactional
    @Auditable(action = "DELETE_PRODUCT_TYPE")
    public ResultWithoutValue deleteProductType(Short productTypeId) {
        var userOpt = userRepository.findById(currentUserContext.getUserId());
        if (userOpt.isEmpty() || !userOpt.get().getIsActive()) {
            return ResultWithoutValue.failure(UserErrors.USER_NOT_FOUND);
        }
        var user = userOpt.get();

        var productTypeCheckOpt = productTypeRepository.getProductTypeWithCheck(productTypeId);
        if (productTypeCheckOpt.isEmpty()) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_NOT_FOUND);
        }
        var productTypeWithCheck = productTypeCheckOpt.get();
        if (productTypeWithCheck.hasActiveProducts()) {
            return ResultWithoutValue.failure(ProductTypeErrors.PRODUCT_TYPE_HAS_PRODUCT);
        }

        var productType = productTypeWithCheck.productType();
        productType.setIsActive(false);
        productTypeRepository.save(productType);

        Message message = Message.builder()
                .user(user)
                .createdAt(Instant.now())
                .content(String.format("User %s %s deleted a product type %s.", user.getFirstName(), user.getLastName(), productType.getName())).build();
        messageRepository.save(message);

        return ResultWithoutValue.success();
    }

    @Override
    public ResultWithValue<List<ProductTypeDTO>> getProductTypesForDropdown() {
        var productTypes = productTypeRepository.findByIsActiveTrue()
                .stream()
                .map(b -> new ProductTypeDTO(b.getProductTypeId(), b.getName()))
                .collect(Collectors.toList());
        return ResultWithValue.success(productTypes);
    }
}