package com.karlexyan.yoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.karlexyan.yoj.model.dto.question.JudgeConfig;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

@Data
public class ExaminationQuestionVO {
    /**
     * id
     */
    private Long id;

    /**
     * 套题ID
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
     * 答案
     */
    private String answer;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 判题配置
     */
    private JudgeConfig judgeConfig;

    /**
     * 题目提交数
     */
    private Integer submitNum;

    /**
     * 题目通过数
     */
    private Integer acceptedNum;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户封装类
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param examinationQuestionVO
     * @return
     */
    public static ExaminationQuestion voToObj(ExaminationQuestionVO examinationQuestionVO) {
        if (examinationQuestionVO == null) {
            return null;
        }
        ExaminationQuestion examinationQuestion = new ExaminationQuestion();
        BeanUtils.copyProperties(examinationQuestionVO, examinationQuestion);
        List<String> tagList = examinationQuestionVO.getTags();
        if (tagList != null) {
            examinationQuestion.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig voJudgeConfig = examinationQuestionVO.getJudgeConfig();
        if (voJudgeConfig != null) {
            examinationQuestion.setJudgeConfig(JSONUtil.toJsonStr(voJudgeConfig));
        }
        return examinationQuestion;
    }

    /**
     * 对象转包装类
     *
     * @param examinationQuestion
     * @return
     */
    public static ExaminationQuestionVO objToVo(ExaminationQuestion examinationQuestion) {
        if (examinationQuestion == null) {
            return null;
        }
        ExaminationQuestionVO examinationQuestionVO = new ExaminationQuestionVO();
        BeanUtils.copyProperties(examinationQuestion, examinationQuestionVO);
        // 转换成包装类
        List<String> tagList = JSONUtil.toList(examinationQuestion.getTags(), String.class);
        examinationQuestionVO.setTags(tagList);
        String judgeConfig = examinationQuestion.getJudgeConfig();
        examinationQuestionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig, JudgeConfig.class));
        return examinationQuestionVO;
    }

    private static final long serialVersionUID = 1L;
}
