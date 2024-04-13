package com.karlexyan.yoj.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.constant.CommonConstant;
import com.karlexyan.yoj.mapper.ContactMapper;
import com.karlexyan.yoj.model.dto.contact.ContactQueryRequest;
import com.karlexyan.yoj.model.entity.Contact;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ContactVO;
import com.karlexyan.yoj.service.ContactService;
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
* @description 针对表【contact(讨论)】的数据库操作Service实现
* @createDate 2024-04-09 19:50:00
*/
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact>
    implements ContactService {

    @Resource
    private UserService userService;

    @Override
    public QueryWrapper<Contact> getQueryWrapper(ContactQueryRequest contactQueryRequest) {
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        if (contactQueryRequest == null) {
            return queryWrapper;
        }
        Long id = contactQueryRequest.getId();
        Long userId = contactQueryRequest.getUserId();
        String content = contactQueryRequest.getContent();
        String sortField = contactQueryRequest.getSortField();
        String sortOrder = contactQueryRequest.getSortOrder();
        // 拼接查询条件

        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        queryWrapper.like(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        queryWrapper.eq("isDelete",false);  // 将被删除的数据剔除
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<ContactVO> getContactVOPage(Page<Contact> contactPage, HttpServletRequest request) {
        List<Contact> contactList = contactPage.getRecords();
        Page<ContactVO> questionVOPage = new Page<>(contactPage.getCurrent(), contactPage.getSize(), contactPage.getTotal());
        if (CollUtil.isEmpty(contactList)) {
            return questionVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = contactList.stream().map(Contact::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<ContactVO> contactVOList = contactList.stream().map(contact -> {
            ContactVO contactVO = ContactVO.objToVo(contact);
            Long userId = contact.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            contactVO.setUserVO(userService.getUserVO(user));
            return contactVO;
        }).collect(Collectors.toList());
        questionVOPage.setRecords(contactVOList);
        return questionVOPage;
    }
}




