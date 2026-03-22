package com.nexapay.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.payment.entity.Transaction;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 交易Mapper
 */
@Mapper
@Repository
public interface TransactionMapper extends BaseMapper<Transaction> {
}
