package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.List;

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
     * Список друзей пользователя.
     */
    private List<User> friends = new ArrayList<User>();

    /**
     * Список целей пользователя.
     */
    private List<Aim> aims = new ArrayList<Aim>();

    /**
     * Список достижений пользователя.
     */
    private List<Achievement> achievements = new ArrayList<Achievement>();

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
     * Возвращает список друзей пользователя.
     * @return Список друзей пользователя.
     */
    public List<User> getFriends() {
        return friends;
    }

    /**
     * Возвращает список целей пользователя.
     * @return Список целей пользователя.
     */
    public List<Aim> getAims() {
        return aims;
    }

    /**
     * Возвращает список достижений пользователя.
     * @return Список достижений пользователя.
     */
    public List<Achievement> getAchievements() {
        return achievements;
    }

    /**
     * Добавляет пользователя в список друзей пользователя, если его ещё нет в этом списке.
     * @param user Добавляемый в список друзей пользователь.
     * @return true, если добавление пользователя в список друзей состоялось успешно (его ещё не было в этом списке), иначе false.
     * @throws NullPointerException Возникает, если переданный пользователь представляет собой null.
     */
    public boolean addFriend(User user) throws NullPointerException {
        if(!friends.contains(user)) {
            friends.add(user);
            user.addFriend(this);
            return true;
        }
        return false;
    }

    /**
     * Добавляет цель в список целей пользователя, если её ещё нет в этом списке.
     * @param aim Добавляемая цель.
     * @return true, если добавление цели состоялось успешно (её ещё не было в этом списке), иначе false.
     */
    public boolean addAim(Aim aim) {
        if(!aims.contains(aim) && aim.getAuthor() == this) {
            aims.add(aim);
            return true;
        }
        return false;
    }

    /**
     * Добавляет достижение в список достижений пользователя, если его ещё нет в этом списке.
     * @param achievement Добавляемое достижение.
     * @return true, если добавление достижения состоялось успешно (его ещё не было в этом списке), иначе false.
     */
    public boolean addAchievement(Achievement achievement) {
        if(!achievements.contains(achievement)) {
            achievements.add(achievement);
            return true;
        }
        return false;
    }

    /**
     * Удаляет пользователя из списка друзей пользователя, если он есть в этом списке.
     * @param user Удаляемый из списка друзей пользователь.
     * @return true, если удаление пользователя из списка друзей успешно состоялось (он был в этом списке), иначе false.
     * @throws NullPointerException Возникает, если переданный пользователь представляет собой null.
     */
    public boolean removeFriend(User user) throws NullPointerException {
        user.removeFriend(this);
        return friends.remove(user);
    }

    /**
     * Удаляет пользователя из списка друзей пользователя, по индексу, если индекс находится в пределах размера списка.
     * @param index Индекс удаляемого из списка друзей пользователя.
     * @return true, если удаление пользователя из списка друзей успешно состоялось (индекс находился в пределах размера списка), иначе false.
     */
    public boolean removeFriend(int index) {
        try {
            friends.get(index).removeFriend(this);
            friends.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
    }

    /**
     * Удаляет цель из списка целей пользователя, если она есть в этом списке.
     * @param aim Удаляемая цель.
     * @return true, если удаление цели успешно состоялось (она была в этом списке), иначе false.
     */
    public boolean removeAim(Aim aim) {
        return aims.remove(aim);
    }

    /**
     * Удаляет цель из списка целей пользователя, по индексу, если индекс находится в пределах размера списка.
     * @param index Индекс удаляемой цели.
     * @return true, если удаление цели успешно состоялось (индекс находился в пределах размера списка), иначе false.
     */
    public boolean removeAim(int index) {
        try {
            aims.remove(index);
            return true;
        }
        catch (IndexOutOfBoundsException ex) {
            return false;
        }
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
