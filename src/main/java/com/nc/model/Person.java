package com.nc.model;

import com.nc.annotation.CheckOnEmail;
import com.nc.annotation.CheckOnHtml;
import com.nc.annotation.CheckOnNumber;
import com.nc.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "person")
public class Person implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "person_id")
    @DecimalMin(value = "0.00")
    private long id;
    @Column(name = "name")
    @NotBlank(message = "Заполните поле")
    @CheckOnNumber(message = "Уберите цифры из имени")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String name;
    @Column(name = "surname")
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnNumber(message = "Уберите цифры из фамилии")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String surname;
    @Column(name = "mail")
    @NotBlank(message = "Ошибка")
    @Email(message = "Неверный формат почты")
    @CheckOnEmail(message = "Неверный формат почты")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String mail;
    @Column(name = "login")
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String login;
    @Column(name = "password")
    @NotBlank(message = "Заполните поле")
    @Length(max = 255, message = "Слишком длинное значение")
    @CheckOnHtml(message = "Некорректный ввод. Присутсвуют теги html.")
    private String password;
    private boolean active;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "activation_code")
    private String activationCode;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<Role> roles = new LinkedHashSet<>();
        roles.add(getRole());
        return roles;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive();
    }
}
