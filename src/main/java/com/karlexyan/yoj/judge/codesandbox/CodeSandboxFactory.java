package com.karlexyan.yoj.judge.codesandbox;

import com.karlexyan.yoj.judge.codesandbox.impl.ExampleCodeSandbox;
import com.karlexyan.yoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.karlexyan.yoj.judge.codesandbox.impl.ThirtyPartyCodeSandbox;

/**
 * 代码沙箱工厂
 * 使用模式：工厂模式
 */
public class CodeSandboxFactory {


    public static CodeSandbox newInstance(String type) {
        switch (type) {
            case "example":
                return new ExampleCodeSandbox();
            case "remote":
                return new RemoteCodeSandbox();
            case "thirtyParty":
                return new ThirtyPartyCodeSandbox();
            default:
                return new ExampleCodeSandbox();
        }
    }
}
