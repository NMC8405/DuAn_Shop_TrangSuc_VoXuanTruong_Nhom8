package com.jewelryshop.service;

import com.jewelryshop.entity.Product;
import com.jewelryshop.entity.Review;
import com.jewelryshop.entity.User;
import com.jewelryshop.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class ReviewService {

    @Autowired private ReviewRepository reviewRepository;

    public Review addReview(Long productId, Long userId, int rating, String comment) {
        if (reviewRepository.existsByProductIdAndUserId(productId, userId)) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi!");
        }
        Product product = new Product(); product.setId(productId);
        User user = new User(); user.setId(userId);

        Review review = Review.builder()
                .product(product).user(user)
                .rating(rating).comment(comment)
                .approved(false)
                .build();
        return reviewRepository.save(review);
    }

    public void approve(Long id) {
        reviewRepository.findById(id).ifPresent(r -> {
            r.setApproved(true);
            reviewRepository.save(r);
        });
    }

    public void delete(Long id) {
        reviewRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Review> findApprovedByProduct(Long productId) {
        return reviewRepository.findByProductIdAndApprovedTrueOrderByCreatedAtDesc(productId);
    }

    @Transactional(readOnly = true)
    public Page<Review> findAllForAdmin(int page, int size) {
        return reviewRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Double getAvgRating(Long productId) {
        return reviewRepository.avgRatingByProductId(productId);
    }
}
