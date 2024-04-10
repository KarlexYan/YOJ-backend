package com.karlexyan.yoj.model.dto.examinationquestion;

import com.karlexyan.yoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExaminationQuestionQueryRequest extends PageRequest implements Serializable {


    /**
     * 套题 id
     */
    private Long examinationId;




    private static final long serialVersionUID = 1L;
}
