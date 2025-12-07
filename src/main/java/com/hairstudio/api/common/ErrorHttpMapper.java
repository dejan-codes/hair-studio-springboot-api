package com.hairstudio.api.common;

import com.hairstudio.api.errors.PaymentStatusErrors;
import com.hairstudio.api.errors.UserErrors;
import com.hairstudio.api.errors.BrandErrors;
import com.hairstudio.api.errors.ProductTypeErrors;
import com.hairstudio.api.errors.ProductErrors;
import com.hairstudio.api.errors.ServiceErrors;
import com.hairstudio.api.errors.OrderErrors;
import com.hairstudio.api.errors.ReservationErrors;
import com.hairstudio.api.errors.WorkHourErrors;
import com.hairstudio.api.errors.ValidationErrors;
import org.springframework.http.HttpStatus;

import java.util.Map;

public final class ErrorHttpMapper {

    private static final Map<String, HttpStatus> ERROR_STATUS_CODES = Map.ofEntries(

            // User errors
            Map.entry(UserErrors.USER_EXISTS.code(), HttpStatus.BAD_REQUEST),
            Map.entry(UserErrors.INVALID_CONFIRMATION_CODE.code(), HttpStatus.BAD_REQUEST),
            Map.entry(UserErrors.EMAIL_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(UserErrors.USER_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(UserErrors.EMAIL_CONFIRMATION_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(UserErrors.INCORRECT_PASSWORD.code(), HttpStatus.BAD_REQUEST),
            Map.entry(UserErrors.NO_ROLES_SPECIFIED.code(), HttpStatus.BAD_REQUEST),
            Map.entry(UserErrors.INVALID_ROLES.code(), HttpStatus.BAD_REQUEST),

            // Brand errors
            Map.entry(BrandErrors.BRAND_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(BrandErrors.BRAND_HAS_PRODUCT.code(), HttpStatus.BAD_REQUEST),

            // Product type errors
            Map.entry(ProductTypeErrors.PRODUCT_TYPE_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(ProductTypeErrors.PRODUCT_TYPE_HAS_PRODUCT.code(), HttpStatus.BAD_REQUEST),

            // Product errors
            Map.entry(ProductErrors.PRODUCT_NOT_FOUND.code(), HttpStatus.NOT_FOUND),

            // Service errors
            Map.entry(ServiceErrors.SERVICE_NOT_FOUND.code(), HttpStatus.NOT_FOUND),

            // Order errors
            Map.entry(OrderErrors.ORDER_NOT_FOUND.code(), HttpStatus.NOT_FOUND),
            Map.entry(OrderErrors.ORDER_STATUS_NOT_FOUND.code(), HttpStatus.NOT_FOUND),

            // Reservation errors
            Map.entry(ReservationErrors.RESERVATION_NOT_FOUND.code(), HttpStatus.NOT_FOUND),

            // Work hour errors
            Map.entry(WorkHourErrors.TIME_RANGE_ERROR.code(), HttpStatus.BAD_REQUEST),

            // Validation errors
            Map.entry(ValidationErrors.INVALID_DATA.code(), HttpStatus.BAD_REQUEST),
            Map.entry(ValidationErrors.NUMBER_OF_PAGES.code(), HttpStatus.BAD_REQUEST),
            Map.entry(ValidationErrors.IMAGE_READ_ERROR.code(), HttpStatus.BAD_REQUEST),

            // Payment status errors
            Map.entry(PaymentStatusErrors.PAYMENT_STATUS_NOT_FOUND.code(), HttpStatus.BAD_REQUEST)
    );

    public static HttpStatus getStatus(Error error) {
        return ERROR_STATUS_CODES.getOrDefault(error.code(), HttpStatus.BAD_REQUEST);
    }
}
