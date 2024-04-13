package com.karlexyan.yoj.model.vo;

import com.karlexyan.yoj.model.entity.Contact;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;


@Data
public class ContactVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 用户 id
     */
    private Long userId;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户封装
     */
    private UserVO userVO;


    /**
     * 包装类转对象
     *
     * @param contactVO
     * @return
     */
    public static Contact voToObj(ContactVO contactVO) {
        if (contactVO == null) {
            return null;
        }
        Contact contact = new Contact();
        BeanUtils.copyProperties(contactVO, contact);
        return contact;
    }

    /**
     * 对象转包装类
     *
     * @param contact
     * @return
     */
    public static ContactVO objToVo(Contact contact) {
        if (contact == null) {
            return null;
        }
        ContactVO contactVO = new ContactVO();
        BeanUtils.copyProperties(contact, contactVO);
        return contactVO;
    }


    private static final long serialVersionUID = 1L;
}