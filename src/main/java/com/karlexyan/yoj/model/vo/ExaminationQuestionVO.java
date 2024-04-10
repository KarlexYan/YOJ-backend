package com.karlexyan.yoj.model.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExaminationQuestionVO implements Serializable {

    private Long examinationQuestionId;

    private Long examinationId;

    private QuestionVO questionVO;


    private static final long serialVersionUID = 1L;
}
