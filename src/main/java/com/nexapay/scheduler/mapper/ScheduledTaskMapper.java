package com.nexapay.scheduler.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nexapay.scheduler.entity.ScheduledTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 调度任务Mapper
 */
@Mapper
@Repository
public interface ScheduledTaskMapper extends BaseMapper<ScheduledTask> {
}
