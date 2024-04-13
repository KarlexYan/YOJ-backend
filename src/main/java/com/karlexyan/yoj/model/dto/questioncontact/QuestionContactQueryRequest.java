package com.karlexyan.yoj.model.dto.questioncontact;

import com.karlexyan.yoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionContactQueryRequest extends PageRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long questionId;

    private static final long serialVersionUID = 1L;
}
