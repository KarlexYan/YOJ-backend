package com.karlexyan.yoj.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.karlexyan.yoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.*;
import com.karlexyan.yoj.model.entity.QuestionSubmit;
import com.karlexyan.yoj.model.enums.QuestionSubmitLanguageEnum;
import com.karlexyan.yoj.model.enums.QuestionSubmitStatusEnum;
import com.karlexyan.yoj.model.vo.QuestionSubmitVO;
import com.karlexyan.yoj.service.QuestionService;
import com.karlexyan.yoj.service.QuestionSubmitService;
import com.karlexyan.yoj.mapper.QuestionSubmitMapper;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author KarlexYan
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2024-01-21 23:24:27
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    /**
     * 提交
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 校验编程语言是否合法
        String language = questionSubmitAddRequest.getSubmitLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        long questionId = questionSubmitAddRequest.getQuestionId();
        // 判断实体是否存在，根据类别获取实体
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        long userId = loginUser.getId();
        // 每个用户串行提交
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(userId);
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setSubmitCode(questionSubmitAddRequest.getSubmitCode());
        questionSubmit.setSubmitLanguage(questionSubmitAddRequest.getSubmitLanguage());
        // 设置初始状态
        questionSubmit.setSubmitState(QuestionSubmitStatusEnum.WAITING.getValue());
        questionSubmit.setJudgeInfo("{}");
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象）
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {

        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String submitLanguage = questionSubmitQueryRequest.getSubmitLanguage();
        Integer submitState = questionSubmitQueryRequest.getSubmitState();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();
        Long userId = questionSubmitQueryRequest.getUserId();

        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        // 拼接查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(submitLanguage), "submitLanguage", submitLanguage);
        queryWrapper.eq(ObjectUtil.isNotEmpty(submitLanguage), "userId", userId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(submitLanguage), "questionId", questionId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(submitState) != null, "submitState", submitState);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    /**
     * 获取查询封装类（单个）
     *
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏：仅本人（提交userId和登录用户id相同）和管理员能看见提交的代码
        Long userId = loginUser.getId();
        // 处理脱敏
        if(!userService.isAdmin(loginUser)){
            if(!userId.equals(questionSubmit.getUserId())){
                questionSubmitVO.setSubmitCode(null);
            }
        }
        return questionSubmitVO;
    }

    /**
     * 获取查询脱敏信息
     *
     * @param questionSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        // 获取原始查询结果
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 遍历脱敏
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser)).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }


}




