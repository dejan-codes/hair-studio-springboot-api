package com.hairstudio.api.service;

import com.hairstudio.api.common.ResultWithValue;
import com.hairstudio.api.common.ResultWithoutValue;
import com.hairstudio.api.dto.orders.PagedOrdersDTO;
import com.hairstudio.api.dto.products.PagedProductsDTO;
import com.hairstudio.api.dto.products.ProductDisplayDTO;
import com.hairstudio.api.dto.products.ProductCreateDTO;
import com.hairstudio.api.dto.products.BuyProductDTO;
import com.hairstudio.api.dto.products.ProductUpdateDTO;
import com.hairstudio.api.dto.products.UrlResponseDTO;

import java.util.List;

public interface ProductService {

    ResultWithValue<PagedProductsDTO> getProducts(int page, int rowsPerPage, String search, Short brand, Short type, int minPrice, int maxPrice, String sortOrder);

    ResultWithValue<List<ProductDisplayDTO>> getMostPopularProducts();

    ResultWithValue<PagedOrdersDTO> getOrders(short tokenUserId, int page, int rowsPerPage);

    ResultWithoutValue createProduct(ProductCreateDTO dto, short tokenUserId);

    ResultWithValue<UrlResponseDTO> buyProducts(List<BuyProductDTO> dtoList, short tokenUserId);

    Object getSessionDetails(String sessionId);

    ResultWithoutValue handleStripeWebhook(String payload, String stripeSignature);

    ResultWithoutValue updateProduct(short productId, ProductUpdateDTO dto, short tokenUserId);

    ResultWithoutValue changeOrderStatus(short orderId, short orderStatusId, short tokenUserId);

    ResultWithoutValue deleteProduct(short productId, short tokenUserId);
}
