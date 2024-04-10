package com.karlexyan.yoj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 套题题目关联表
 * @TableName examination_question
 */
@TableName(value ="examination_question")
@Data
public class ExaminationQuestion implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 套题 id
     */
    private Long examinationId;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}