package com.jewelryshop.repository;

import com.jewelryshop.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductIdAndApprovedTrueOrderByCreatedAtDesc(Long productId);
    Page<Review> findAllByOrderByCreatedAtDesc(Pageable pageable);
    boolean existsByProductIdAndUserId(Long productId, Long userId);

    @Query("SELECT COALESCE(AVG(CAST(r.rating AS double)), 0.0) FROM Review r WHERE r.product.id = :productId AND r.approved = true")
    Double avgRatingByProductId(@Param("productId") Long productId);
}
