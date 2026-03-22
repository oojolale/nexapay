package com.nexapay.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.system.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 用户Mapper
 */
@Mapper
@Repository
public interface UserMapper extends BaseMapper<User> {
}
