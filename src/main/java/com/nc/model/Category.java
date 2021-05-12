package com.nc.model;

import com.nc.annotation.CheckOnHtml;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;


@Data
@Entity
@Table(name = "category")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @DecimalMin(value = "0.00")
    @Column(name = "category_id")
    private long id;

    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    @Column(name = "name")
    private String name;
}
