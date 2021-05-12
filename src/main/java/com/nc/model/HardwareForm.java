package com.nc.model;

import com.nc.annotation.CheckOnHtml;
import com.nc.annotation.CheckPrice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HardwareForm {
    @DecimalMin(value = "0.00")
    private long id;
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String name;
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String description;
    @NotBlank(message = "Заполните поле")
    @CheckPrice(message = "Цена не может быть отрицательной")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String price;
    @DecimalMin(value = "0.00", message = "Значение должно быть 0 или больше нуля.")
    private int totalCount;
    private Category category;
    private List<Characteristic> characteristics;
    private MultipartFile image;
}
