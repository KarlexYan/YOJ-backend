package com.karlexyan.yoj.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.mapper.ExaminationSubmitMapper;
import com.karlexyan.yoj.model.dto.examinationsubmit.ExaminationSubmitQueryRequest;
import com.karlexyan.yoj.model.entity.ExaminationSubmit;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.enums.QuestionSubmitStatusEnum;
import com.karlexyan.yoj.model.vo.ExaminationSubmitVO;
import com.karlexyan.yoj.service.ExaminationSubmitService;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
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
    private UserService userService;

    /**
     * 获取查询包装类（用户根据哪些字段查询，根据前端传来的请求对象）
     *
     * @param examinationSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ExaminationSubmit> getQueryWrapper(ExaminationSubmitQueryRequest examinationSubmitQueryRequest) {
        Long examinationId = examinationSubmitQueryRequest.getExaminationId();
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
        queryWrapper.like(ObjectUtil.isNotEmpty(userId), "userId", userId);
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




