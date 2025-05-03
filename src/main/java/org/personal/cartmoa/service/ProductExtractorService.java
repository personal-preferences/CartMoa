package org.personal.cartmoa.service;

import org.personal.cartmoa.entity.CartItem;

public interface ProductExtractorService {
    CartItem extractProductInfo(String productUrl) throws Exception;
} 