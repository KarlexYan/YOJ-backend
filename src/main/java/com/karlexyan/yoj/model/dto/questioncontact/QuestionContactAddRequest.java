package com.karlexyan.yoj.model.dto.questioncontact;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 */
@Data
public class QuestionContactAddRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 内容
     */
    private String content;


    private static final long serialVersionUID = 1L;
}