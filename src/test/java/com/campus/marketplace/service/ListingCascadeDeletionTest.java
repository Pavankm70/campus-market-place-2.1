package com.campus.marketplace.service;

import com.campus.marketplace.entity.*;
import com.campus.marketplace.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class ListingCascadeDeletionTest {

    @Autowired
    private ListingService listingService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private InquiryRepository inquiryRepository;

    @Autowired
    private InquiryReplyRepository inquiryReplyRepository;

    @Autowired
    private WishlistRepository wishlistRepository;

    private User seller;
    private User buyer1;
    private User buyer2;

    @BeforeEach
    void setUp() {
        wishlistRepository.deleteAll();
        inquiryReplyRepository.deleteAll();
        inquiryRepository.deleteAll();
        listingRepository.deleteAll();
        userRepository.deleteAll();

        seller = new User();
        seller.setName("Seller Student");
        seller.setEmail("seller@nmit.ac.in");
        seller.setPassword("password123");
        seller.setPhone("9876543210");
        seller.setCampusName("NMIT Main Campus");
        seller = userRepository.save(seller);

        buyer1 = new User();
        buyer1.setName("Buyer One");
        buyer1.setEmail("buyer1@nmit.ac.in");
        buyer1.setPassword("password123");
        buyer1.setPhone("9876543211");
        buyer1.setCampusName("NMIT Main Campus");
        buyer1 = userRepository.save(buyer1);

        buyer2 = new User();
        buyer2.setName("Buyer Two");
        buyer2.setEmail("buyer2@nmit.ac.in");
        buyer2.setPassword("password123");
        buyer2.setPhone("9876543212");
        buyer2.setCampusName("NMIT Main Campus");
        buyer2 = userRepository.save(buyer2);

        // Authenticate as seller by default
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(seller.getEmail(), null, Collections.emptyList())
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
        wishlistRepository.deleteAll();
        inquiryReplyRepository.deleteAll();
        inquiryRepository.deleteAll();
        listingRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void testDeleteListingWithMultipleInquiriesAndMultipleRepliesSucceeds() {
        // 1. Create a listing
        Listing listing = new Listing();
        listing.setTitle("Database Systems Textbook");
        listing.setDescription("Complete guide to DB design and SQL queries.");
        listing.setPrice(new BigDecimal("499.00"));
        listing.setCategory(Category.BOOKS);
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setConditionType("Like New");
        listing.setSeller(seller);
        listing = listingRepository.save(listing);
        Long listingId = listing.getId();

        // 2. Add wishlist entries
        Wishlist w1 = new Wishlist(buyer1, listing);
        Wishlist w2 = new Wishlist(buyer2, listing);
        wishlistRepository.save(w1);
        wishlistRepository.save(w2);

        // 3. Create Inquiry 1 (Buyer 1 -> Seller) with 2 replies
        Inquiry inq1 = new Inquiry(listing, buyer1, seller, "Is this textbook still available?", "buyer1@nmit.ac.in");
        inq1 = inquiryRepository.save(inq1);

        InquiryReply reply1a = new InquiryReply(inq1, seller, "Yes, it is available!");
        inquiryReplyRepository.save(reply1a);
        inq1.addReply(reply1a);

        InquiryReply reply1b = new InquiryReply(inq1, buyer1, "Can we meet at the library?");
        inquiryReplyRepository.save(reply1b);
        inq1.addReply(reply1b);
        inquiryRepository.save(inq1);

        // 4. Create Inquiry 2 (Buyer 2 -> Seller) with 2 replies
        Inquiry inq2 = new Inquiry(listing, buyer2, seller, "Can you do 400 for this?", "buyer2@nmit.ac.in");
        inq2 = inquiryRepository.save(inq2);

        InquiryReply reply2a = new InquiryReply(inq2, seller, "Price is firm at 499.");
        inquiryReplyRepository.save(reply2a);
        inq2.addReply(reply2a);

        InquiryReply reply2b = new InquiryReply(inq2, buyer2, "Understood, I'll take it at 499.");
        inquiryReplyRepository.save(reply2b);
        inq2.addReply(reply2b);
        inquiryRepository.save(inq2);

        Long inq1Id = inq1.getId();
        Long inq2Id = inq2.getId();
        Long reply1aId = reply1a.getId();
        Long reply1bId = reply1b.getId();
        Long reply2aId = reply2a.getId();
        Long reply2bId = reply2b.getId();

        // Verify pre-conditions
        assertTrue(listingRepository.existsById(listingId));
        assertTrue(inquiryRepository.existsById(inq1Id));
        assertTrue(inquiryRepository.existsById(inq2Id));
        assertTrue(inquiryReplyRepository.existsById(reply1aId));
        assertTrue(inquiryReplyRepository.existsById(reply1bId));
        assertTrue(inquiryReplyRepository.existsById(reply2aId));
        assertTrue(inquiryReplyRepository.existsById(reply2bId));
        assertEquals(2, wishlistRepository.findAll().stream().filter(w -> w.getListing().getId().equals(listingId)).count());

        // 5. Execute listing deletion
        assertDoesNotThrow(() -> listingService.deleteListing(listingId));

        // 6. Verify post-conditions: all child records and listing are cleanly deleted
        assertFalse(listingRepository.existsById(listingId), "Listing must be deleted");
        assertFalse(inquiryRepository.existsById(inq1Id), "Inquiry 1 must be deleted");
        assertFalse(inquiryRepository.existsById(inq2Id), "Inquiry 2 must be deleted");
        assertFalse(inquiryReplyRepository.existsById(reply1aId), "Reply 1a must be deleted");
        assertFalse(inquiryReplyRepository.existsById(reply1bId), "Reply 1b must be deleted");
        assertFalse(inquiryReplyRepository.existsById(reply2aId), "Reply 2a must be deleted");
        assertFalse(inquiryReplyRepository.existsById(reply2bId), "Reply 2b must be deleted");
        assertEquals(0, wishlistRepository.findAll().stream().filter(w -> w.getListing().getId().equals(listingId)).count(), "Wishlists must be deleted");

        // 7. Verify unrelated users remain intact
        assertTrue(userRepository.existsById(seller.getId()), "Seller must not be deleted");
        assertTrue(userRepository.existsById(buyer1.getId()), "Buyer 1 must not be deleted");
        assertTrue(userRepository.existsById(buyer2.getId()), "Buyer 2 must not be deleted");
    }

    @Test
    void testDeleteListingWithZeroInquiriesSucceeds() {
        // Create listing with 0 inquiries
        Listing listing = new Listing();
        listing.setTitle("Scientific Calculator fx-991EX");
        listing.setDescription("Great condition calculator.");
        listing.setPrice(new BigDecimal("800.00"));
        listing.setCategory(Category.ELECTRONICS);
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setConditionType("Like New");
        listing.setSeller(seller);
        listing = listingRepository.save(listing);
        Long listingId = listing.getId();

        assertTrue(listingRepository.existsById(listingId));

        // Delete listing
        assertDoesNotThrow(() -> listingService.deleteListing(listingId));

        // Verify listing deleted
        assertFalse(listingRepository.existsById(listingId));
        assertTrue(userRepository.existsById(seller.getId()));
    }

    @Test
    void testDeleteInquiryDirectlyWithRepliesSucceeds() {
        // Create a listing
        Listing listing = new Listing();
        listing.setTitle("Desk Lamp");
        listing.setDescription("Adjustable LED lamp.");
        listing.setPrice(new BigDecimal("250.00"));
        listing.setCategory(Category.OTHER);
        listing.setStatus(ListingStatus.AVAILABLE);
        listing.setConditionType("Good");
        listing.setSeller(seller);
        listing = listingRepository.save(listing);

        // Create an inquiry with replies
        Inquiry inq = new Inquiry(listing, buyer1, seller, "Is it rechargeable?", "buyer1@nmit.ac.in");
        inq = inquiryRepository.save(inq);

        InquiryReply reply = new InquiryReply(inq, seller, "Yes, USB-C rechargeable.");
        inquiryReplyRepository.save(reply);
        inq.addReply(reply);
        inquiryRepository.save(inq);

        Long inqId = inq.getId();
        Long replyId = reply.getId();

        assertTrue(inquiryRepository.existsById(inqId));
        assertTrue(inquiryReplyRepository.existsById(replyId));

        // Delete inquiry directly via repository
        assertDoesNotThrow(() -> inquiryRepository.deleteById(inqId));

        // Verify inquiry and reply are deleted, while listing and users remain
        assertFalse(inquiryRepository.existsById(inqId), "Inquiry must be deleted");
        assertFalse(inquiryReplyRepository.existsById(replyId), "Inquiry reply must be deleted");
        assertTrue(listingRepository.existsById(listing.getId()), "Listing must remain");
    }
}
