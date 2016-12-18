package ru.aimsproject.models;

import java.util.Date;

/**
 * Created by Антон on 17.12.2016.
 * Представляет подтверждение выполнения цели.
 */
public abstract class Proof {
    /**
     * Текст подтверждения выполнения цели.
     */
    private String text;

    /**
     * Дата публикации подтверждения выполнения цели.
     */
    private Date date;

    /**
     * Конструктор, инициализирует объект подтверждения выполнения цели.
     * @param text Текст подтверждения выполнения цели.
     * @param date Дата публикации подтверждения выполнения цели.
     */
    public Proof(String text, Date date) {
        this.text = text;
        this.date = date;
    }

    /**
     * Возвращает текст подтверждения выполнения цели.
     * @return Текст подтверждения выполнения цели.
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст подтверждения выполнения цели.
     * @param text Текст подтверждения выполнения цели.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Возвращает дату публикации подтверждения выполнения цели.
     * @return Дата публикации подтверждения выполнения цели.
     */
    public Date getDate() {
        return date;
    }
}
