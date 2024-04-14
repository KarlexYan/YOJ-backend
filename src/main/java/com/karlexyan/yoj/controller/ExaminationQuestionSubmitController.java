package com.karlexyan.yoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karlexyan.yoj.common.BaseResponse;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.common.ResultUtils;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.model.dto.examinationquestionsubmit.ExaminationQuestionSubmitQueryRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitAddRequest;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationQuestionSubmitVO;
import com.karlexyan.yoj.service.ExaminationQuestionSubmitService;
import com.karlexyan.yoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 套题题目提交接口
 */
@RestController
@RequestMapping("/examination_question_submit")
@Slf4j
public class ExaminationQuestionSubmitController {

    @Resource
    private UserService userService;

    @Resource
    private ExaminationQuestionSubmitService examinationQuestionSubmitService;

    /**
     * 提交套题题目
     *
     * @param examinationSubmitAddRequest
     * @param request
     * @return 提交记录的 id
     */
    @PostMapping("/do")
    public BaseResponse<Long> doExaminationQuestionSubmit(@RequestBody ExaminationSubmitAddRequest examinationSubmitAddRequest,
                                               HttpServletRequest request) {
        if (examinationSubmitAddRequest == null || examinationSubmitAddRequest.getExaminationId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能做题
        final User loginUser = userService.getLoginUser(request);
        long examinationSubmitId = examinationQuestionSubmitService.doExaminationSubmit(examinationSubmitAddRequest, loginUser);
        return ResultUtils.success(examinationSubmitId);
    }

    /**
     * 分页获取套题题目提交列表（管理员）
     *
     * @param examinationQuestionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<ExaminationQuestionSubmitVO>> listExaminationQuestionSubmitByPage(@RequestBody ExaminationQuestionSubmitQueryRequest examinationQuestionSubmitQueryRequest,
                                                                                    HttpServletRequest request) {
        long current = examinationQuestionSubmitQueryRequest.getCurrent();
        long size = examinationQuestionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<ExaminationQuestionSubmit> examinationQuestionSubmitPage = examinationQuestionSubmitService.page(new Page<>(current, size),
                examinationQuestionSubmitService.getQueryWrapper(examinationQuestionSubmitQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        // 返回脱敏信息
        return ResultUtils.success(examinationQuestionSubmitService.getExaminationQuestionSubmitVOPage(examinationQuestionSubmitPage, loginUser));
    }
}
