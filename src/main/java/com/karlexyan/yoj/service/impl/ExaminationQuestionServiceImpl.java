package com.karlexyan.yoj.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.mapper.ExaminationQuestionMapper;
import com.karlexyan.yoj.model.dto.examinationquestion.ExaminationQuestionQueryRequest;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationQuestionVO;
import com.karlexyan.yoj.model.vo.UserVO;
import com.karlexyan.yoj.service.ExaminationQuestionService;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author KarlexYan
* @description 针对表【examination_question(套题题目表)】的数据库操作Service实现
* @createDate 2024-04-14 00:48:38
*/
@Service
public class ExaminationQuestionServiceImpl extends ServiceImpl<ExaminationQuestionMapper, ExaminationQuestion>
    implements ExaminationQuestionService {

    @Resource
    private UserService userService;

    @Override
    public void validExaminationQuestion(ExaminationQuestion examinationQuestion, boolean add) {
        if (examinationQuestion == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = examinationQuestion.getTitle();
        String content = examinationQuestion.getContent();
        String tags = examinationQuestion.getTags();
        String answer = examinationQuestion.getAnswer();
        String judgeCase = examinationQuestion.getJudgeCase();
        String judgeConfig = examinationQuestion.getJudgeConfig();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(answer) && answer.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "答案过长");
        }
        if (StringUtils.isNotBlank(judgeCase) && judgeCase.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题用例过长");
        }
        if (StringUtils.isNotBlank(judgeConfig) && judgeConfig.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "判题配置过长");
        }
    }

    @Override
    public ExaminationQuestionVO getExaminationQuestionVO(ExaminationQuestion examinationQuestion, HttpServletRequest request) {
        ExaminationQuestionVO examinationQuestionVO = ExaminationQuestionVO.objToVo(examinationQuestion);
        // 1. 关联查询用户信息
        Long userId = examinationQuestion.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        examinationQuestionVO.setUserVO(userVO);
        return examinationQuestionVO;
    }

    @Override
    public QueryWrapper<ExaminationQuestion> getQueryWrapper(ExaminationQuestionQueryRequest examinationQuestionQueryRequest) {
        QueryWrapper<ExaminationQuestion> queryWrapper = new QueryWrapper<>();
        if (examinationQuestionQueryRequest == null) {
            return queryWrapper;
        }

        Long examinationQuestionId = examinationQuestionQueryRequest.getExaminationQuestionId();
        Long examinationId = examinationQuestionQueryRequest.getExaminationId();
        String title = examinationQuestionQueryRequest.getTitle();
        Long userId = examinationQuestionQueryRequest.getUserId();
        List<String> tags = examinationQuestionQueryRequest.getTags();
        String sortField = examinationQuestionQueryRequest.getSortField();
        String sortOrder = examinationQuestionQueryRequest.getSortOrder();


        // 拼接查询条件
        queryWrapper.like(ObjectUtils.isNotEmpty(examinationQuestionId), "id", examinationQuestionId);
        queryWrapper.like(ObjectUtils.isNotEmpty(examinationId), "examinationId", examinationId);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq("isDelete",false);  // 将被删除的数据剔除
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<ExaminationQuestionVO> getExaminationQuestionVOPage(Page<ExaminationQuestion> examinationQuestionPage, HttpServletRequest request) {
        List<ExaminationQuestion> examinationQuestionList = examinationQuestionPage.getRecords();
        Page<ExaminationQuestionVO> examinationQuestionVOPage = new Page<>(examinationQuestionPage.getCurrent(), examinationQuestionPage.getSize(), examinationQuestionPage.getTotal());
        if (CollUtil.isEmpty(examinationQuestionList)) {
            return examinationQuestionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = examinationQuestionList.stream().map(ExaminationQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<ExaminationQuestionVO> examinationQuestionVOList = examinationQuestionList.stream().map(examinationQuestion -> {
            ExaminationQuestionVO examinationQuestionVO = ExaminationQuestionVO.objToVo(examinationQuestion);
            Long userId = examinationQuestion.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            examinationQuestionVO.setUserVO(userService.getUserVO(user));
            return examinationQuestionVO;
        }).collect(Collectors.toList());
        examinationQuestionVOPage.setRecords(examinationQuestionVOList);
        return examinationQuestionVOPage;
    }

    @Override
    public List<ExaminationQuestionVO> getExaminationQuestionVOList(List<ExaminationQuestion> examinationQuestionList, HttpServletRequest request) {
        List<ExaminationQuestionVO> examinationQuestionVOList = new ArrayList<>();
        if (CollUtil.isEmpty(examinationQuestionList)) {
            return examinationQuestionVOList;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = examinationQuestionList.stream().map(ExaminationQuestion::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        examinationQuestionVOList = examinationQuestionList.stream().map(examinationQuestion -> {
            ExaminationQuestionVO examinationQuestionVO = ExaminationQuestionVO.objToVo(examinationQuestion);
            Long userId = examinationQuestion.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            examinationQuestionVO.setUserVO(userService.getUserVO(user));
            return examinationQuestionVO;
        }).collect(Collectors.toList());
        return examinationQuestionVOList;
    }
}




