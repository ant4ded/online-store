package com.nc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Entity
@Table(name = "hardware")
public class Hardware {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hardware_id")
    private long id;
    @Column(name = "name")
    private String name;
    @Column(name = "description")
    private String description;
    @Column(name = "price")
    private double price;
    @Column(name = "count")
    private int totalCount;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "characteristic_id", table = "category")
    private List<Characteristic> characteristics;
    @Lob
    @Column(name = "image", length = 10000000)
    private byte[] image;

    public String generateBase64Image() {
        return new String(Base64.getEncoder().encode(this.image == null ? new byte[0] : this.image), StandardCharsets.UTF_8);
    }
}
