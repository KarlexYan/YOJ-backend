package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.examinationquestion.ExaminationQuestionQueryRequest;
import com.karlexyan.yoj.model.entity.ExaminationQuestion;
import com.karlexyan.yoj.model.vo.ExaminationQuestionVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author KarlexYan
* @description 针对表【examination_question(套题题目表)】的数据库操作Service
* @createDate 2024-04-14 00:48:38
*/
public interface ExaminationQuestionService extends IService<ExaminationQuestion> {

    /**
     * 校验
     *
     * @param examinationQuestion
     * @param add
     */
    void validExaminationQuestion(ExaminationQuestion examinationQuestion, boolean add);

    /**
     * 获取套题题目封装
     *
     * @param examinationQuestion
     * @param request
     * @return
     */
    ExaminationQuestionVO getExaminationQuestionVO(ExaminationQuestion examinationQuestion, HttpServletRequest request);

    /**
     * 获取查询条件
     *
     * @param examinationQuestionQueryRequest
     * @return
     */
    QueryWrapper<ExaminationQuestion> getQueryWrapper(ExaminationQuestionQueryRequest examinationQuestionQueryRequest);

    /**
     * 分页获取套题题目封装
     *
     * @param examinationQuestionPage
     * @param request
     * @return
     */
    Page<ExaminationQuestionVO> getExaminationQuestionVOPage(Page<ExaminationQuestion> examinationQuestionPage, HttpServletRequest request);


    /**
     * 套题答题页面获取题目列表
     * @param examinationQuestionList
     * @param request
     * @return
     */
    List<ExaminationQuestionVO> getExaminationQuestionVOList(List<ExaminationQuestion> examinationQuestionList, HttpServletRequest request);
}
