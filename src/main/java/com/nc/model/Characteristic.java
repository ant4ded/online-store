package com.nc.model;

import com.nc.annotation.CheckOnHtml;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "characteristic")
public class Characteristic {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DecimalMin(value = "0.00")
    @Column(name = "characteristic_id")
    private long id;
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    @Column(name = "name")
    private String name;
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    @Column(name = "value")
    private String value;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
}
