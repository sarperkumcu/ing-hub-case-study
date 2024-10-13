package com.brokerage.controller.admin;

import com.brokerage.models.entity.Asset;
import com.brokerage.service.interfaces.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin/assets")
public class AdminAssetController {

    private final AssetService assetService;

    public AdminAssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/admin")
    public ResponseEntity<Page<Asset>> getAssetsAdmin(
            @RequestParam UUID customerId,
            @RequestParam(required = false) String assetName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Asset> assets = assetService.getAssets(customerId, assetName, pageable);

        return ResponseEntity.ok(assets);
    }
}
