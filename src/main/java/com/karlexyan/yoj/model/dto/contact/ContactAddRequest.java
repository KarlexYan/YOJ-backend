package com.karlexyan.yoj.model.dto.contact;

import lombok.Data;

import java.io.Serializable;

/**
 * 创建请求
 *
 */
@Data
public class ContactAddRequest implements Serializable {



    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;



    private static final long serialVersionUID = 1L;
}