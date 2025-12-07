package com.hairstudio.api.errors;

import com.hairstudio.api.common.Error;

public final class ServiceErrors {
    public static final Error SERVICE_NOT_FOUND = new Error(
            "Service.ServiceNotFound", "Service not found.");
}