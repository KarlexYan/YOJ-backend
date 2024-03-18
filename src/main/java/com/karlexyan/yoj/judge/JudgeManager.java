package com.karlexyan.yoj.judge;

import com.karlexyan.yoj.judge.codesandbox.model.JudgeInfo;
import com.karlexyan.yoj.judge.strategy.DefaultJudgeStrategy;
import com.karlexyan.yoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.karlexyan.yoj.judge.strategy.JudgeContext;
import com.karlexyan.yoj.judge.strategy.JudgeStrategy;
import com.karlexyan.yoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getSubmitLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        // 对JAVA进行走额外策略
        if("java".equals(language)){
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }
}
