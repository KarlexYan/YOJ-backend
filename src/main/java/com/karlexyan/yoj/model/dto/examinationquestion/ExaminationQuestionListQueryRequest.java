package com.karlexyan.yoj.model.dto.examinationquestion;

import lombok.Data;

import java.io.Serializable;

/**
 * 查询请求
 */
@Data
public class ExaminationQuestionListQueryRequest implements Serializable {



    /**
     * 套题id
     */
    private Long examinationId;



    private static final long serialVersionUID = 1L;
}