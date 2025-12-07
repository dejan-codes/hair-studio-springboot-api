package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class ValidationErrors {
    public static final Error INVALID_DATA = new Error(
            "Validation.InvalidData", "Invalid input data.");

    public static final Error NUMBER_OF_PAGES = new Error(
            "Validation.NumberOfPages", "Page and rowsPerPage must be greater than 0.");

    public static final Error IMAGE_READ_ERROR = new Error(
            "Validation.ImageReadError", "Unable to process the uploaded image.");
}