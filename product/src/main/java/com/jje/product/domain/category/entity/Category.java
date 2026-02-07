package com.jje.product.domain.category.entity;

import com.jje.product.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    private Long parentId;

    @Column(nullable = false)
    private Integer depth;

    @Builder
    public Category(String name, Long parentId, Integer depth) {
        this.name = name;
        this.parentId = parentId;
        this.depth = depth != null ? depth : 0;
    }

    public void update(String name) {
        this.name = name;
    }
}
