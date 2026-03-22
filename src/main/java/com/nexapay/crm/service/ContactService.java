package com.nexapay.crm.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.crm.entity.Contact;
import com.nexapay.crm.mapper.ContactMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CRM联系人服务
 */
@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactMapper contactMapper;

    /**
     * 获取联系人列表
     */
    public Page<Contact> getContacts(Long tenantId, int page, int size, String stage) {
        Page<Contact> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getTenantId, tenantId);
        if (stage != null && !stage.isEmpty()) {
            wrapper.eq(Contact::getPipelineStage, stage);
        }
        wrapper.orderByDesc(Contact::getCreatedAt);
        return contactMapper.selectPage(pageParam, wrapper);
    }

    /**
     * 创建联系人
     */
    public Contact createContact(Contact contact) {
        contact.setCreatedAt(LocalDateTime.now());
        contact.setUpdatedAt(LocalDateTime.now());
        contactMapper.insert(contact);
        return contact;
    }

    /**
     * 更新联系人阶段
     */
    public boolean updateStage(Long tenantId, Long contactId, String stage) {
        Contact contact = contactMapper.selectById(contactId);
        if (contact != null && contact.getTenantId().equals(tenantId)) {
            contact.setPipelineStage(stage);
            contact.setUpdatedAt(LocalDateTime.now());
            return contactMapper.updateById(contact) > 0;
        }
        return false;
    }

    /**
     * 获取管道统计
     */
    public Map<String, Object> getPipelineStats(Long tenantId) {
        LambdaQueryWrapper<Contact> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Contact::getTenantId, tenantId);

        List<Contact> contacts = contactMapper.selectList(wrapper);

        long total = contacts.size();
        BigDecimal totalValue = contacts.stream()
            .filter(c -> c.getLeadValue() != null)
            .map(Contact::getLeadValue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, Long> stageCount = contacts.stream()
            .collect(Collectors.groupingBy(Contact::getPipelineStage, Collectors.counting()));

        return Map.of(
            "total", total,
            "totalValue", totalValue,
            "stageCount", stageCount
        );
    }
}
