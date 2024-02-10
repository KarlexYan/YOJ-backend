package com.karlexyan.yoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karlexyan.yoj.common.BaseResponse;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.common.ResultUtils;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.karlexyan.yoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.QuestionSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.QuestionSubmitVO;
import com.karlexyan.yoj.service.QuestionSubmitService;
import com.karlexyan.yoj.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return 提交记录id
     */
    @PostMapping("/")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final User loginUser = userService.getLoginUser(request);
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(questionSubmitId);
    }

    @PostMapping("/list/page")
    @ApiOperation(value = "分页获取题目提交列表")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                   HttpServletRequest request) {
        // 获取页号
        long current = questionSubmitQueryRequest.getCurrent();
        // 获取当前页数量
        long pageSize = questionSubmitQueryRequest.getPageSize();

        // 从数据库查询到原始的题目提交信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, pageSize), questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 脱敏后返回
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }

}
