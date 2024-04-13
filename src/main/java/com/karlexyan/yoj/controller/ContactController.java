package com.karlexyan.yoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.karlexyan.yoj.annotation.AuthCheck;
import com.karlexyan.yoj.common.BaseResponse;
import com.karlexyan.yoj.common.DeleteRequest;
import com.karlexyan.yoj.common.ErrorCode;
import com.karlexyan.yoj.common.ResultUtils;
import com.karlexyan.yoj.constant.UserConstant;
import com.karlexyan.yoj.exception.BusinessException;
import com.karlexyan.yoj.exception.ThrowUtils;
import com.karlexyan.yoj.model.dto.contact.ContactAddRequest;
import com.karlexyan.yoj.model.dto.contact.ContactQueryRequest;
import com.karlexyan.yoj.model.entity.Contact;
import com.karlexyan.yoj.model.entity.User;
import com.karlexyan.yoj.model.vo.ContactVO;
import com.karlexyan.yoj.service.ContactService;
import com.karlexyan.yoj.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 讨论接口
 */
@RestController
@RequestMapping("/contact")
@Slf4j
public class ContactController {

    @Resource
    private ContactService contactService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建讨论
     *
     * @param contactAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addContact(@RequestBody ContactAddRequest contactAddRequest, HttpServletRequest request) {
        if (contactAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactAddRequest, contact);
        // 写入登录用户
        User loginUser = userService.getLoginUser(request);
        contact.setUserId(loginUser.getId());
        contact.setFavourNum(0);
        contact.setThumbNum(0);
        boolean result = contactService.save(contact);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        Long contactId = contact.getId();
        return ResultUtils.success(contactId);
    }

    /**
     * 删除讨论
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteContact(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        Contact contact = contactService.getById(id);
        ThrowUtils.throwIf(contact == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!contact.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = contactService.removeById(id);
        return ResultUtils.success(b);
    }


    /**
     * 根据 id 获取讨论（仅管理员）
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Contact> getContactById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Contact contact = contactService.getById(id);
        ThrowUtils.throwIf(contact == null, ErrorCode.NOT_FOUND_ERROR);
        User loginUser = userService.getLoginUser(request);
        // 不是本人或者非管理员，不能直接获取所有信息
        if (!contact.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return ResultUtils.success(contact);
    }

    /**
     * 根据 id 获取包装类
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<ContactVO> getContactVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Contact contact = contactService.getById(id);
        if (contact == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(ContactVO.objToVo(contact));
    }

    /**
     * 分页获取讨论列表（仅管理员）
     *
     * @param contactQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Contact>> listContactByPage(@RequestBody ContactQueryRequest contactQueryRequest,
                                                         HttpServletRequest request) {
        long current = contactQueryRequest.getCurrent();
        long size = contactQueryRequest.getPageSize();
        Page<Contact> contactPage = contactService.page(new Page<>(current, size),
                contactService.getQueryWrapper(contactQueryRequest));
        return ResultUtils.success(contactPage);
    }

    /**
     * 分页获取讨论封装列表
     *
     * @param contactQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ContactVO>> listContactVOByPage(@RequestBody ContactQueryRequest contactQueryRequest,
                                                             HttpServletRequest request) {
        if (contactQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = contactQueryRequest.getCurrent();
        long size = contactQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Contact> contactPage = contactService.page(new Page<>(current, size),
                contactService.getQueryWrapper(contactQueryRequest));
        return ResultUtils.success(contactService.getContactVOPage(contactPage, request));
    }

    // endregion

    @PostMapping("/star")
    public BaseResponse<Boolean> addStar(@RequestBody ContactQueryRequest contactQueryRequest, HttpServletRequest request) {
        if (contactQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = contactQueryRequest.getId();
        Contact contact = contactService.getById(id);
        Integer favourNum = contact.getFavourNum();
        if (favourNum < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Contact updateContact = new Contact();
        BeanUtils.copyProperties(contact, updateContact);
        updateContact.setFavourNum(favourNum + 1);
        boolean result = contactService.updateById(updateContact);
        return ResultUtils.success(result);
    }

    @PostMapping("/cancelStar")
    public BaseResponse<Boolean> cancelStar(@RequestBody ContactQueryRequest contactQueryRequest, HttpServletRequest request) {
        if (contactQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = contactQueryRequest.getId();
        Contact contact = contactService.getById(id);
        Integer favourNum = contact.getFavourNum();
        if (favourNum == 0 || favourNum < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Contact updateContact = new Contact();
        BeanUtils.copyProperties(contact, updateContact);
        updateContact.setFavourNum(favourNum - 1);
        boolean result = contactService.updateById(updateContact);
        return ResultUtils.success(result);
    }

    @PostMapping("/thumb")
    public BaseResponse<Boolean> addThumb(@RequestBody ContactQueryRequest contactQueryRequest, HttpServletRequest request) {
        if (contactQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = contactQueryRequest.getId();
        Contact contact = contactService.getById(id);
        Integer thumbNum = contact.getThumbNum();
        if (thumbNum < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Contact updateContact = new Contact();
        BeanUtils.copyProperties(contact, updateContact);
        updateContact.setFavourNum(thumbNum + 1);
        boolean result = contactService.updateById(updateContact);
        return ResultUtils.success(result);
    }

    @PostMapping("/cancelThumb")
    public BaseResponse<Boolean> cancelThumb(@RequestBody ContactQueryRequest contactQueryRequest, HttpServletRequest request) {
        if (contactQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = contactQueryRequest.getId();
        Contact contact = contactService.getById(id);
        Integer thumbNum = contact.getThumbNum();
        if (thumbNum == 0 || thumbNum < 0) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        Contact updateContact = new Contact();
        BeanUtils.copyProperties(contact, updateContact);
        updateContact.setFavourNum(thumbNum - 1);
        boolean result = contactService.updateById(updateContact);
        return ResultUtils.success(result);
    }

}
