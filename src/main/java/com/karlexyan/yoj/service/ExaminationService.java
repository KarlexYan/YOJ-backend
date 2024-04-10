package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.examination.ExaminationQueryRequest;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.vo.ExaminationVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author KarlexYan
* @description 针对表【examination(套题)】的数据库操作Service
* @createDate 2024-04-09 19:50:45
*/
public interface ExaminationService extends IService<Examination> {

    /**
     * 校验
     *
     * @param examination
     * @param add
     */
    void validExamination(Examination examination, boolean add);

    /**
     * 获取查询条件
     *
     * @param examinationQueryRequest
     * @return
     */
    QueryWrapper<Examination> getQueryWrapper(ExaminationQueryRequest examinationQueryRequest);


    /**
     * 获取题目封装
     *
     * @param examination
     * @param request
     * @return
     */
    ExaminationVO getExaminationVO(Examination examination, HttpServletRequest request);

    /**
     * 分页获取题目封装
     *
     * @param examinationPage
     * @param request
     * @return
     */
    Page<ExaminationVO> getExaminationVOPage(Page<Examination> examinationPage, HttpServletRequest request);
}
