package com.karlexyan.yoj.judge.strategy;

import cn.hutool.json.JSONUtil;
import com.karlexyan.yoj.judge.codesandbox.model.JudgeInfo;
import com.karlexyan.yoj.model.dto.question.JudgeCase;
import com.karlexyan.yoj.model.dto.question.JudgeConfig;
import com.karlexyan.yoj.model.entity.Question;
import com.karlexyan.yoj.model.enums.JudgeInfoMessageEnum;

import java.util.List;
import java.util.Optional;

/**
 * Java程序的判题策略
 */
public class JavaLanguageJudgeStrategy implements JudgeStrategy{
    @Override
    public JudgeInfo doJudge(JudgeContext judgeContext) {
        JudgeInfoMessageEnum judgeInfoMessageEnum = JudgeInfoMessageEnum.ACCEPTED;
        JudgeInfo judgeInfoResponse = new JudgeInfo();
        JudgeInfo judgeInfo = judgeContext.getJudgeInfo();
        if(judgeInfo==null){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.RUNTIME_ERROR;
            judgeInfoResponse.setMemory(0L);
            judgeInfoResponse.setTime(0L);
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        Long memory = Optional.ofNullable(judgeInfo.getMemory()).orElse(0L);
        Long time = Optional.ofNullable(judgeInfo.getTime()).orElse(0L);
        List<String> inputList = judgeContext.getInputList();
        List<String> outputList = judgeContext.getOutputList();
        Question question = judgeContext.getQuestion();
        List<JudgeCase> judgeCaseList = judgeContext.getJudgeCaseList();

        judgeInfoResponse.setMemory(memory);
        judgeInfoResponse.setTime(time);

        // 先判断沙箱执行的结果输出数量是否和预期输出数量相等
        if(outputList.size() != inputList.size()){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }
        // 依次判断每一项输出和预期输出是否相等
        for(int i = 0;i<judgeCaseList.size();i++){
            JudgeCase judgeCase = judgeCaseList.get(i);
            if(!judgeCase.getOutput().equals(outputList.get(i))){
                judgeInfoMessageEnum = JudgeInfoMessageEnum.WRONG_ANSWER;
                judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
                return judgeInfoResponse;
            }
        }
        // 判断题目限制
        String judgeConfigStr = question.getJudgeConfig();
        JudgeConfig judgeConfig = JSONUtil.toBean(judgeConfigStr, JudgeConfig.class);
        Long needMemoryLimit = judgeConfig.getMemoryLimit();
        Long needTimeLimit = judgeConfig.getTimeLimit();
        if(memory > needMemoryLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.MEMORY_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // Java程序本身需要额外执行 1000ms
        long JAVA_PROGRAM_TIME_COST = 1000L;
        if((time - JAVA_PROGRAM_TIME_COST) > needTimeLimit){
            judgeInfoMessageEnum = JudgeInfoMessageEnum.TIME_LIMIT_EXCEEDED;
            judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
            return judgeInfoResponse;
        }

        // 通过判题
        judgeInfoResponse.setMessage(judgeInfoMessageEnum.getValue());
        return judgeInfoResponse;
    }
}
