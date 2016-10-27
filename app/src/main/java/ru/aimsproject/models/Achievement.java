package ru.aimsproject.models;

/**
 * Created by Антон on 27.10.2016.
 * Представляет достижение.
 */
public class Achievement {
    /**
     * Название достижения.
     */
    private String name;

    /**
     * Описание достижения.
     */
    private String description;

    /**
     * Конструктор, инициализирует объект достижения.
     * @param name Название достижения.
     * @param description Описание достижения.
     */
    public Achievement(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Возвращает название достижения.
     * @return Название достижения.
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает описание достижения.
     * @return Описание достижения.
     */
    public String getDescription() {
        return description;
    }
}
