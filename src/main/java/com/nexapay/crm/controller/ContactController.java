package com.nexapay.crm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nexapay.crm.entity.Contact;
import com.nexapay.crm.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CRM联系人控制器
 */
@RestController
@RequestMapping("/api/crm/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 获取联系人列表
     */
    @GetMapping
    public ResponseEntity<Page<Contact>> getContacts(
            @RequestParam Long tenantId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String stage) {
        return ResponseEntity.ok(contactService.getContacts(tenantId, page, size, stage));
    }

    /**
     * 创建联系人
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(
            @RequestParam Long tenantId,
            @RequestBody Contact contact) {
        contact.setTenantId(tenantId);
        return ResponseEntity.ok(contactService.createContact(contact));
    }

    /**
     * 更新联系人阶段
     */
    @PatchMapping("/{contactId}/stage")
    public ResponseEntity<Contact> updateStage(
            @RequestParam Long tenantId,
            @PathVariable Long contactId,
            @RequestBody Contact contactRequest) {
        boolean success = contactService.updateStage(tenantId, contactId, contactRequest.getPipelineStage());
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * 获取管道统计
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStats(@RequestParam Long tenantId) {
        return ResponseEntity.ok(contactService.getPipelineStats(tenantId));
    }
}
