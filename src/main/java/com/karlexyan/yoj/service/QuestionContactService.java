package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.questioncontact.QuestionContactQueryRequest;
import com.karlexyan.yoj.model.entity.QuestionContact;

/**
* @author KarlexYan
* @description 针对表【question_contact(题目评论)】的数据库操作Service
* @createDate 2024-04-09 19:51:07
*/
public interface QuestionContactService extends IService<QuestionContact> {

    /**
     * 获取查询条件
     *
     * @param questionContactQueryRequest
     * @return
     */
    QueryWrapper<QuestionContact> getQueryWrapper(QuestionContactQueryRequest questionContactQueryRequest);
}
