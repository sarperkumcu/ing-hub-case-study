package com.brokerage.controller;

import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.User;
import com.brokerage.models.response.GetAssetsResponse;
import com.brokerage.models.response.GetOrdersResponse;
import com.brokerage.service.interfaces.AssetService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    @GetMapping("")
    public ResponseEntity<List<GetAssetsResponse>> getAssets(
            @RequestParam(required = false) String assetName,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        List<Asset> assets = assetService.getAssets(user.getId(), assetName, pageable).getContent();
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

