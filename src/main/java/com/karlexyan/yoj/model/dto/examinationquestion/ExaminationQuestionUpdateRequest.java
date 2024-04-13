package com.karlexyan.yoj.model.dto.examinationquestion;

import com.karlexyan.yoj.model.dto.question.JudgeCase;
import com.karlexyan.yoj.model.dto.question.JudgeConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 */
@Data
public class ExaminationQuestionUpdateRequest implements Serializable {

    /**
     * 套题题目ID
     */
    private Long id;


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

    /**
     * 判题用例
     */
    private List<JudgeCase> judgeCase;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;


    private static final long serialVersionUID = 1L;
}