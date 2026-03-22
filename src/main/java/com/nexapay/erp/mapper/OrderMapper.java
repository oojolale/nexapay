package com.nexapay.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.erp.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 订单Mapper
 */
@Mapper
@Repository
public interface OrderMapper extends BaseMapper<Order> {
}
