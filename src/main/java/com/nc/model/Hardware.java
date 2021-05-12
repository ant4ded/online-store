package com.nc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;
import java.sql.SQLException;
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
    private Blob image;

    public byte[] getImage() {
        byte[] bytes = new byte[0];
        try {
            if (image != null) {
                log.info("Convert images from Blob to byte.");
                bytes = image.getBytes(1, (int) image.length());
                image.free();
            }
        } catch (SQLException e) {
            log.error("Error converting image from blob to byte:\n" + e);
            e.printStackTrace();
        }
        return bytes;
    }

    public String generateBase64Image() {
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(this.getImage()), StandardCharsets.UTF_8);
    }
}
