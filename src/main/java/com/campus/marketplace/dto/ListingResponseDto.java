package com.campus.marketplace.dto;

import com.campus.marketplace.entity.Category;
import com.campus.marketplace.entity.ListingStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ListingResponseDto {

    private Long id;
    private String title;
    private String description;
    private BigDecimal price;
    private Category category;
    private String categoryDisplayName;
    private String imageUrl;
    private ListingStatus status;
    private String conditionType;
    private String isbn;
    private String author;
    private String pickupLocation;

    // Seller summary (sellerId links directly to user ID)
    private Long sellerId;
    private Long sellerUserId;
    private String sellerName;
    private String sellerEmail;
    private String sellerPhone;
    private String sellerCampus;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long inquiryCount;

    public ListingResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            this.categoryDisplayName = category.getDisplayName();
        }
    }

    public String getCategoryDisplayName() {
        return categoryDisplayName;
    }

    public void setCategoryDisplayName(String categoryDisplayName) {
        this.categoryDisplayName = categoryDisplayName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public ListingStatus getStatus() {
        return status;
    }

    public void setStatus(ListingStatus status) {
        this.status = status;
    }

    public String getConditionType() {
        return conditionType;
    }

    public void setConditionType(String conditionType) {
        this.conditionType = conditionType;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPickupLocation() {
        return pickupLocation;
    }

    public void setPickupLocation(String pickupLocation) {
        this.pickupLocation = pickupLocation;
    }

    public Long getSellerId() {
        return sellerId;
    }

    public void setSellerId(Long sellerId) {
        this.sellerId = sellerId;
        if (this.sellerUserId == null) {
            this.sellerUserId = sellerId;
        }
    }

    public Long getSellerUserId() {
        return sellerUserId != null ? sellerUserId : sellerId;
    }

    public void setSellerUserId(Long sellerUserId) {
        this.sellerUserId = sellerUserId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerEmail() {
        return sellerEmail;
    }

    public void setSellerEmail(String sellerEmail) {
        this.sellerEmail = sellerEmail;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public void setSellerPhone(String sellerPhone) {
        this.sellerPhone = sellerPhone;
    }

    public String getSellerCampus() {
        return sellerCampus;
    }

    public void setSellerCampus(String sellerCampus) {
        this.sellerCampus = sellerCampus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getInquiryCount() {
        return inquiryCount != null ? inquiryCount : 0L;
    }

    public void setInquiryCount(Long inquiryCount) {
        this.inquiryCount = inquiryCount;
    }
}
