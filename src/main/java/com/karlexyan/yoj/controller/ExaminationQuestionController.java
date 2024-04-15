package com.karlexyan.yoj.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.karlexyan.yoj.annotation.AuthCheck;
import com.karlexyan.yoj.common.BaseResponse;
import com.karlexyan.yoj.common.DeleteRequest;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.common.ResultUtils;
import com.karlexyan.yoj.constant.UserConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.model.dto.examinationquestion.*;
import com.karlexyan.yoj.model.dto.question.JudgeCase;
import com.karlexyan.yoj.model.dto.question.JudgeConfig;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationQuestionVO;
import com.karlexyan.yoj.service.ExaminationQuestionService;
import com.karlexyan.yoj.service.ExaminationService;
import com.karlexyan.yoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 套题题目接口
 */
@RestController
@RequestMapping("/examination_question")
@Slf4j
public class ExaminationQuestionController {
    @Resource
    private UserService userService;

    @Resource
    private ExaminationService examinationService;

    @Resource
    private ExaminationQuestionService examinationQuestionService;

    private final static Gson GSON = new Gson();

    /**
     * 创建套题题目
     *
     * @param examinationAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addExaminationQuestion(@RequestBody ExaminationQuestionAddRequest examinationAddRequest, HttpServletRequest request) {
        if (examinationAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if(examinationAddRequest.getExaminationId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Examination examination = examinationService.getById(examinationAddRequest.getExaminationId());
        if(examination == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        ExaminationQuestion examinationQuestion = new ExaminationQuestion();
        BeanUtils.copyProperties(examinationAddRequest, examinationQuestion);
        List<String> tags = examinationAddRequest.getTags();
        if (tags != null) {
            examinationQuestion.setTags(GSON.toJson(tags));
        }
        JudgeConfig judgeConfig = examinationAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            examinationQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        List<JudgeCase> judgeCase = examinationAddRequest.getJudgeCase();
        if (judgeCase != null) {
            examinationQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }

        examinationQuestionService.validExaminationQuestion(examinationQuestion, true);
        User loginUser = userService.getLoginUser(request);

        examinationQuestion.setExaminationId(examinationAddRequest.getExaminationId());
        examinationQuestion.setUserId(loginUser.getId());
        examinationQuestion.setThumbNum(0);
        examinationQuestion.setFavourNum(0);

        boolean result = examinationQuestionService.save(examinationQuestion);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newExaminationQuestionId = examinationQuestion.getId();
        return ResultUtils.success(newExaminationQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteExaminationQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        ExaminationQuestion examinationQuestion = examinationQuestionService.getById(id);
        ThrowUtils.throwIf(examinationQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!examinationQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = examinationQuestionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param examinationQuestionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateExaminationQuestion(@RequestBody ExaminationQuestionUpdateRequest examinationQuestionUpdateRequest) {
        if (examinationQuestionUpdateRequest == null || examinationQuestionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ExaminationQuestion examinationQuestion = new ExaminationQuestion();
        BeanUtils.copyProperties(examinationQuestionUpdateRequest, examinationQuestion);
        List<String> tags = examinationQuestionUpdateRequest.getTags();
        if (tags != null) {
            examinationQuestion.setTags(GSON.toJson(tags));
        }

        JudgeConfig judgeConfig = examinationQuestionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            examinationQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        List<JudgeCase> judgeCase = examinationQuestionUpdateRequest.getJudgeCase();
        if (judgeCase != null) {
            examinationQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }

        // 参数校验
        examinationQuestionService.validExaminationQuestion(examinationQuestion, false);
        long id = examinationQuestion.getId();
        // 判断是否存在
        ExaminationQuestion oldExaminationQuestion = examinationQuestionService.getById(id);
        ThrowUtils.throwIf(oldExaminationQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = examinationQuestionService.updateById(examinationQuestion);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（不脱敏原始输出）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<ExaminationQuestion> getExaminationQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ExaminationQuestion examinationQuestion = examinationQuestionService.getById(id);
        if (examinationQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或者非管理员，不能直接获取所有信息
        if (!examinationQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(examinationQuestion);
    }

    /**
     * 根据 id 获取（脱敏）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ExaminationQuestionVO> getExaminationQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ExaminationQuestion examinationQuestion = examinationQuestionService.getById(id);
        if (examinationQuestion == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(examinationQuestionService.getExaminationQuestionVO(examinationQuestion, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param examinationQuestionQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ExaminationQuestion>> listExaminationQuestionByPage(@RequestBody ExaminationQuestionQueryRequest examinationQuestionQueryRequest, HttpServletRequest request) {
        long current = examinationQuestionQueryRequest.getCurrent();
        long size = examinationQuestionQueryRequest.getPageSize();
        Page<ExaminationQuestion> examinationQuestionPage = examinationQuestionService.page(new Page<>(current, size),
                examinationQuestionService.getQueryWrapper(examinationQuestionQueryRequest));
        return ResultUtils.success(examinationQuestionPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param examinationQuestionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ExaminationQuestionVO>> listExaminationQuestionVOByPage(@RequestBody ExaminationQuestionQueryRequest examinationQuestionQueryRequest,
                                                                                     HttpServletRequest request) {
        long current = examinationQuestionQueryRequest.getCurrent();
        long size = examinationQuestionQueryRequest.getPageSize();
        Page<ExaminationQuestion> examinationQuestionPage = examinationQuestionService.page(new Page<>(current, size),
                examinationQuestionService.getQueryWrapper(examinationQuestionQueryRequest));
        return ResultUtils.success(examinationQuestionService.getExaminationQuestionVOPage(examinationQuestionPage, request));
    }

    /**
     * 编辑（管理员）
     *
     * @param examinationQuestionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editExaminationQuestion(@RequestBody ExaminationQuestionEditRequest examinationQuestionEditRequest, HttpServletRequest request) {
        if (examinationQuestionEditRequest == null || examinationQuestionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        ExaminationQuestion examinationQuestion = new ExaminationQuestion();
        BeanUtils.copyProperties(examinationQuestionEditRequest, examinationQuestion);
        List<String> tags = examinationQuestionEditRequest.getTags();
        if (tags != null) {
            examinationQuestion.setTags(GSON.toJson(tags));
        }

        JudgeConfig judgeConfig = examinationQuestionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            examinationQuestion.setJudgeConfig(GSON.toJson(judgeConfig));
        }

        List<JudgeCase> judgeCase = examinationQuestionEditRequest.getJudgeCase();
        if (judgeCase != null) {
            examinationQuestion.setJudgeCase(GSON.toJson(judgeCase));
        }

        // 参数校验
        examinationQuestionService.validExaminationQuestion(examinationQuestion, false);
        User loginUser = userService.getLoginUser(request);
        long id = examinationQuestionEditRequest.getId();
        // 判断是否存在
        ExaminationQuestion oldExaminationQuestion = examinationQuestionService.getById(id);
        ThrowUtils.throwIf(oldExaminationQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldExaminationQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = examinationQuestionService.updateById(examinationQuestion);
        return ResultUtils.success(result);
    }

    @PostMapping("/list/vo")
    public BaseResponse<List<ExaminationQuestionVO>> listExaminationQuestionVOByExamination(@RequestBody ExaminationQuestionListQueryRequest examinationQuestionListQueryRequest, HttpServletRequest request) {
        if (examinationQuestionListQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long examinationId = examinationQuestionListQueryRequest.getExaminationId();

        QueryWrapper<ExaminationQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ObjectUtils.isNotEmpty(examinationId), "examinationId", examinationId);

        List<ExaminationQuestion> examinationQuestionList = examinationQuestionService.list(queryWrapper);
        return ResultUtils.success(examinationQuestionService.getExaminationQuestionVOList(examinationQuestionList, request));
    }
}
