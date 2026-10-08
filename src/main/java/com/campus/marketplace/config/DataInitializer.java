package com.campus.marketplace.config;

import com.campus.marketplace.entity.Category;
import com.campus.marketplace.entity.Listing;
import com.campus.marketplace.entity.ListingStatus;
import com.campus.marketplace.entity.User;
import com.campus.marketplace.repository.ListingRepository;
import com.campus.marketplace.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Arrays;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, ListingRepository listingRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized with demo data.");
            return;
        }

        log.info("Seeding realistic Campus Marketplace demo data...");

        // Seed Users
        User alex = new User("Alex Rivera", "alex@nmit.ac.in", passwordEncoder.encode("Password123!"));
        alex.setPhone("9876543210");
        alex.setCampusName("NMIT Main Campus");

        User sarah = new User("Sarah Chen", "sarah@nmit.ac.in", passwordEncoder.encode("Password123!"));
        sarah.setPhone("9876543211");
        sarah.setCampusName("NMIT Science Quad");

        User marcus = new User("Marcus Johnson", "marcus@nmit.ac.in", passwordEncoder.encode("Password123!"));
        marcus.setPhone("9876543212");
        marcus.setCampusName("NMIT Engineering Hall");

        userRepository.saveAll(Arrays.asList(alex, sarah, marcus));

        // Seed Listings
        Listing l1 = new Listing();
        l1.setTitle("Introduction to Algorithms (CLRS 4th Edition)");
        l1.setDescription("Essential textbook for CS201 / Algorithms course. Barely highlighted, binding in mint condition. Includes cheat-sheet inserts.");
        l1.setPrice(new BigDecimal("65.00"));
        l1.setCategory(Category.BOOKS);
        l1.setImageUrl("https://images.unsplash.com/photo-1532012164546-f432f2e3777a?w=800&auto=format&fit=crop&q=80");
        l1.setStatus(ListingStatus.AVAILABLE);
        l1.setConditionType("Like New");
        l1.setIsbn("9780262046305");
        l1.setAuthor("Thomas H. Cormen, Charles E. Leiserson");
        l1.setPickupLocation("CS Building Lobby / Library 2nd Floor");
        l1.setSeller(alex);

        Listing l2 = new Listing();
        l2.setTitle("TI-84 Plus CE Color Graphing Calculator");
        l2.setDescription("Preloaded with all programs for Calculus, Stats, and Physics. Comes with official USB charging cable and slide protective cover.");
        l2.setPrice(new BigDecimal("75.00"));
        l2.setCategory(Category.ELECTRONICS);
        l2.setImageUrl("https://images.unsplash.com/photo-1587145820266-a5951ee6f620?w=800&auto=format&fit=crop&q=80");
        l2.setStatus(ListingStatus.AVAILABLE);
        l2.setConditionType("Good");
        l2.setPickupLocation("Student Union Food Court");
        l2.setSeller(marcus);

        Listing l3 = new Listing();
        l3.setTitle("Chemistry Lab Coat & Safety Goggles Set");
        l3.setDescription("Size M white 100% cotton flame-resistant lab coat and UV-blocking splash-proof safety goggles. Required for CHEM 101/102 labs.");
        l3.setPrice(new BigDecimal("22.00"));
        l3.setCategory(Category.LAB_SUPPLIES);
        l3.setImageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&auto=format&fit=crop&q=80");
        l3.setStatus(ListingStatus.AVAILABLE);
        l3.setConditionType("Good");
        l3.setPickupLocation("Science Hall Lab Wing");
        l3.setSeller(sarah);

        Listing l4 = new Listing();
        l4.setTitle("Campbell Biology (12th Edition)");
        l4.setDescription("Standard textbook for General Biology BIO110. Clean pages, no missing diagrams. Saved me during midterm prep.");
        l4.setPrice(new BigDecimal("80.00"));
        l4.setCategory(Category.BOOKS);
        l4.setImageUrl("https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?w=800&auto=format&fit=crop&q=80");
        l4.setStatus(ListingStatus.AVAILABLE);
        l4.setConditionType("Very Good");
        l4.setIsbn("9780135188743");
        l4.setAuthor("Lisa A. Urry, Michael L. Cain");
        l4.setPickupLocation("Health Sciences Atrium");
        l4.setSeller(sarah);

        Listing l5 = new Listing();
        l5.setTitle("Ergonomic Mesh Dorm Desk Chair");
        l5.setDescription("Breathable mesh high-back chair with adjustable armrests and lumbar support. Fits nicely into standard dorm desks. Pick up on North Campus.");
        l5.setPrice(new BigDecimal("45.00"));
        l5.setCategory(Category.FURNITURE);
        l5.setImageUrl("https://images.unsplash.com/photo-1580481077195-c3a821a506cb?w=800&auto=format&fit=crop&q=80");
        l5.setStatus(ListingStatus.AVAILABLE);
        l5.setConditionType("Good");
        l5.setPickupLocation("Maple Residence Hall Lounge");
        l5.setSeller(alex);

        Listing l6 = new Listing();
        l6.setTitle("Apple iPad 9th Gen 64GB WiFi with Apple Pencil");
        l6.setDescription("Space Gray iPad with 1st Gen Apple Pencil. Screen protector applied from day 1. Fantastic for GoodNotes or Notability lecture notes.");
        l6.setPrice(new BigDecimal("240.00"));
        l6.setCategory(Category.ELECTRONICS);
        l6.setImageUrl("https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=800&auto=format&fit=crop&q=80");
        l6.setStatus(ListingStatus.SOLD); // Demo SOLD item!
        l6.setConditionType("Like New");
        l6.setPickupLocation("Campus Tech Store Front");
        l6.setSeller(marcus);

        Listing l7 = new Listing();
        l7.setTitle("Official Campus Athletics Varsity Hoodie (Size M)");
        l7.setDescription("Heavyweight 80/20 cotton blend campus hoodie. Navy blue with embroidered golden crest. Only worn twice, laundered and ready.");
        l7.setPrice(new BigDecimal("30.00"));
        l7.setCategory(Category.CLOTHING);
        l7.setImageUrl("https://images.unsplash.com/photo-1556905055-8f358a7a47b2?w=800&auto=format&fit=crop&q=80");
        l7.setStatus(ListingStatus.AVAILABLE);
        l7.setConditionType("Like New");
        l7.setPickupLocation("Recreation Center entrance");
        l7.setSeller(alex);

        Listing l8 = new Listing();
        l8.setTitle("Midori & Moleskine Grid Notebook Set + Fineliners");
        l8.setDescription("3 unused dotted/grid notebooks and a set of 6 waterproof archival ink fineliner pens (0.1 to 0.8mm). Perfect for bullet journaling and engineering sketch notes.");
        l8.setPrice(new BigDecimal("18.00"));
        l8.setCategory(Category.STATIONERY);
        l8.setImageUrl("https://images.unsplash.com/photo-1585776245991-cf89dd7fc73a?w=800&auto=format&fit=crop&q=80");
        l8.setStatus(ListingStatus.AVAILABLE);
        l8.setConditionType("New");
        l8.setPickupLocation("University Bookstore Plaza");
        l8.setSeller(sarah);

        listingRepository.saveAll(Arrays.asList(l1, l2, l3, l4, l5, l6, l7, l8));
        log.info("Demo data initialization complete with 3 users and 8 listings across all categories.");
    }
}
