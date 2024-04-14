package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.examinationquestionsubmit.ExaminationQuestionSubmitQueryRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitAddRequest;
import com.karlexyan.yoj.model.entity.ExaminationQuestionSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationQuestionSubmitVO;

/**
* @author KarlexYan
* @description 针对表【examination_question_submit(题目提交)】的数据库操作Service
* @createDate 2024-04-14 15:44:39
*/
public interface ExaminationQuestionSubmitService extends IService<ExaminationQuestionSubmit> {

    /**
     * 套题提交
     *
     * @param examinationSubmitAddRequest  套题提交信息
     * @param loginUser
     * @return
     */
    long doExaminationSubmit(ExaminationSubmitAddRequest examinationSubmitAddRequest, User loginUser);


    /**
     * 获取查询条件
     *
     * @param examinationQuestionSubmitQueryRequest
     * @return
     */
    QueryWrapper<ExaminationQuestionSubmit> getQueryWrapper(ExaminationQuestionSubmitQueryRequest examinationQuestionSubmitQueryRequest);

    /**
     * 获取套题题目封装
     *
     * @param examinationQuestionSubmit
     * @param loginUser
     * @return
     */
    ExaminationQuestionSubmitVO getExaminationQuestionSubmitVO(ExaminationQuestionSubmit examinationQuestionSubmit, User loginUser);

    /**
     * 分页获取套题题目封装
     *
     * @param examinationQuestionSubmitPage
     * @param loginUser
     * @return
     */
    Page<ExaminationQuestionSubmitVO> getExaminationQuestionSubmitVOPage(Page<ExaminationQuestionSubmit> examinationQuestionSubmitPage, User loginUser);
}
