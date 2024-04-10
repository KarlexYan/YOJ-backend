package com.karlexyan.yoj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 套题提交记录
 * @TableName examination_submit
 */
@TableName(value ="examination_submit")
@Data
public class ExaminationSubmit implements Serializable {
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
     * 创建用户 id
     */
    private Long userId;

    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 得分
     */
    private Integer score;

    /**
     * 判题状态（0 - 待判题、1 - 判题中、2 - 成功、3 - 失败）
     */
    private Integer submitState;

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