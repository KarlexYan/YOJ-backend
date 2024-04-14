package com.karlexyan.yoj.judge.strategy;

import com.karlexyan.yoj.judge.codesandbox.model.JudgeInfo;

/**
 * 判题策略
 */
public interface JudgeStrategy {

    /**
     * 执行判题
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);

    /**
     * 执行套题判题
     * @param judgeExaminationContext
     * @return
     */
    JudgeInfo doExaminationJudge(JudgeExaminationContext judgeExaminationContext);
}
