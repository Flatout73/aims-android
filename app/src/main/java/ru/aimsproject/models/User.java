package ru.aimsproject.models;

/**
 * Created by Антон on 27.10.2016.
 * Представляет пользователя.
 */
public class User implements Comparable<User> {
    /**
     * Имя пользователя.
     */
    private String name;

    /**
     * Логин пользователя.
     */
    private String login;

    /**
     * E-mail пользователя.
     */
    private String email;

    /**
     * Пол пользователя.
     */
    private int sex;

    /**
     * Аватарка пользователя (изображение, сохранённое в виде строки).
     */
    private String image;

    /**
     * Конструктор, инициализирует объект пользователя.
     * @param name Имя пользователя.
     * @param login Логин пользователя.
     * @param email E-mail пользователя.
     * @param sex Пол пользователя.
     * @param image Аватарка пользователя (изображение, сохранённое в виде строки).
     */
    public User(String name, String login, String email, int sex, String image) {
        this.name = name;
        this.login = login;
        this.email = email;
        this.sex = sex;
        this.image = image;
    }

    /**
     * Возвращает имя пользователя.
     * @return Имя пользователя.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает логин пользователя.
     * @return Логин пользователя.
     */
    public String getLogin() {
        return login;
    }

    /**
     * Возвращает E-mail пользователя.
     * @return E-mail пользователя.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Возвращает пол пользователя.
     * @return Пол пользователя.
     */
    public int getSex() {
        return sex;
    }

    /**
     * Возвращает аватарку пользователя (изображение, сохранённое в виде строки).
     * @return Аватарка пользователя (изображение, сохранённое в виде строки).
     */
    public String getImage() {
        return image;
    }

    /**
     * Сравнивает пользователя с другим для сортировки по возрастанию логинов пользователей.
     * @param other Другой пользователь.
     * @return Значение для метода сортировки по возрастанию логинов пользователей.
     */
    @Override
    public int compareTo(User other) {
        return login.compareTo(other.getLogin());
    }
}
