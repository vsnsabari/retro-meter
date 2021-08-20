package com.vsnsabari.retrometer.models;

import lombok.Data;
import lombok.NonNull;

@Data
public class EventDto {
    @NonNull
    private String message;
}
