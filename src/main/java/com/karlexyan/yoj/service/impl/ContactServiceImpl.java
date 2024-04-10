package com.karlexyan.yoj.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.karlexyan.yoj.mapper.ContactMapper;
import com.karlexyan.yoj.model.entity.Contact;
import com.karlexyan.yoj.service.ContactService;
import org.springframework.stereotype.Service;

/**
* @author KarlexYan
* @description 针对表【contact(讨论)】的数据库操作Service实现
* @createDate 2024-04-09 19:50:00
*/
@Service
public class ContactServiceImpl extends ServiceImpl<ContactMapper, Contact>
    implements ContactService {

}




