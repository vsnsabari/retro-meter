package com.vsnsabari.retrometer.models;

import lombok.Data;
import lombok.NonNull;

import com.vsnsabari.retrometer.entities.Comment;

@Data
public class EventDto {
    @NonNull
    private EventType type;
    @NonNull
    private Comment comment;
}
