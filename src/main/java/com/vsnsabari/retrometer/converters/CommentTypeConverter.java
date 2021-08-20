package com.vsnsabari.retrometer.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import com.vsnsabari.retrometer.models.CommentType;

@Converter(autoApply = true)
public class CommentTypeConverter implements AttributeConverter<CommentType, String> {
    @Override
    public String convertToDatabaseColumn(CommentType accountType) {
        if (accountType == null) {
            return null;
        }
        return accountType.getCode();
    }

    @SneakyThrows
    @Override
    public CommentType convertToEntityAttribute(String code) {
        if (!StringUtils.hasLength(code)) {
            return null;
        }
        return CommentType.getByCode(code);
    }
}
