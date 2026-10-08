package com.campus.marketplace.repository;

import com.campus.marketplace.entity.Category;
import com.campus.marketplace.entity.Listing;
import com.campus.marketplace.entity.ListingStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListingRepository extends JpaRepository<Listing, Long> {

    List<Listing> findBySellerIdOrderByCreatedAtDesc(Long sellerId);

    List<Listing> findTop6ByStatusOrderByCreatedAtDesc(ListingStatus status);

    @Query("SELECT l FROM Listing l WHERE " +
           "(:status IS NULL OR l.status = :status) AND " +
           "(:category IS NULL OR l.category = :category) AND " +
           "(:searchTerm IS NULL OR :searchTerm = '' OR " +
           " LOWER(l.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           " LOWER(l.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Listing> searchAndFilter(
            @Param("searchTerm") String searchTerm,
            @Param("category") Category category,
            @Param("status") ListingStatus status,
            Sort sort
    );
}
