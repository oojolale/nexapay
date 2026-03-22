package com.nexapay.crm.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.crm.entity.Contact;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 联系人Mapper
 */
@Mapper
@Repository
public interface ContactMapper extends BaseMapper<Contact> {
}
