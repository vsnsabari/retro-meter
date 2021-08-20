package com.vsnsabari.retrometer.models;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;

import com.vsnsabari.retrometer.exceptions.InvalidCommentTypeException;

@AllArgsConstructor
@Getter
public enum CommentType {
    GOOD("G"),
    BAD("B"),
    IMPROVE("I");

    private final String code;
    private static final Map<String, CommentType> commentTypeCodeMap;

    static {
        commentTypeCodeMap =
                Arrays.stream(CommentType.values()).collect(Collectors.toMap(type -> type.code, type -> type));
    }

    public static CommentType getByCode(String code) {
        return Optional.ofNullable(commentTypeCodeMap.get(code)).orElseThrow(() -> new InvalidCommentTypeException(code));
    }

}
