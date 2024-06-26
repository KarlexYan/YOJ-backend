package com.karlexyan.yoj.judge.strategy;

import com.karlexyan.yoj.judge.codesandbox.model.JudgeInfo;
import com.karlexyan.yoj.model.dto.question.JudgeCase;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import lombok.Data;

import java.util.List;

/**
 * 上下文（用于定义在策略中传递的参数）
 */
@Data
public class JudgeExaminationContext {

    private JudgeInfo judgeInfo;

    private List<String> inputList;

    private List<String> outputList;

    private List<JudgeCase> judgeCaseList;

    private ExaminationQuestion examinationQuestion;

    private ExaminationQuestionSubmit examinationQuestionSubmit;
}
