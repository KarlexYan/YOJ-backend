package com.karlexyan.yoj.model.dto.examinationquestionsubmit;

import com.karlexyan.yoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExaminationQuestionSubmitQueryRequest extends PageRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long examinationQuestionId;


    /**
     * 编程语言
     */
    private String submitLanguage;


    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer submitState;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}
