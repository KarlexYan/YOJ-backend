package com.karlexyan.yoj.model.dto.questionsubmit;

import lombok.Data;

/**
 * 代码提交记录
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗时间，单位为 ms
     */
    private Long time;

    /**
     * // 消耗内存，单位为 kb
     */
    private Long memory;
}
