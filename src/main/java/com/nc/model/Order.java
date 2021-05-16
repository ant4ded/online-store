package com.nc.model;

import com.nc.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private int count;
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('IN_CART', 'IN_PROCESSING', 'DELIVERED')")
    private Status status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "hardware_id")
    private Hardware hardware;
}
