package ru.aimsproject.data;

import java.util.ArrayList;
import java.util.List;

import ru.aimsproject.models.*;

/**
 * Представляет хранилище данных приложения.
 * Правда, есть подозрение, что подобным образом реализовать ничего не удастся.
 * Но это не точно.
 * Поэтому надо тут подумать ещё.
 * Created by Антон on 06.11.2016.
 */
public class DataStorage {
    /**
     * Лента ("новостная лента") целей.
     */
    private static List<Aim> newsFeed = new ArrayList<Aim>();

    /**
     * Все доступные в приложении достижения.
     */
    private static List<Achievement> allAchievements = new ArrayList<Achievement>();

    /**
     * Пользователь, залогинившийся в приложении.
     */
    private static User me;

    /**
     * Текущий токен.
     */
    private static String token;

    /**
     * Возвращает ленту ("новостную ленту") целей.
     * @return Лента ("новостная лента") целей.
     */
    public static List<Aim> getNewsFeed() {
        return newsFeed;
    }

    /**
     * Возвращает пользователя, залогинившегося в приложении.
     * @return Пользователь, залогинившийся в приложении.
     */
    public static User getMe() {
        return me;
    }

    /**
     * Устанавливает пользователя, залогинившегося в приложении.
     * @param me Пользователь, залогинившийся в приложении.
     */
    public static void setMe(User me) {
        DataStorage.me = me;
    }

    /**
     * Возвращает текущий токен.
     * @return Текущий токен.
     */
    public static String getToken() {
        return token;
    }

    /**
     * Устанавливает текущий токен.
     * @param token Текущий токен.
     */
    public static void setToken(String token) {
        DataStorage.token = token;
    }

    /**
     * Очищает хранилище данных приложения.
     */
    public static void clear() {
        newsFeed.clear();
        me = null;
        token = null;
    }
}
