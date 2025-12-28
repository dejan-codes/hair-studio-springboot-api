package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.brands.BrandCreateDTO;
import com.hairstudio.api.dto.brands.BrandUpdateDTO;
import com.hairstudio.api.dto.brands.BrandDTO;
import com.hairstudio.api.dto.brands.PagedBrandsDTO;

import java.util.List;

public interface BrandService {
    ResultWithValue<PagedBrandsDTO> getPagedBrands(int page, int rowsPerPage);
    ResultWithoutValue createBrand(BrandCreateDTO dto);
    ResultWithoutValue updateBrand(Short brandId, BrandUpdateDTO dto);
    ResultWithoutValue deleteBrand(Short brandId);
    ResultWithValue<List<BrandDTO>> getBrandsForDropdown();
}