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



}
