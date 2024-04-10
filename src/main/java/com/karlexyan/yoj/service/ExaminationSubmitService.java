package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitAddRequest;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationSubmitVO;

/**
* @author KarlexYan
* @description 针对表【examination_submit(套题提交记录)】的数据库操作Service
* @createDate 2024-04-09 19:51:02
*/
public interface ExaminationSubmitService extends IService<ExaminationSubmit> {

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
     * @param examinationSubmitQueryRequest
     * @return
     */
    QueryWrapper<ExaminationSubmit> getQueryWrapper(ExaminationSubmitQueryRequest examinationSubmitQueryRequest);

    /**
     * 获取套题封装
     *
     * @param examinationSubmit
     * @param loginUser
     * @return
     */
    ExaminationSubmitVO getExaminationSubmitVO(ExaminationSubmit examinationSubmit, User loginUser);

    /**
     * 分页获取套题封装
     *
     * @param examinationSubmitPage
     * @param loginUser
     * @return
     */
    Page<ExaminationSubmitVO> getExaminationSubmitVOPage(Page<ExaminationSubmit> examinationSubmitPage, User loginUser);

}
