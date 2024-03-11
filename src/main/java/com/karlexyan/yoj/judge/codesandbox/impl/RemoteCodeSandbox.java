package com.karlexyan.yoj.judge.codesandbox.impl;

import com.karlexyan.yoj.judge.codesandbox.CodeSandbox;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 远程代码沙箱（项目实际使用沙箱）
 */
public class RemoteCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        return null;
    }
}
