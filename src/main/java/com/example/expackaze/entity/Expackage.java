package com.example.expackaze.entity;

import com.example.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "package")
public class Expackage extends BaseEntity {       // PT 상품 패키지

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageSeq;

    private String packageName;
    private Integer count;
    private Integer period;
}
