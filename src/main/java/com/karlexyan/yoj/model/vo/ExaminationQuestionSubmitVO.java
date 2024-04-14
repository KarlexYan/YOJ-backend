package com.karlexyan.yoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.karlexyan.yoj.model.dto.questionsubmit.JudgeInfo;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * 题目提交
 * @TableName examination_question_submit
 */
@Data
public class ExaminationQuestionSubmitVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目 id
     */
    private Long examinationQuestionId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 判题信息
     */
    private JudgeInfo judgeInfo;

    /**
     * 编程语言
     */
    private String submitLanguage;

    /**
     * 用户提交代码
     */
    private String submitCode;

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
     * 提交用户脱敏信息
     */
    private UserVO userVO;

    /**
     * 对应题目信息
     */
    private ExaminationQuestionVO examinationQuestionVO;

    /**
     * 包装类转对象
     *
     * @param examinationQuestionSubmitVO
     * @return
     */
    public static ExaminationQuestionSubmit voToObj(ExaminationQuestionSubmitVO examinationQuestionSubmitVO) {
        if (examinationQuestionSubmitVO == null) {
            return null;
        }
        ExaminationQuestionSubmit examinationQuestionSubmit = new ExaminationQuestionSubmit();
        BeanUtils.copyProperties(examinationQuestionSubmitVO, examinationQuestionSubmit);
        JudgeInfo judgeInfoObj = examinationQuestionSubmitVO.getJudgeInfo();
        if (judgeInfoObj != null) {
            examinationQuestionSubmit.setJudgeInfo(JSONUtil.toJsonStr(judgeInfoObj));
        }
        return examinationQuestionSubmit;
    }

    /**
     * 对象转包装类
     *
     * @param examinationQuestionSubmit
     * @return
     */
    public static ExaminationQuestionSubmitVO objToVo(ExaminationQuestionSubmit examinationQuestionSubmit) {
        if (examinationQuestionSubmit == null) {
            return null;
        }
        ExaminationQuestionSubmitVO examinationQuestionSubmitVO = new ExaminationQuestionSubmitVO();
        BeanUtils.copyProperties(examinationQuestionSubmit, examinationQuestionSubmitVO);
        String judgeInfoStr = examinationQuestionSubmit.getJudgeInfo();
        // 转换成包装类
        examinationQuestionSubmitVO.setJudgeInfo(JSONUtil.toBean(judgeInfoStr, JudgeInfo.class));
        return examinationQuestionSubmitVO;
    }

    private static final long serialVersionUID = 1L;
}