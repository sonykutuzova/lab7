package data;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

public class User implements Serializable {
    private Integer id;
    private String name;         // Уникальный логин
    private String password;  // Хэш пароля (например, MD5/SHA-256)

    public User(String name, String password) {
        if (name == null || password == null) {
            throw new IllegalArgumentException("Имя и пароль не могут быть null");
        }
        this.name = name;
        this.password = password;
    }

    // Геттеры (сеттеры только если нужны)
    public int getId() { return id; }
    public String getName() { return name; }
    public String getPassword() { return password; }


}
