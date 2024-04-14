package com.karlexyan.yoj.judge;

import cn.hutool.json.JSONUtil;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.judge.codesandbox.CodeSandbox;
import com.karlexyan.yoj.judge.codesandbox.CodeSandboxFactory;
import com.karlexyan.yoj.judge.codesandbox.CodeSandboxProxy;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeResponse;
import com.karlexyan.yoj.judge.codesandbox.model.JudgeInfo;
import com.karlexyan.yoj.judge.strategy.JudgeContext;
import com.karlexyan.yoj.judge.strategy.JudgeExaminationContext;
import com.karlexyan.yoj.model.dto.question.JudgeCase;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import com.karlexyan.yoj.model.entity.Question;
import com.karlexyan.yoj.model.entity.QuestionSubmit;
import com.karlexyan.yoj.model.enums.ExaminationQuestionSubmitStatusEnum;
import com.karlexyan.yoj.model.enums.QuestionSubmitStatusEnum;
import com.karlexyan.yoj.service.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JudgeServiceImpl implements JudgeService{

    @Resource
    private QuestionService questionService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private JudgeManager judgeManager;

    @Resource
    private ExaminationQuestionService examinationQuestionService;

    @Resource
    private ExaminationSubmitService examinationSubmitService;

    @Resource
    private ExaminationQuestionSubmitService examinationQuestionSubmitService;

    @Value("${codesandbox.type:example}")
    private String type;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        //1. 传入题目的提交id，获取到对应的题目、提交信息（包含代码、编程语言等）
        QuestionSubmit questionSubmit = questionSubmitService.getById(questionSubmitId);
        if(questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long questionId = questionSubmit.getQuestionId();
        Question question = questionService.getById(questionId);
        if(question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目不存在");
        }
        //2. 如果题目提交状态不为等待中，就不用重复执行了
        if (!questionSubmit.getSubmitState().equals(QuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");
        }
        //3. 更改判题（题目提交）的状态为“判题中”，防止重复执行，也能让用户即时看到状态
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = questionSubmitService.updateById(questionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }
        //4. 调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = questionSubmit.getSubmitLanguage();
        String code = questionSubmit.getSubmitCode();
        // 获取输入用例
        String judgeCaseStr = question.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 调用沙箱
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);  // 执行
        if(executeCodeResponse.getStatus() == null){
            questionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.FAILED.getValue());
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        //5. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeContext judgeContext = new JudgeContext();
        judgeContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeContext.setInputList(inputList);
        judgeContext.setOutputList(outputList);
        judgeContext.setJudgeCaseList(judgeCaseList);
        judgeContext.setQuestion(question);
        judgeContext.setQuestionSubmit(questionSubmit);
        JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
        //6. 修改数据库中判题结果
        questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmitId);
        questionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.SUCCEED.getValue());
        questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = questionSubmitService.updateById(questionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionSubmitService.getById(questionId);
        return questionSubmitResult;
    }

    @Override
    public ExaminationQuestionSubmit doExaminationQuestionJudge(long examinationQuestionSubmitId) {
        //1. 传入题目的提交id，获取到对应的题目、提交信息（包含代码、编程语言等）
        ExaminationQuestionSubmit examinationQuestionSubmit = examinationQuestionSubmitService.getById(examinationQuestionSubmitId);
        if(examinationQuestionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"提交信息不存在");
        }
        Long examinationQuestionId = examinationQuestionSubmit.getExaminationQuestionId();
        ExaminationQuestion examinationQuestion = examinationQuestionService.getById(examinationQuestionId);
        if(examinationQuestion == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"套题题目不存在");
        }
        //2. 如果题目提交状态不为等待中，就不用重复执行了
        if (!examinationQuestionSubmit.getSubmitState().equals(ExaminationQuestionSubmitStatusEnum.WAITING.getValue())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题中");
        }
        //3. 更改判题（题目提交）的状态为“判题中”，防止重复执行，也能让用户即时看到状态
        ExaminationQuestionSubmit examinationQuestionSubmitUpdate = new ExaminationQuestionSubmit();
        examinationQuestionSubmitUpdate.setId(examinationQuestionSubmitId);
        examinationQuestionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.RUNNING.getValue());
        boolean update = examinationQuestionSubmitService.updateById(examinationQuestionSubmitUpdate);
        if(!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"题目状态更新错误");
        }

        //4. 调用沙箱，获取到执行结果
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        codeSandbox = new CodeSandboxProxy(codeSandbox);
        String language = examinationQuestionSubmit.getSubmitLanguage();
        String code = examinationQuestionSubmit.getSubmitCode();
        // 获取输入用例
        String judgeCaseStr = examinationQuestion.getJudgeCase();
        List<JudgeCase> judgeCaseList = JSONUtil.toList(judgeCaseStr, JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 调用沙箱
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);  // 执行
        if(executeCodeResponse.getStatus() == null){
            examinationQuestionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.FAILED.getValue());
        }
        List<String> outputList = executeCodeResponse.getOutputList();
        //5. 根据沙箱的执行结果，设置题目的判题状态和信息
        JudgeExaminationContext judgeExaminationContext = new JudgeExaminationContext();
        judgeExaminationContext.setJudgeInfo(executeCodeResponse.getJudgeInfo());
        judgeExaminationContext.setInputList(inputList);
        judgeExaminationContext.setOutputList(outputList);
        judgeExaminationContext.setJudgeCaseList(judgeCaseList);
        judgeExaminationContext.setExaminationQuestion(examinationQuestion);
        judgeExaminationContext.setExaminationQuestionSubmit(examinationQuestionSubmit);
        JudgeInfo judgeInfo = judgeManager.doExaminationJudge(judgeExaminationContext);
        //6. 修改数据库中判题结果
        examinationQuestionSubmitUpdate = new ExaminationQuestionSubmit();
        examinationQuestionSubmitUpdate.setId(examinationQuestionSubmitId);
        examinationQuestionSubmitUpdate.setSubmitState(QuestionSubmitStatusEnum.SUCCEED.getValue());
        examinationQuestionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        update = examinationQuestionSubmitService.updateById(examinationQuestionSubmitUpdate);
        if (!update) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目状态更新错误");
        }
        ExaminationQuestionSubmit examinationQuestionSubmitResult = examinationQuestionSubmitService.getById(examinationQuestionId);
        return examinationQuestionSubmitResult;
    }
}
