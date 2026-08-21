package com.apimgt.apiordermgt.controller;

import com.apimgt.apiordermgt.common.dto.BatchIdRequest;
import com.apimgt.apiordermgt.common.dto.Result;
import com.apimgt.apiordermgt.common.dto.ProviderSaveRequest;
import com.apimgt.apiordermgt.service.ProviderService;
import com.apimgt.apiordermgt.common.vo.ProviderVO;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/v1/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @GetMapping
    public Result<List<ProviderVO>> list() {
        return Result.success(providerService.list());
    }

    @GetMapping("/options")
    public Result<List<ProviderVO>> options() {
        return Result.success(providerService.list());
    }

    @PostMapping
    public Result<ProviderVO> create(
            @Valid @RequestBody ProviderSaveRequest request) {
        return Result.success("提供商创建成功", providerService.create(request));
    }

    @GetMapping("/{id}")
    public Result<ProviderVO> detail(@PathVariable Long id) {
        return Result.success(providerService.detail(id));
    }

    @PutMapping("/{id}")
    public Result<ProviderVO> update(
            @PathVariable Long id,
            @Valid @RequestBody ProviderSaveRequest request) {
        return Result.success("提供商修改成功", providerService.update(id, request));
    }

    @PostMapping("/batch-delete")
    public Result<Void> batchDelete(@Valid @RequestBody BatchIdRequest request) {
        providerService.batchDelete(request);
        return Result.success("提供商删除成功", null);
    }

}
