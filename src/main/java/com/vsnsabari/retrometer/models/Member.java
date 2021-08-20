package com.vsnsabari.retrometer.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Member {
    private String sessionId;
    private String clientId;

    @Override
    public String toString() {
        return sessionId + "_" + clientId;
    }
}
