package ru.aimsproject.data;

import java.util.ArrayList;
import java.util.List;

import ru.aimsproject.models.*;

/**
 * Представляет хранилище данных приложения.
 * Created by Антон on 06.11.2016.
 */
public class DataStorage {
    /**
     * Лента ("новостная лента") целей.
     */
    private List<Aim> aimsFeed = new ArrayList<Aim>();

    /**
     * Все доступные в приложении достижения.
     */
    private List<Achievement> allAchievements = new ArrayList<Achievement>();

    /**
     * Пользователь, залогинившийся в приложении.
     */
    private User me;

    // Coming soon...
}
