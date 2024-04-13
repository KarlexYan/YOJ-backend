package com.karlexyan.yoj.model.dto.examinationquestion;

import com.karlexyan.yoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ExaminationQuestionQueryRequest extends PageRequest implements Serializable {

    /**
     * 套题题目ID
     */
    private Long examinationQuestionId;

    /**
     * 套题id
     */
    private Long examinationId;


    /**
     * 创建题目用户 id
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 题目答案
     */
    private String answer;


    private static final long serialVersionUID = 1L;
}