package com.nexapay.erp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.erp.entity.Inventory;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 库存Mapper
 */
@Mapper
@Repository
public interface InventoryMapper extends BaseMapper<Inventory> {
}
