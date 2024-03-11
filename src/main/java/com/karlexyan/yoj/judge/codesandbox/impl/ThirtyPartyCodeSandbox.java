package com.karlexyan.yoj.judge.codesandbox.impl;

import com.karlexyan.yoj.judge.codesandbox.CodeSandbox;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.karlexyan.yoj.judge.codesandbox.model.ExecuteCodeResponse;

/**
 * 第三方代码沙箱（用于调用网上现成的代码沙箱）
 */
public class ThirtyPartyCodeSandbox implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
