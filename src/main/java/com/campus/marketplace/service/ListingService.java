package com.campus.marketplace.service;

import com.campus.marketplace.dto.ListingRequestDto;
import com.campus.marketplace.dto.ListingResponseDto;
import com.campus.marketplace.entity.Category;
import com.campus.marketplace.entity.Listing;
import com.campus.marketplace.entity.ListingStatus;
import com.campus.marketplace.entity.User;
import com.campus.marketplace.exception.ForbiddenException;
import com.campus.marketplace.exception.ResourceNotFoundException;
import com.campus.marketplace.repository.ListingRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.campus.marketplace.repository.WishlistRepository;
import com.campus.marketplace.repository.InquiryRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingService {

    private final ListingRepository listingRepository;
    private final AuthService authService;
    private final WishlistRepository wishlistRepository;
    private final InquiryRepository inquiryRepository;

    public ListingService(ListingRepository listingRepository,
                          AuthService authService,
                          WishlistRepository wishlistRepository,
                          InquiryRepository inquiryRepository) {
        this.listingRepository = listingRepository;
        this.authService = authService;
        this.wishlistRepository = wishlistRepository;
        this.inquiryRepository = inquiryRepository;
    }

    @Transactional
    public ListingResponseDto createListing(ListingRequestDto dto) {
        User currentUser = authService.getAuthenticatedUser();

        Listing listing = new Listing();
        listing.setTitle(dto.getTitle().trim());
        listing.setDescription(dto.getDescription().trim());
        listing.setPrice(dto.getPrice());
        listing.setCategory(dto.getCategory());
        listing.setImageUrl(dto.getImageUrl());
        listing.setConditionType(dto.getConditionType() != null ? dto.getConditionType() : "Good");
        listing.setIsbn(dto.getIsbn());
        listing.setAuthor(dto.getAuthor());
        listing.setPickupLocation(dto.getPickupLocation() != null ? dto.getPickupLocation() : "Campus Student Center");
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setSeller(currentUser);

        Listing saved = listingRepository.save(listing);
        return mapToDto(saved);
    }

    @Transactional
    public ListingResponseDto updateListing(Long id, ListingRequestDto dto) {
        User currentUser = authService.getAuthenticatedUser();
        Listing listing = findListingOrThrow(id);

        // Strict Owner Authorization Check (Requirement 15)
        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to edit this listing. Only the verified seller (User ID: " + listing.getSeller().getId() + ") has permission.");
        }

        listing.setTitle(dto.getTitle().trim());
        listing.setDescription(dto.getDescription().trim());
        listing.setPrice(dto.getPrice());
        listing.setCategory(dto.getCategory());
        if (dto.getImageUrl() != null && !dto.getImageUrl().isBlank()) {
            listing.setImageUrl(dto.getImageUrl());
        }
        if (dto.getConditionType() != null) {
            listing.setConditionType(dto.getConditionType());
        }
        listing.setIsbn(dto.getIsbn());
        listing.setAuthor(dto.getAuthor());
        if (dto.getPickupLocation() != null) {
            listing.setPickupLocation(dto.getPickupLocation());
        }

        Listing updated = listingRepository.save(listing);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteListing(Long id) {
        User currentUser = authService.getAuthenticatedUser();
        Listing listing = findListingOrThrow(id);

        // Strict Owner Authorization Check (Requirement 15)
        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to delete this listing. Only the verified seller (User ID: " + listing.getSeller().getId() + ") has permission.");
        }

        // Clean up any saved wishlist items and inquiries for this listing
        wishlistRepository.deleteByListingId(id);
        inquiryRepository.deleteByListingId(id);

        listingRepository.delete(listing);
    }

    @Transactional
    public ListingResponseDto markAsSold(Long id) {
        User currentUser = authService.getAuthenticatedUser();
        Listing listing = findListingOrThrow(id);

        // Strict Owner Authorization Check (Requirement 15)
        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("You are not authorized to mark this listing as sold. Only the verified seller (User ID: " + listing.getSeller().getId() + ") has permission.");
        }

        listing.setStatus(ListingStatus.SOLD);
        Listing updated = listingRepository.save(listing);
        return mapToDto(updated);
    }

    @Transactional(readOnly = true)
    public ListingResponseDto getListingById(Long id) {
        Listing listing = findListingOrThrow(id);
        return mapToDto(listing);
    }

    @Transactional(readOnly = true)
    public List<ListingResponseDto> getMyListings() {
        User currentUser = authService.getAuthenticatedUser();
        return listingRepository.findBySellerIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ListingResponseDto> searchAndFilterListings(String search, Category category, String sort, ListingStatus status) {
        Sort sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("price_asc".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "price");
        } else if ("newest".equalsIgnoreCase(sort)) {
            sorting = Sort.by(Sort.Direction.DESC, "createdAt");
        }

        String searchTerm = (search != null && !search.trim().isEmpty()) ? search.trim() : null;

        return listingRepository.searchAndFilter(searchTerm, category, status, sorting)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ListingResponseDto> getFeaturedListings() {
        return listingRepository.findTop6ByStatusOrderByCreatedAtDesc(ListingStatus.AVAILABLE)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private Listing findListingOrThrow(Long id) {
        return listingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + id));
    }

    public ListingResponseDto mapToDto(Listing listing) {
        ListingResponseDto dto = new ListingResponseDto();
        dto.setId(listing.getId());
        dto.setTitle(listing.getTitle());
        dto.setDescription(listing.getDescription());
        dto.setPrice(listing.getPrice());
        dto.setCategory(listing.getCategory());
        dto.setImageUrl(listing.getImageUrl());
        dto.setStatus(listing.getStatus());
        dto.setConditionType(listing.getConditionType());
        dto.setIsbn(listing.getIsbn());
        dto.setAuthor(listing.getAuthor());
        dto.setPickupLocation(listing.getPickupLocation());

        if (listing.getSeller() != null) {
            dto.setSellerId(listing.getSeller().getId());
            dto.setSellerUserId(listing.getSeller().getId());
            dto.setSellerName(listing.getSeller().getName());
            dto.setSellerEmail(listing.getSeller().getEmail());
            dto.setSellerPhone(listing.getSeller().getPhone());
            dto.setSellerCampus(listing.getSeller().getCampusName());
        }

        dto.setCreatedAt(listing.getCreatedAt());
        dto.setUpdatedAt(listing.getUpdatedAt());
        dto.setInquiryCount(inquiryRepository.countByListingId(listing.getId()));
        return dto;
    }
}
