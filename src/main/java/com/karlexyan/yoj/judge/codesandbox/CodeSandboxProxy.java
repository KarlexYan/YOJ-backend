package com.karlexyan.yoj.judge.codesandbox;

import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
public class CodeSandboxProxy implements CodeSandbox {

    private final CodeSandbox codeSandbox;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息：" + executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息：" + executeCodeResponse);
        return executeCodeResponse;
    }
}
