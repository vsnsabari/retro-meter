package com.vsnsabari.retrometer.entities;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.vsnsabari.retrometer.converters.CommentTypeConverter;
import com.vsnsabari.retrometer.models.CommentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@ToString
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String commentText;
    private String sessionId;
    private String addedBy;
    @Convert(converter = CommentTypeConverter.class)
    private CommentType commentType;
    private int upVotes = 0;
    private int downVotes = 0;
    @JsonSerialize(using = ToStringSerializer.class)
    private LocalDateTime createdDate = LocalDateTime.now();
}
