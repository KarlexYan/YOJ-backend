package com.karlexyan.yoj.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.mapper.ExaminationQuestionMapper;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.service.ExaminationQuestionService;
import org.springframework.stereotype.Service;

/**
* @author KarlexYan
* @description 针对表【examination_question(套题题目关联表)】的数据库操作Service实现
* @createDate 2024-04-09 19:50:58
*/
@Service
public class ExaminationQuestionServiceImpl extends ServiceImpl<ExaminationQuestionMapper, ExaminationQuestion>
    implements ExaminationQuestionService {


}




