package org.personal.cartmoa.service;

import org.personal.cartmoa.entity.CartItem;
import org.personal.cartmoa.entity.SharedCart;

import java.util.List;

public interface CartService {
    List<CartItem> getCartItems();
    void addItem(CartItem item);
    void removeItem(Long itemId);
    String shareCart();
    SharedCart getSharedCart(String shareId);
} 