package com.karlexyan.yoj.judge.codesandbox;

import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeResponse;

public interface CodeSandbox {

    /**
     * 代码沙箱执行代码接口
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
