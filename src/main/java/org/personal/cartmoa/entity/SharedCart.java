package org.personal.cartmoa.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "shared_carts")
public class SharedCart {
    @Id
    @Column(nullable = false, unique = true)
    private String shareId;

    @Column(nullable = false)
    private LocalDateTime expiryTimestamp;

    @Column(nullable = false)
    private LocalDateTime createdTimestamp;

    @OneToMany(mappedBy = "sharedCart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<CartItem> items = new ArrayList<>();
} 