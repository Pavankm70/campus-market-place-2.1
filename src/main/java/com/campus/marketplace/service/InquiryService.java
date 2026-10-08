package com.campus.marketplace.service;

import com.campus.marketplace.dto.InquiryReplyDto;
import com.campus.marketplace.dto.InquiryReplyRequestDto;
import com.campus.marketplace.dto.InquiryRequestDto;
import com.campus.marketplace.dto.InquiryResponseDto;
import com.campus.marketplace.entity.Inquiry;
import com.campus.marketplace.entity.InquiryReply;
import com.campus.marketplace.entity.Listing;
import com.campus.marketplace.entity.User;
import com.campus.marketplace.exception.BadRequestException;
import com.campus.marketplace.exception.ForbiddenException;
import com.campus.marketplace.exception.ResourceNotFoundException;
import com.campus.marketplace.repository.InquiryReplyRepository;
import com.campus.marketplace.repository.InquiryRepository;
import com.campus.marketplace.repository.ListingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
    private final ListingRepository listingRepository;
    private final AuthService authService;

    public InquiryService(InquiryRepository inquiryRepository,
                          InquiryReplyRepository inquiryReplyRepository,
                          ListingRepository listingRepository,
                          AuthService authService) {
        this.inquiryRepository = inquiryRepository;
        this.inquiryReplyRepository = inquiryReplyRepository;
        this.listingRepository = listingRepository;
        this.authService = authService;
    }

    @Transactional
    public InquiryResponseDto createInquiry(Long listingId, InquiryRequestDto dto) {
        User sender = authService.getAuthenticatedUser();
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + listingId));

        if (listing.getSeller().getId().equals(sender.getId())) {
            throw new BadRequestException("You cannot send an inquiry for your own listing");
        }

        // Check if an existing thread exists between this buyer and seller for this listing
        Optional<Inquiry> existingThread = inquiryRepository.findByListingIdAndSenderId(listingId, sender.getId());
        if (existingThread.isPresent()) {
            Inquiry inquiry = existingThread.get();
            InquiryReply reply = new InquiryReply(inquiry, sender, dto.getMessage().trim());
            inquiryReplyRepository.save(reply);
            inquiry.addReply(reply);
            Inquiry updated = inquiryRepository.save(inquiry);
            return mapToDto(updated, sender);
        }

        Inquiry inquiry = new Inquiry(
                listing,
                sender,
                listing.getSeller(),
                dto.getMessage().trim(),
                dto.getContactInfo() != null && !dto.getContactInfo().isBlank() ? dto.getContactInfo().trim() : sender.getEmail()
        );

        Inquiry saved = inquiryRepository.save(inquiry);
        return mapToDto(saved, sender);
    }

    @Transactional
    public InquiryResponseDto replyToInquiry(Long inquiryId, InquiryReplyRequestDto dto) {
        User currentUser = authService.getAuthenticatedUser();
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with ID: " + inquiryId));

        boolean isSender = inquiry.getSender().getId().equals(currentUser.getId());
        boolean isReceiver = inquiry.getReceiver().getId().equals(currentUser.getId());

        if (!isSender && !isReceiver) {
            throw new ForbiddenException("You are not authorized to participate in this conversation");
        }

        InquiryReply reply = new InquiryReply(inquiry, currentUser, dto.getMessage().trim());
        inquiryReplyRepository.save(reply);
        inquiry.addReply(reply);
        Inquiry updated = inquiryRepository.save(inquiry);

        return mapToDto(updated, currentUser);
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getAllUserConversations() {
        User currentUser = authService.getAuthenticatedUser();
        return inquiryRepository.findAllUserConversations(currentUser.getId())
                .stream()
                .map(inq -> mapToDto(inq, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InquiryResponseDto getConversationById(Long inquiryId) {
        User currentUser = authService.getAuthenticatedUser();
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResourceNotFoundException("Conversation not found with ID: " + inquiryId));

        boolean isSender = inquiry.getSender().getId().equals(currentUser.getId());
        boolean isReceiver = inquiry.getReceiver().getId().equals(currentUser.getId());

        if (!isSender && !isReceiver) {
            throw new ForbiddenException("You are not authorized to view this conversation");
        }

        return mapToDto(inquiry, currentUser);
    }

    @Transactional(readOnly = true)
    public Optional<InquiryResponseDto> getInquiryByListingForCurrentUser(Long listingId) {
        User currentUser = authService.getAuthenticatedUser();
        return inquiryRepository.findByListingIdAndSenderId(listingId, currentUser.getId())
                .map(inq -> mapToDto(inq, currentUser));
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getInquiriesForListing(Long listingId) {
        User currentUser = authService.getAuthenticatedUser();
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResourceNotFoundException("Listing not found with ID: " + listingId));
        if (!listing.getSeller().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Only the seller can view all buyer inquiries for this listing");
        }
        return inquiryRepository.findByListingIdOrderByCreatedAtDesc(listingId)
                .stream()
                .map(inq -> mapToDto(inq, currentUser))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<InquiryResponseDto> getReceivedInquiries() {
        User user = authService.getAuthenticatedUser();
        return inquiryRepository.findByReceiverIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(inq -> mapToDto(inq, user))
                .collect(Collectors.toList());
    }

    private InquiryResponseDto mapToDto(Inquiry inq, User currentUser) {
        InquiryResponseDto dto = new InquiryResponseDto();
        dto.setId(inq.getId());
        dto.setListingId(inq.getListing().getId());
        dto.setListingTitle(inq.getListing().getTitle());
        dto.setListingPrice(inq.getListing().getPrice());
        dto.setListingImageUrl(inq.getListing().getImageUrl());
        dto.setListingCategory(inq.getListing().getCategory() != null ? inq.getListing().getCategory().name() : null);
        dto.setListingStatus(inq.getListing().getStatus());

        dto.setSenderId(inq.getSender().getId());
        dto.setSenderName(inq.getSender().getName());
        dto.setSenderEmail(inq.getSender().getEmail());

        dto.setReceiverId(inq.getReceiver().getId());
        dto.setReceiverName(inq.getReceiver().getName());
        dto.setReceiverEmail(inq.getReceiver().getEmail());

        dto.setMessage(inq.getMessage());
        dto.setContactInfo(inq.getContactInfo());
        dto.setCreatedAt(inq.getCreatedAt());
        dto.setUpdatedAt(inq.getUpdatedAt() != null ? inq.getUpdatedAt() : inq.getCreatedAt());

        boolean isBuyer = inq.getSender().getId().equals(currentUser.getId());
        dto.setBuyer(isBuyer);
        dto.setOtherPartyName(isBuyer ? inq.getReceiver().getName() : inq.getSender().getName());
        dto.setOtherPartyRole(isBuyer ? "Seller" : "Buyer");

        List<InquiryReplyDto> replyDtos = inq.getReplies().stream()
                .map(r -> new InquiryReplyDto(
                        r.getId(),
                        r.getSender().getId(),
                        r.getSender().getName(),
                        r.getSender().getEmail(),
                        r.getMessage(),
                        r.getCreatedAt(),
                        r.getSender().getId().equals(inq.getReceiver().getId()),
                        r.getSender().getId().equals(currentUser.getId())
                ))
                .collect(Collectors.toList());
        dto.setReplies(replyDtos);

        if (!replyDtos.isEmpty()) {
            InquiryReplyDto last = replyDtos.get(replyDtos.size() - 1);
            dto.setLastMessage(last.getMessage());
            dto.setLastMessageTime(last.getCreatedAt());
        } else {
            dto.setLastMessage(inq.getMessage());
            dto.setLastMessageTime(inq.getCreatedAt());
        }

        return dto;
    }
}
