package com.karlexyan.yoj.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.judge.JudgeService;
import com.karlexyan.yoj.mapper.ExaminationSubmitMapper;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitAddRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import com.karlexyan.yoj.model.entity.QuestionSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.enums.ExaminationSubmitStatusEnum;
import com.karlexyan.yoj.model.enums.QuestionSubmitLanguageEnum;
import com.karlexyan.yoj.model.enums.QuestionSubmitStatusEnum;
import com.karlexyan.yoj.model.vo.ExaminationSubmitVO;
import com.karlexyan.yoj.service.*;
import com.karlexyan.yoj.utils.SqlUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author KarlexYan
* @description 针对表【examination_submit(套题提交记录)】的数据库操作Service实现
* @createDate 2024-04-09 19:51:02
*/
@Service
public class ExaminationSubmitServiceImpl extends ServiceImpl<ExaminationSubmitMapper, ExaminationSubmit>
    implements ExaminationSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private ExaminationService examinationService;



    /**
     * 提交
     * @param examinationSubmitAddRequest  套题提交信息
     * @param loginUser
     * @return
     */
    @Override
    public long doExaminationSubmit(ExaminationSubmitAddRequest examinationSubmitAddRequest, User loginUser) {

        Long examinationId = examinationSubmitAddRequest.getExaminationId();
        String submitLanguage = examinationSubmitAddRequest.getSubmitLanguage();
        List<QuestionSubmit> questionSubmitList = examinationSubmitAddRequest.getQuestionSubmitList();

        // 校验编程语言是否合法
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(submitLanguage);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        // 判断套题提交实体是否存在，根据id获取实体
        Examination examination = examinationService.getById(examinationId);
        if (examination == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        // 设置套题提交数
        Examination updateExamination = examinationService.getById(examinationId);
        synchronized (examination.getSubmitNum()) {
            int submitNum = examination.getSubmitNum() + 1;
            updateExamination.setSubmitNum(submitNum);
            boolean save = examinationService.updateById(updateExamination);
            if (!save) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "数据保存失败");
            }
        }

        // 是否已提交题目
        long userId = loginUser.getId();

        // 遍历提交信息，创建题目提交记录
        for (QuestionSubmit questionSubmitItem : questionSubmitList){
            // 每个用户串行提交
            QuestionSubmit questionSubmit = new QuestionSubmit();
            questionSubmit.setUserId(userId);
            questionSubmit.setQuestionId(questionSubmitItem.getQuestionId());
            questionSubmit.setSubmitCode(questionSubmitItem.getSubmitCode());
            questionSubmit.setSubmitLanguage(submitLanguage);
            // 设置初始状态
            questionSubmit.setSubmitState(QuestionSubmitStatusEnum.WAITING.getValue());
            questionSubmit.setJudgeInfo("{}");
            boolean save = questionSubmitService.save(questionSubmit);
            ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "数据保存失败");
            Long questionSubmitId = questionSubmit.getId();
            // 异步执行判题服务
            CompletableFuture.runAsync(() -> {
                judgeService.doJudge(questionSubmitId);
            });
        }

        // 创建套题提交信息
        ExaminationSubmit examinationSubmit = new ExaminationSubmit();
        examinationSubmit.setExaminationId(examinationId);
        examinationSubmit.setUserId(userId);
        examinationSubmit.setSubmitLanguage(submitLanguage);
        examinationSubmit.setScore(0); // 初始0分
        examinationSubmit.setSubmitState(ExaminationSubmitStatusEnum.WAITING.getValue());
        boolean save = this.save(examinationSubmit);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "数据保存失败");
        Long examinationSubmitId = examinationSubmit.getId();
        return examinationSubmitId;
    }

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象）
     *
     * @param examinationSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ExaminationSubmit> getQueryWrapper(ExaminationSubmitQueryRequest examinationSubmitQueryRequest) {
        Long examinationId = examinationSubmitQueryRequest.getExaminationId();
        String title = examinationSubmitQueryRequest.getTitle();
        String submitLanguage = examinationSubmitQueryRequest.getSubmitLanguage();
        Integer submitState = examinationSubmitQueryRequest.getSubmitState();
        Long userId = examinationSubmitQueryRequest.getUserId();
        String sortField = examinationSubmitQueryRequest.getSortField();
        String sortOrder = examinationSubmitQueryRequest.getSortOrder();

        QueryWrapper<ExaminationSubmit> queryWrapper = new QueryWrapper<>();
        if (examinationSubmitQueryRequest == null) {
            return queryWrapper;
        }

        // 拼接查询条件
        queryWrapper.eq(ObjectUtil.isNotEmpty(submitLanguage), "submitLanguage", submitLanguage);
        queryWrapper.eq(ObjectUtil.isNotEmpty(title), "title", title);
        queryWrapper.eq(ObjectUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtil.isNotEmpty(examinationId), "examinationId", examinationId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(submitState) != null, "submitState", submitState);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    /**
     * 获取查询封装类（单个）
     *
     * @param examinationSubmit
     * @param loginUser
     * @return
     */
    @Override
    public ExaminationSubmitVO getExaminationSubmitVO(ExaminationSubmit examinationSubmit, User loginUser) {
        ExaminationSubmitVO examinationSubmitVO = ExaminationSubmitVO.objToVo(examinationSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (userId != examinationSubmit.getUserId() && !userService.isAdmin(loginUser)) {
        }
        return examinationSubmitVO;
    }

    /**
     * 获取查询脱敏信息
     *
     * @param examinationSubmitPage
     * @param loginUser
     * @return
     */
    @Override
    public Page<ExaminationSubmitVO> getExaminationSubmitVOPage(Page<ExaminationSubmit> examinationSubmitPage, User loginUser) {
        // 获取原始查询结果
        List<ExaminationSubmit> examinationSubmitList = examinationSubmitPage.getRecords();
        Page<ExaminationSubmitVO> examinationSubmitVOPage = new Page<>(examinationSubmitPage.getCurrent(), examinationSubmitPage.getSize(), examinationSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(examinationSubmitList)) {
            return examinationSubmitVOPage;
        }
        // 遍历脱敏
        List<ExaminationSubmitVO> examinationSubmitVOList = examinationSubmitList.stream().map(examinationSubmit -> getExaminationSubmitVO(examinationSubmit, loginUser)).collect(Collectors.toList());
        examinationSubmitVOPage.setRecords(examinationSubmitVOList);
        return examinationSubmitVOPage;
    }
}




