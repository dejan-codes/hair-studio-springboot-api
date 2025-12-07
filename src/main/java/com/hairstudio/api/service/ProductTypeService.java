package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.producttypes.PagedProductTypesDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeCreateDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeUpdateDTO;
import com.hairstudio.api.dto.producttypes.ProductTypeDTO;

import java.util.List;

public interface ProductTypeService {
    ResultWithValue<PagedProductTypesDTO> getPagedProductTypes(int page, int rowsPerPage);
    ResultWithoutValue createProductType(ProductTypeCreateDTO dto, Short tokenUserId);
    ResultWithoutValue updateProductType(Short productTypeId, ProductTypeUpdateDTO dto, Short tokenUserId);
    ResultWithoutValue deleteProductType(Short productTypeId, Short tokenUserId);
    ResultWithValue<List<ProductTypeDTO>> getProductTypesForDropdown();
}