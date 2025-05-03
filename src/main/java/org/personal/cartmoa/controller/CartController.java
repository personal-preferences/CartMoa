package org.personal.cartmoa.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.personal.cartmoa.entity.CartItem;
import org.personal.cartmoa.entity.SharedCart;
import org.personal.cartmoa.service.CartService;
import org.personal.cartmoa.service.ProductExtractorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    private final ProductExtractorService productExtractorService;

    @GetMapping("/")
    public String index(Model model) {
        List<CartItem> items = cartService.getCartItems();
        model.addAttribute("items", items);
        return "index";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        List<CartItem> items = cartService.getCartItems();
        int totalPrice = items.stream().mapToInt(CartItem::getPrice).sum();
        model.addAttribute("items", items);
        model.addAttribute("totalPrice", totalPrice);
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addItem(@RequestParam String productUrl, RedirectAttributes redirectAttributes) {
        try {
            CartItem item = productExtractorService.extractProductInfo(productUrl);
            cartService.addItem(item);
            redirectAttributes.addFlashAttribute("message", "상품이 장바구니에 추가되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeItem(@RequestParam Long itemId, RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(itemId);
            redirectAttributes.addFlashAttribute("message", "상품이 삭제되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/share")
    public String shareCart(RedirectAttributes redirectAttributes) {
        try {
            String shareId = cartService.shareCart();
            redirectAttributes.addFlashAttribute("shareUrl", "/share/" + shareId);
            redirectAttributes.addFlashAttribute("message", "장바구니가 공유되었습니다.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/cart";
    }

    @GetMapping("/share/{shareId}")
    public String viewSharedCart(@PathVariable String shareId, Model model) {
        try {
            SharedCart sharedCart = cartService.getSharedCart(shareId);
            int totalPrice = sharedCart.getItems().stream().mapToInt(CartItem::getPrice).sum();
            model.addAttribute("items", sharedCart.getItems());
            model.addAttribute("totalPrice", totalPrice);
            return "shared-cart";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "error";
        }
    }
} 