package com.karlexyan.yoj.judge;

import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import com.karlexyan.yoj.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {

    /**
     * 判题
     * @param questionSubmitId
     * @return
     */
    QuestionSubmit doJudge(long questionSubmitId);


    ExaminationQuestionSubmit doExaminationQuestionJudge(long examinationQuestionSubmitId);
}
