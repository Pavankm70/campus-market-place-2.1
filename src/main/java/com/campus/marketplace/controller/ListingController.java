package com.campus.marketplace.controller;

import com.campus.marketplace.dto.ListingRequestDto;
import com.campus.marketplace.dto.ListingResponseDto;
import com.campus.marketplace.entity.Category;
import com.campus.marketplace.entity.ListingStatus;
import com.campus.marketplace.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/listings")
@Tag(name = "Listings", description = "Endpoints for marketplace listings management and browsing")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    @Operation(summary = "Browse, search, and filter listings")
    public ResponseEntity<List<ListingResponseDto>> getListings(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Category category,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) ListingStatus status
    ) {
        List<ListingResponseDto> listings = listingService.searchAndFilterListings(search, category, sort, status);
        return ResponseEntity.ok(listings);
    }

    @GetMapping("/featured")
    @Operation(summary = "Get latest featured listings for home page")
    public ResponseEntity<List<ListingResponseDto>> getFeaturedListings() {
        return ResponseEntity.ok(listingService.getFeaturedListings());
    }

    @GetMapping("/my")
    @Operation(summary = "Get all listings created by the authenticated user")
    public ResponseEntity<List<ListingResponseDto>> getMyListings() {
        return ResponseEntity.ok(listingService.getMyListings());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get listing details by ID")
    public ResponseEntity<ListingResponseDto> getListingById(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.getListingById(id));
    }

    @PostMapping
    @Operation(summary = "Create a new listing")
    public ResponseEntity<ListingResponseDto> createListing(@Valid @RequestBody ListingRequestDto dto) {
        ListingResponseDto created = listingService.createListing(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing listing (owner only)")
    public ResponseEntity<ListingResponseDto> updateListing(
            @PathVariable Long id,
            @Valid @RequestBody ListingRequestDto dto) {
        return ResponseEntity.ok(listingService.updateListing(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a listing (owner only)")
    public ResponseEntity<Map<String, String>> deleteListing(@PathVariable Long id) {
        listingService.deleteListing(id);
        return ResponseEntity.ok(Map.of("message", "Listing deleted successfully", "id", id.toString()));
    }

    @PatchMapping("/{id}/sold")
    @Operation(summary = "Mark a listing as SOLD (owner only)")
    public ResponseEntity<ListingResponseDto> markAsSold(@PathVariable Long id) {
        return ResponseEntity.ok(listingService.markAsSold(id));
    }
}
