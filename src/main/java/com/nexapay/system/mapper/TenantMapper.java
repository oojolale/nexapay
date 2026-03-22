package com.nexapay.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.system.entity.Tenant;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 租户Mapper
 */
@Mapper
@Repository
public interface TenantMapper extends BaseMapper<Tenant> {
}
