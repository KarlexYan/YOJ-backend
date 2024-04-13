package com.karlexyan.yoj.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.mapper.QuestionContactMapper;
import com.karlexyan.yoj.model.dto.questioncontact.QuestionContactQueryRequest;
import com.karlexyan.yoj.model.entity.QuestionContact;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.QuestionContactVO;
import com.karlexyan.yoj.service.QuestionContactService;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author KarlexYan
* @description 针对表【question_contact(题目评论)】的数据库操作Service实现
* @createDate 2024-04-09 19:51:07
*/
@Service
public class QuestionContactServiceImpl extends ServiceImpl<QuestionContactMapper, QuestionContact>
    implements QuestionContactService {

    @Resource
    private UserService userService;

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

    @Override
    public Page<QuestionContactVO> getQuestionContactVOPage(Page<QuestionContact> questionContactPage, HttpServletRequest request) {
        List<QuestionContact> questionContactList = questionContactPage.getRecords();
        Page<QuestionContactVO> questionContactVOPage = new Page<>(questionContactPage.getCurrent(), questionContactPage.getSize(), questionContactPage.getTotal());
        if (CollUtil.isEmpty(questionContactList)) {
            return questionContactVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionContactList.stream().map(QuestionContact::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionContactVO> questionContactVOList = questionContactList.stream().map(questionContact -> {
            QuestionContactVO questionContactVO = QuestionContactVO.objToVo(questionContact);
            Long userId = questionContact.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            questionContactVO.setUserVO(userService.getUserVO(user));
            return questionContactVO;
        }).collect(Collectors.toList());
        questionContactVOPage.setRecords(questionContactVOList);
        return questionContactVOPage;
    }
}




