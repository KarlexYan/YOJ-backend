package com.karlexyan.yoj.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.mapper.QuestionContactMapper;
import com.karlexyan.yoj.model.dto.questioncontact.QuestionContactQueryRequest;
import com.karlexyan.yoj.model.entity.QuestionContact;
import com.karlexyan.yoj.service.QuestionContactService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

/**
* @author KarlexYan
* @description 针对表【question_contact(题目评论)】的数据库操作Service实现
* @createDate 2024-04-09 19:51:07
*/
@Service
public class QuestionContactServiceImpl extends ServiceImpl<QuestionContactMapper, QuestionContact>
    implements QuestionContactService {

    /**
     * 获取查询条件
     * @param questionContactQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionContact> getQueryWrapper(QuestionContactQueryRequest questionContactQueryRequest) {
        QueryWrapper<QuestionContact> queryWrapper = new QueryWrapper<>();
        if (questionContactQueryRequest == null) {
            return queryWrapper;
        }

        Long questionId = questionContactQueryRequest.getQuestionId();
        String sortField = questionContactQueryRequest.getSortField();
        String sortOrder = questionContactQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete",false);  // 将被删除的数据剔除
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




