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
import com.karlexyan.yoj.mapper.ExaminationQuestionSubmitMapper;
import com.karlexyan.yoj.model.dto.examinationquestionsubmit.ExaminationQuestionSubmitQueryRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitAddRequest;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.enums.ExaminationSubmitLanguageEnum;
import com.karlexyan.yoj.model.enums.ExaminationSubmitStatusEnum;
import com.karlexyan.yoj.model.enums.QuestionSubmitStatusEnum;
import com.karlexyan.yoj.model.vo.ExaminationQuestionSubmitVO;
import com.karlexyan.yoj.service.ExaminationQuestionSubmitService;
import com.karlexyan.yoj.service.ExaminationService;
import com.karlexyan.yoj.service.ExaminationSubmitService;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
* @author KarlexYan
* @description 针对表【examination_question_submit(题目提交)】的数据库操作Service实现
* @createDate 2024-04-14 15:44:39
*/
@Service
public class ExaminationQuestionSubmitServiceImpl extends ServiceImpl<ExaminationQuestionSubmitMapper, ExaminationQuestionSubmit>
    implements ExaminationQuestionSubmitService {

    @Resource
    private UserService userService;

    @Resource
    @Lazy
    private JudgeService judgeService;

    @Resource
    private ExaminationSubmitService examinationSubmitService;

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
        List<ExaminationQuestionSubmit> examinationQuestionSubmitList = examinationSubmitAddRequest.getExaminationQuestionSubmitList();


        // 校验编程语言是否合法
        ExaminationSubmitLanguageEnum languageEnum = ExaminationSubmitLanguageEnum.getEnumByValue(submitLanguage);
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

        // 创建套题提交信息
        ExaminationSubmit examinationSubmit = new ExaminationSubmit();
        examinationSubmit.setExaminationId(examinationId);
        examinationSubmit.setUserId(userId);
        examinationSubmit.setSubmitLanguage(submitLanguage);
        examinationSubmit.setScore(0); // 初始0分
        examinationSubmit.setSubmitState(ExaminationSubmitStatusEnum.RUNNING.getValue());
        boolean save = examinationSubmitService.save(examinationSubmit);


        // 遍历提交信息，创建套题题目提交记录
        for (ExaminationQuestionSubmit examinationQuestionSubmitItem : examinationQuestionSubmitList){
            // 每个用户串行提交
            ExaminationQuestionSubmit examinationQuestionSubmit = new ExaminationQuestionSubmit();
            examinationQuestionSubmit.setExaminationQuestionId(examinationQuestionSubmitItem.getExaminationQuestionId());
            examinationQuestionSubmit.setUserId(userId);
            examinationQuestionSubmit.setSubmitLanguage(submitLanguage);
            examinationQuestionSubmit.setSubmitCode(examinationQuestionSubmitItem.getSubmitCode());

            // 设置初始状态
            examinationQuestionSubmit.setSubmitState(ExaminationSubmitStatusEnum.WAITING.getValue());
            examinationQuestionSubmit.setJudgeInfo("{}");
            boolean flag = this.save(examinationQuestionSubmit);
            ThrowUtils.throwIf(!flag, ErrorCode.SYSTEM_ERROR, "数据保存失败");
            Long examinationQuestionSubmitId = examinationQuestionSubmit.getId();
            // 异步执行判题服务
            CompletableFuture.runAsync(() -> {
                judgeService.doExaminationQuestionJudge(examinationQuestionSubmitId);
            });
        }

        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "数据保存失败");
        Long examinationSubmitId = examinationSubmit.getId();
        return examinationSubmitId;
    }

    @Override
    public QueryWrapper<ExaminationQuestionSubmit> getQueryWrapper(ExaminationQuestionSubmitQueryRequest examinationQuestionSubmitQueryRequest) {
        Long examinationQuestionId = examinationQuestionSubmitQueryRequest.getExaminationQuestionId();
        String submitLanguage = examinationQuestionSubmitQueryRequest.getSubmitLanguage();
        Integer submitState = examinationQuestionSubmitQueryRequest.getSubmitState();
        Long userId = examinationQuestionSubmitQueryRequest.getUserId();
        String sortField = examinationQuestionSubmitQueryRequest.getSortField();
        String sortOrder = examinationQuestionSubmitQueryRequest.getSortOrder();


        QueryWrapper<ExaminationQuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (examinationQuestionSubmitQueryRequest == null) {
            return queryWrapper;
        }

        // 拼接查询条件
        queryWrapper.like(ObjectUtil.isNotEmpty(examinationQuestionId), "examinationQuestionId", examinationQuestionId);
        queryWrapper.like(StringUtils.isNotEmpty(submitLanguage), "submitLanguage", submitLanguage);
        queryWrapper.like(ObjectUtil.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(submitState) != null, "submitState", submitState);
        queryWrapper.eq("isDelete", false);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);

        return queryWrapper;
    }

    /**
     * 获取查询封装类（单个）
     *
     * @param examinationQuestionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public ExaminationQuestionSubmitVO getExaminationQuestionSubmitVO(ExaminationQuestionSubmit examinationQuestionSubmit, User loginUser) {
        ExaminationQuestionSubmitVO examinationQuestionSubmitVO = ExaminationQuestionSubmitVO.objToVo(examinationQuestionSubmit);
        // 脱敏：仅本人和管理员能看见自己（提交 userId 和登录用户 id 不同）提交的代码
        long userId = loginUser.getId();
        // 处理脱敏
        if (!userService.isAdmin(loginUser)) {
            examinationQuestionSubmitVO.setSubmitCode(null);
        }
        return examinationQuestionSubmitVO;
    }

    @Override
    public Page<ExaminationQuestionSubmitVO> getExaminationQuestionSubmitVOPage(Page<ExaminationQuestionSubmit> examinationQuestionSubmitPage, User loginUser) {
        // 获取原始查询结果
        List<ExaminationQuestionSubmit> examinationQuestionSubmitList = examinationQuestionSubmitPage.getRecords();
        Page<ExaminationQuestionSubmitVO> examinationQuestionSubmitVOPage = new Page<>(examinationQuestionSubmitPage.getCurrent(), examinationQuestionSubmitPage.getSize(), examinationQuestionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(examinationQuestionSubmitList)) {
            return examinationQuestionSubmitVOPage;
        }
        // 遍历脱敏
        List<ExaminationQuestionSubmitVO> examinationQuestionSubmitVOList = examinationQuestionSubmitList.stream().map(examinationQuestionSubmit -> getExaminationQuestionSubmitVO(examinationQuestionSubmit, loginUser)).collect(Collectors.toList());
        examinationQuestionSubmitVOPage.setRecords(examinationQuestionSubmitVOList);
        return examinationQuestionSubmitVOPage;
    }

}




