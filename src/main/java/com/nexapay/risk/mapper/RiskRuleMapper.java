package com.nexapay.risk.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.risk.entity.RiskRule;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 风控规则Mapper
 */
@Mapper
@Repository
public interface RiskRuleMapper extends BaseMapper<RiskRule> {
}
