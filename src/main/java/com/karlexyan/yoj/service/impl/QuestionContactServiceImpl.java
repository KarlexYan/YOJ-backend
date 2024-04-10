package com.karlexyan.yoj.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.mapper.QuestionContactMapper;
import com.karlexyan.yoj.model.entity.QuestionContact;
import com.karlexyan.yoj.service.QuestionContactService;
import org.springframework.stereotype.Service;

/**
* @author KarlexYan
* @description 针对表【question_contact(题目评论)】的数据库操作Service实现
* @createDate 2024-04-09 19:51:07
*/
@Service
public class QuestionContactServiceImpl extends ServiceImpl<QuestionContactMapper, QuestionContact>
    implements QuestionContactService {

}




