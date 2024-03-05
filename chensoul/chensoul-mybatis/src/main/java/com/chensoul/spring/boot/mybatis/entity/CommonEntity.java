package com.chensoul.spring.boot.mybatis.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import static com.chensoul.spring.boot.mybatis.entity.SaveEntity.NORM_DATETIME_PATTERN;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 实体超类
 */
@Data
@EqualsAndHashCode(exclude = {"deleted", "createdBy", "createdTime", "updatedBy", "updatedTime"})
public class CommonEntity<ID extends Serializable> implements Serializable {
    private static final long serialVersionUID = -1736224862030957691L;

    private ID id;

    private Boolean deleted;

    private String createdBy;

    @JsonFormat(pattern = NORM_DATETIME_PATTERN)
    private LocalDateTime createdTime;

    private String updatedBy;

    @JsonFormat(pattern = NORM_DATETIME_PATTERN)
    private LocalDateTime updatedTime;

}