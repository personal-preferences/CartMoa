package org.personal.cartmoa.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.personal.cartmoa.entity.CartItem;
import org.personal.cartmoa.service.ProductExtractorService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class CoupangProductExtractor implements ProductExtractorService {
    @Override
    public CartItem extractProductInfo(String productUrl) throws Exception {
        try {
            Document doc = Jsoup.connect(productUrl).get();
            
            String productName = doc.select("h2.prod-buy-header__title").text();
            String priceText = doc.select("span.total-price strong").text().replaceAll("[^0-9]", "");
            Integer price = Integer.parseInt(priceText);
            String shippingCost = doc.select("div.prod-shipping-fee-message").text();
            String imageUrl = doc.select("img.prod-image__detail").attr("src");
            String storeName = "쿠팡";

            return CartItem.builder()
                    .productName(productName)
                    .price(price)
                    .shippingCost(shippingCost)
                    .imageUrl(imageUrl)
                    .productUrl(productUrl)
                    .storeName(storeName)
                    .build();
        } catch (IOException e) {
            throw new Exception("상품 정보를 가져올 수 없습니다.", e);
        }
    }
} 