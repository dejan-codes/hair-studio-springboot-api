package com.hairstudio.api.controller;

import com.hairstudio.api.dto.products.BuyProductDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import com.hairstudio.api.dto.products.ProductCreateDTO;
import com.hairstudio.api.dto.products.ProductUpdateDTO;
import com.hairstudio.api.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/Product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<?> getProducts(@RequestParam int page,
                                         @RequestParam int rowsPerPage,
                                         @RequestParam(required = false) String search,
                                         @RequestParam(required = false) Short brand,
                                         @RequestParam(required = false) Short type,
                                         @RequestParam(defaultValue = "0") int minPrice,
                                         @RequestParam(defaultValue = "9999") int maxPrice,
                                         @RequestParam(defaultValue = "") String sortOrder) {
        var result = productService.getProducts(page, rowsPerPage, search, brand, type, minPrice, maxPrice, sortOrder);
        return result.toResponseEntity();
    }

    @GetMapping("/popularity")
    public ResponseEntity<?> getMostPopularProducts() {
        var result = productService.getMostPopularProducts();
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @GetMapping("/orders")
    public ResponseEntity<?> getOrders(@RequestParam int page, @RequestParam int rowsPerPage) {
        var result = productService.getOrders(page, rowsPerPage);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(@ModelAttribute ProductCreateDTO dto) {
        var result = productService.createProduct(dto);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).USER.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).EMPLOYEE.getRoleName()) or " +
            "hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PostMapping("/buy")
    public ResponseEntity<?> buyProducts(@RequestBody List<BuyProductDTO> dtoList) {
        var result = productService.buyProducts(dtoList);
        return result.toResponseEntity();
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<?> getSessionDetails(@PathVariable String sessionId) {
        var details = productService.getSessionDetails(sessionId);
        return ResponseEntity.ok(details);
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload,
                                                 @RequestHeader("Stripe-Signature") String stripeSignature) {
        var result = productService.handleStripeWebhook(payload, stripeSignature);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping(path = "{productId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateProduct(@PathVariable short productId, @ModelAttribute ProductUpdateDTO dto) {
        var result = productService.updateProduct(productId, dto);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @PutMapping("/change-order-status")
    public ResponseEntity<?> changeOrderStatus(@RequestParam short orderId, @RequestParam short status) {
        var result = productService.changeOrderStatus(orderId, status);
        return result.toResponseEntity();
    }

    @PreAuthorize("hasRole(T(com.hairstudio.api.model.enums.RoleEnum).ADMINISTRATOR.getRoleName())")
    @DeleteMapping("/{productId}")
    public ResponseEntity<?> deleteProduct(@PathVariable short productId) {
        var result = productService.deleteProduct(productId);
        return result.toResponseEntity();
    }
}
