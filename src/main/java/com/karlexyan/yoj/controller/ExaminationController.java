package com.karlexyan.yoj.controller;

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
import com.karlexyan.yoj.model.dto.examination.ExaminationAddRequest;
import com.karlexyan.yoj.model.dto.examination.ExaminationEditRequest;
import com.karlexyan.yoj.model.dto.examination.ExaminationQueryRequest;
import com.karlexyan.yoj.model.dto.examination.ExaminationUpdateRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationSubmitVO;
import com.karlexyan.yoj.model.vo.ExaminationVO;
import com.karlexyan.yoj.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 套题接口
 */
@RestController
@RequestMapping("/examination")
@Slf4j
public class ExaminationController {

    @Resource
    private ExaminationService examinationService;

    @Resource
    private UserService userService;

    @Resource
    private ExaminationSubmitService examinationSubmitService;

    @Resource
    private ExaminationQuestionService examinationQuestionService;

    @Resource
    private QuestionService questionService;

    private final static Gson GSON = new Gson();


    /**
     * 创建
     *
     * @param examinationAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addExamination(@RequestBody ExaminationAddRequest examinationAddRequest, HttpServletRequest request) {
        if (examinationAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Examination examination = new Examination();
        BeanUtils.copyProperties(examinationAddRequest, examination);
        List<String> tags = examinationAddRequest.getTags();
        if (tags != null) {
            examination.setTags(GSON.toJson(tags));
        }

        examinationService.validExamination(examination, true);
        User loginUser = userService.getLoginUser(request);
        examination.setUserId(loginUser.getId());
        examination.setFavourNum(0);
        examination.setThumbNum(0);
        boolean result = examinationService.save(examination);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newExaminationId = examination.getId();
        return ResultUtils.success(newExaminationId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteExamination(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Examination oldExamination = examinationService.getById(id);
        ThrowUtils.throwIf(oldExamination == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldExamination.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = examinationService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param examinationUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateExamination(@RequestBody ExaminationUpdateRequest examinationUpdateRequest) {
        if (examinationUpdateRequest == null || examinationUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Examination examination = new Examination();
        BeanUtils.copyProperties(examinationUpdateRequest, examination);
        List<String> tags = examinationUpdateRequest.getTags();
        if (tags != null) {
            examination.setTags(GSON.toJson(tags));
        }

        // 参数校验
        examinationService.validExamination(examination, false);
        long id = examinationUpdateRequest.getId();
        // 判断是否存在
        Examination oldExamination = examinationService.getById(id);
        ThrowUtils.throwIf(oldExamination == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = examinationService.updateById(examination);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取（不脱敏原始输出）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Examination> getExaminationById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Examination examination = examinationService.getById(id);
        if (examination == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        // 不是本人或者非管理员，不能直接获取所有信息
        if (!examination.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(examination);
    }

    /**
     * 根据 id 获取脱敏
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ExaminationVO> getExaminationVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Examination examination = examinationService.getById(id);
        if (examination == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(examinationService.getExaminationVO(examination, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param examinationQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Examination>> listExaminationByPage(@RequestBody ExaminationQueryRequest examinationQueryRequest, HttpServletRequest request) {
        long current = examinationQueryRequest.getCurrent();
        long size = examinationQueryRequest.getPageSize();
        Page<Examination> examinationPage = examinationService.page(new Page<>(current, size),
                examinationService.getQueryWrapper(examinationQueryRequest));
        return ResultUtils.success(examinationPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param examinationQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ExaminationVO>> listExaminationVOByPage(@RequestBody ExaminationQueryRequest examinationQueryRequest,
                                                               HttpServletRequest request) {
        long current = examinationQueryRequest.getCurrent();
        long size = examinationQueryRequest.getPageSize();
        Page<Examination> examinationPage = examinationService.page(new Page<>(current, size),
                examinationService.getQueryWrapper(examinationQueryRequest));
        return ResultUtils.success(examinationService.getExaminationVOPage(examinationPage, request));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param examinationQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<ExaminationVO>> listMyExaminationVOByPage(@RequestBody ExaminationQueryRequest examinationQueryRequest,
                                                                 HttpServletRequest request) {
        if (examinationQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        examinationQueryRequest.setUserId(loginUser.getId());
        long current = examinationQueryRequest.getCurrent();
        long size = examinationQueryRequest.getPageSize();
        Page<Examination> examinationPage = examinationService.page(new Page<>(current, size),
                examinationService.getQueryWrapper(examinationQueryRequest));
        return ResultUtils.success(examinationService.getExaminationVOPage(examinationPage, request));
    }

    // endregion

    /**
     * 编辑（用户）
     *
     * @param examinationEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editExamination(@RequestBody ExaminationEditRequest examinationEditRequest, HttpServletRequest request) {
        if (examinationEditRequest == null || examinationEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Examination examination = new Examination();
        BeanUtils.copyProperties(examinationEditRequest, examination);
        List<String> tags = examinationEditRequest.getTags();
        if (tags != null) {
            examination.setTags(GSON.toJson(tags));
        }


        // 参数校验
        examinationService.validExamination(examination, false);
        User loginUser = userService.getLoginUser(request);
        long id = examinationEditRequest.getId();
        // 判断是否存在
        Examination oldExamination = examinationService.getById(id);
        ThrowUtils.throwIf(oldExamination == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldExamination.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = examinationService.updateById(examination);
        return ResultUtils.success(result);
    }



    /**
     * 分页获取套题提交列表（除了管理员外，普通用户只能看到非答案、提交代码等公开信息）
     *
     * @param examinationSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/examination_submit/list/page")
    public BaseResponse<Page<ExaminationSubmitVO>> listExaminationSubmitByPage(@RequestBody ExaminationSubmitQueryRequest examinationSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = examinationSubmitQueryRequest.getCurrent();
        long size = examinationSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的套题提交分页信息
        Page<ExaminationSubmit> examinationSubmitPage = examinationSubmitService.page(new Page<>(current, size),
                examinationSubmitService.getQueryWrapper(examinationSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(examinationSubmitService.getExaminationSubmitVOPage(examinationSubmitPage, loginUser));
    }

}
