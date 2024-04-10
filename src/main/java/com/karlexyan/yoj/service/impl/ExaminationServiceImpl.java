package com.karlexyan.yoj.service.impl;


import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.mapper.ExaminationMapper;
import com.karlexyan.yoj.model.dto.examination.ExaminationQueryRequest;
import com.karlexyan.yoj.model.entity.Examination;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ExaminationVO;
import com.karlexyan.yoj.model.vo.UserVO;
import com.karlexyan.yoj.service.ExaminationService;
import com.karlexyan.yoj.service.UserService;
import com.karlexyan.yoj.utils.SqlUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author KarlexYan
* @description 针对表【examination(套题)】的数据库操作Service实现
* @createDate 2024-04-09 19:50:45
*/
@Service
public class ExaminationServiceImpl extends ServiceImpl<ExaminationMapper, Examination>
    implements ExaminationService {

    @Resource
    private UserService userService;

    @Override
    public void validExamination(Examination examination, boolean add) {
        if (examination == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String title = examination.getTitle();
        String content = examination.getContent();
        String tags = examination.getTags();

        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content, tags), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }

    }

    /**
     * 获取查询包装类（用户根据哪个字段查询，根据前端传来的请求对象，得到mybatis框架支持的查询QueryMapper类
     *
     */
    @Override
    public QueryWrapper<Examination> getQueryWrapper(ExaminationQueryRequest examinationQueryRequest) {
        QueryWrapper<Examination> queryWrapper = new QueryWrapper<>();
        if (examinationQueryRequest == null) {
            return queryWrapper;
        }
        Long id = examinationQueryRequest.getId();
        Long userId = examinationQueryRequest.getUserId();
        String title = examinationQueryRequest.getTitle();
        String content = examinationQueryRequest.getContent();
        List<String> tags = examinationQueryRequest.getTags();
        String sortField = examinationQueryRequest.getSortField();
        String sortOrder = examinationQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.like(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq("isDelete",false);  // 将被删除的数据剔除
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取脱敏对象
     * @param examination
     * @param request
     * @return
     */
    @Override
    public ExaminationVO getExaminationVO(Examination examination, HttpServletRequest request) {
        ExaminationVO examinationVO = ExaminationVO.objToVo(examination);
        // 1. 关联查询用户信息
        Long userId = examination.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        examinationVO.setUserVO(userVO);
        return examinationVO;
    }

    /**
     * 获取分页脱敏列表
     * @param examinationPage
     * @param request
     * @return
     */
    @Override
    public Page<ExaminationVO> getExaminationVOPage(Page<Examination> examinationPage, HttpServletRequest request) {
        List<Examination> examinationList = examinationPage.getRecords();
        Page<ExaminationVO> examinationVOPage = new Page<>(examinationPage.getCurrent(), examinationPage.getSize(), examinationPage.getTotal());
        if (CollUtil.isEmpty(examinationList)) {
            return examinationVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = examinationList.stream().map(Examination::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<ExaminationVO> examinationVOList = examinationList.stream().map(examination -> {
            ExaminationVO examinationVO = ExaminationVO.objToVo(examination);
            Long userId = examination.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            examinationVO.setUserVO(userService.getUserVO(user));
            return examinationVO;
        }).collect(Collectors.toList());
        examinationVOPage.setRecords(examinationVOList);
        return examinationVOPage;
    }
}




