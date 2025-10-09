package com.group35.smartcart.service;

import com.group35.smartcart.entity.Customer;
import com.group35.smartcart.entity.Review;
import com.group35.smartcart.repository.CustomerRepository;
import com.group35.smartcart.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Transactional
    public Review createReview(String username, int stars, String message) {
        if (stars < 1 || stars > 5) {
            throw new IllegalArgumentException("Stars must be between 1 and 5");
        }

        Customer customer = customerRepository
                .findById(username)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Review review = new Review(customer, stars, message);
        return reviewRepository.save(review);
    }

    @Transactional(readOnly = true)
    public Review getReview(Long id) {
        return reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
    }

    @Transactional(readOnly = true)
    public List<Review> getReviewsByCustomerUsername(String username) {
        return reviewRepository.findByCustomerUsername(username);
    }

    @Transactional(readOnly = true)
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review updateReview(Long id, Integer stars, String message) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));

        if (stars != null) {
            if (stars < 1 || stars > 5) {
                throw new IllegalArgumentException("Stars must be between 1 and 5");
            }
            existing.setStars(stars);
        }

        if (message != null && !message.isBlank()) {
            existing.setMessage(message);
        }

        return reviewRepository.save(existing);
    }

    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new NoSuchElementException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    @Transactional
    public Review setAdminVerified(Long id, boolean verified) {
        Review existing = reviewRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Review not found"));
        existing.setAdminVerified(verified);
        return reviewRepository.save(existing);
    }
}