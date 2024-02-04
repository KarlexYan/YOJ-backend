package com.karlexyan.yoj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建请求
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {


    /**
     * 题目 id
     */
    private Long questionId;


    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 用户提交代码
     */
    private String submitCode;


    private static final long serialVersionUID = 1L;
}