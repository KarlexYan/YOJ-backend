package com.karlexyan.yoj.model.vo;

import com.karlexyan.yoj.model.entity.QuestionContact;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目评论
 * @TableName question_contact
 */
@Data
public class QuestionContactVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户vo
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionContactVO
     * @return
     */
    public static QuestionContact voToObj(QuestionContactVO questionContactVO) {
        if (questionContactVO == null) {
            return null;
        }
        QuestionContact questionContact = new QuestionContact();
        BeanUtils.copyProperties(questionContactVO, questionContact);
        return questionContact;
    }

    /**
     * 对象转包装类
     *
     * @param questionContact
     * @return
     */
    public static QuestionContactVO objToVo(QuestionContact questionContact) {
        if (questionContact == null) {
            return null;
        }
        QuestionContactVO questionContactVO = new QuestionContactVO();
        BeanUtils.copyProperties(questionContact, questionContactVO);
        return questionContactVO;
    }

    private static final long serialVersionUID = 1L;
}