package org.personal.cartmoa.service.impl;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.personal.cartmoa.entity.CartItem;
import org.personal.cartmoa.entity.SharedCart;
import org.personal.cartmoa.service.CartService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CartServiceImpl implements CartService {
    @PersistenceContext
    private EntityManager entityManager;

    @Value("${cartmoa.share.expiry-hours}")
    private int expiryHours;

    @Override
    @Transactional(readOnly = true)
    public List<CartItem> getCartItems() {
        return entityManager.createQuery("SELECT c FROM CartItem c", CartItem.class)
                .getResultList();
    }

    @Override
    @Transactional
    public void addItem(CartItem item) {
        entityManager.persist(item);
    }

    @Override
    @Transactional
    public void removeItem(Long itemId) {
        CartItem item = entityManager.find(CartItem.class, itemId);
        if (item != null) {
            entityManager.remove(item);
        }
    }

    @Override
    @Transactional
    public String shareCart() {
        List<CartItem> items = getCartItems();
        if (items.isEmpty()) {
            throw new IllegalStateException("장바구니가 비어 있습니다.");
        }

        String shareId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiryTime = now.plusHours(expiryHours);

        SharedCart sharedCart = SharedCart.builder()
                .shareId(shareId)
                .createdTimestamp(now)
                .expiryTimestamp(expiryTime)
                .build();

        entityManager.persist(sharedCart);

        for (CartItem item : items) {
            CartItem sharedItem = CartItem.builder()
                    .productName(item.getProductName())
                    .price(item.getPrice())
                    .shippingCost(item.getShippingCost())
                    .imageUrl(item.getImageUrl())
                    .productUrl(item.getProductUrl())
                    .storeName(item.getStoreName())
                    .sharedCart(sharedCart)
                    .build();
            entityManager.persist(sharedItem);
        }

        return shareId;
    }

    @Override
    @Transactional(readOnly = true)
    public SharedCart getSharedCart(String shareId) {
        SharedCart sharedCart = entityManager.find(SharedCart.class, shareId);
        if (sharedCart == null || sharedCart.getExpiryTimestamp().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("존재하지 않거나 만료된 공유 링크입니다.");
        }
        return sharedCart;
    }
} 