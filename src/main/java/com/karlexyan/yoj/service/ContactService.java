package com.karlexyan.yoj.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.karlexyan.yoj.model.dto.contact.ContactQueryRequest;
import com.karlexyan.yoj.model.entity.Contact;
import com.karlexyan.yoj.model.vo.ContactVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author KarlexYan
* @description 针对表【contact(讨论)】的数据库操作Service
* @createDate 2024-04-09 19:50:00
*/
public interface ContactService extends IService<Contact> {

    /**
     * 获取查询条件
     *
     * @param contactQueryRequest
     * @return
     */
    QueryWrapper<Contact> getQueryWrapper(ContactQueryRequest contactQueryRequest);

    /**
     * 分页获取讨论封装
     *
     * @param contactPage
     * @param request
     * @return
     */
    Page<ContactVO> getContactVOPage(Page<Contact> contactPage, HttpServletRequest request);

}
