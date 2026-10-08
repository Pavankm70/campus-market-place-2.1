package com.campus.marketplace.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer runs on application startup.
 * Automatic seeding of dummy/sample data has been disabled so the marketplace
 * runs in clean production mode with only user-created data.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Override
    public void run(String... args) {
        log.info("Campus Marketplace started in clean production mode (automatic dummy data seeding disabled).");
    }
}
