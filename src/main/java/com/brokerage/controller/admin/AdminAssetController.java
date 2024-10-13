package com.brokerage.controller.admin;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.response.GetAssetsResponse;
import com.brokerage.service.interfaces.AssetService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/assets")
public class AdminAssetController {

    private final AssetService assetService;

    public AdminAssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("/admin")
    public ResponseEntity<List<GetAssetsResponse>> getAssetsAdmin(
            @RequestParam UUID customerId,
            @RequestParam(required = false) String assetName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<Asset> assets = assetService.getAssets(customerId, assetName, pageable).getContent();
        List<GetAssetsResponse> assetsResponses = assets.stream()
                .map(asset -> new GetAssetsResponse(
                        asset.getId(),
                        asset.getAssetName(),
                        asset.getSize(),
                        asset.getUsableSize()
                ))
                .toList();
        return ResponseEntity.ok(assetsResponses);
    }
}
