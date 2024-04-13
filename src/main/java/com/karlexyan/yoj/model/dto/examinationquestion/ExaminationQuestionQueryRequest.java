package com.karlexyan.yoj.model.dto.examinationquestion;

import lombok.Data;

import java.io.Serializable;

@Data
public class ExaminationQuestionQueryRequest  implements Serializable {


    /**
     * 套题 id
     */
    private Long examinationId;




    private static final long serialVersionUID = 1L;
}
