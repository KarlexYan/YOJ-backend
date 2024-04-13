package com.karlexyan.yoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.karlexyan.yoj.common.BaseResponse;
import com.karlexyan.yoj.common.DeleteRequest;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.common.ResultUtils;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.model.dto.questioncontact.QuestionContactAddRequest;
import com.karlexyan.yoj.model.dto.questioncontact.QuestionContactQueryRequest;
import com.karlexyan.yoj.model.entity.QuestionContact;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.service.QuestionContactService;
import com.karlexyan.yoj.service.QuestionService;
import com.karlexyan.yoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目评论接口
 */
@RestController
@RequestMapping("/question_contact")
@Slf4j
public class QuestionContactController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    private QuestionContactService questionContactService;

    private final static Gson GSON = new Gson();


    /**
     * 创建
     *
     * @param questionContactAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestionContact(@RequestBody QuestionContactAddRequest questionContactAddRequest, HttpServletRequest request) {
        if (questionContactAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long questionId = questionContactAddRequest.getQuestionId();
        String content = questionContactAddRequest.getContent();


        QuestionContact questionContact = new QuestionContact();
        User loginUser = userService.getLoginUser(request);
        questionContact.setQuestionId(questionId);
        questionContact.setUserId(loginUser.getId());
        questionContact.setContent(content);
        boolean result = questionContactService.save(questionContact);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        long newQuestionContactId = questionContact.getId();
        return ResultUtils.success(newQuestionContactId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestionContact(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        QuestionContact questionContact = questionContactService.getById(id);
        ThrowUtils.throwIf(questionContact == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人或管理员可删除
        if (!questionContact.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionContactService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 根据 id 获取（不脱敏原始输出）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<QuestionContact> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QuestionContact questionContact = questionContactService.getById(id);
        if (questionContact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或者非管理员，不能直接获取所有信息
        if (!questionContact.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(questionContact);
    }

    /**
     * 分页获取列表
     *
     * @param questionContactQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<QuestionContact>> listQuestionContactByPage(@RequestBody QuestionContactQueryRequest questionContactQueryRequest, HttpServletRequest request) {
        long current = questionContactQueryRequest.getCurrent();
        long size = questionContactQueryRequest.getPageSize();
        Page<QuestionContact> questionContactPage = questionContactService.page(new Page<>(current, size),
                questionContactService.getQueryWrapper(questionContactQueryRequest));
        return ResultUtils.success(questionContactPage);
    }
}
