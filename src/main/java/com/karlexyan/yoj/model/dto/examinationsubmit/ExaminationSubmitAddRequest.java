package com.karlexyan.yoj.model.dto.examinationsubmit;

import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class ExaminationSubmitAddRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long examinationId;


    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 提交题目信息
     */
    private List<ExaminationQuestionSubmit> examinationQuestionSubmitList;


    private static final long serialVersionUID = 1L;
}