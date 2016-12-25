package ru.aimsproject.models;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Антон on 24.12.2016.
 * Представляет комментарий к цели.
 */
public class Comment {
    /**
     * Текст комментария к цели.
     */
    private String text;

    /**
     * Логин автора комментария к цели.
     */
    private String authorLogin;

    /**
     * Имя автора комментария к цели.
     */
    private String authorName;

    /**
     * Сжатая аватарка автора комментария к цели (изображение, сохранённое в виде Bitmap).
     */
    private Bitmap authorPhoto;

    /**
     * Дата публикации комментария к цели.
     */
    private Date date;

    /**
     * Конструктор, инициализирует объект комментария к цели.
     * @param text Текст комментария к цели.
     * @param authorLogin Логин автора комментария к цели.
     * @param authorName Имя автора комментария к цели.
     * @param authorPhoto Аватарка автора комментария к цели (изображение, сохранённое в виде Bitmap).
     * @param date Дата публикации комментария к цели.
     */
    public Comment(String text, String authorLogin, String authorName, Bitmap authorPhoto, Date date) {
        this.text = text;
        this.authorLogin = authorLogin;
        this.authorName = authorName;
        this.authorPhoto = authorPhoto;
        this.date = date;
    }

    /**
     * Возвращает текст комментария к цели.
     * @return Текст комментария к цели.
     */
    public String getText() {
        return text;
    }

    /**
     * Устанавливает текст комментария к цели.
     * @param text Текст комментария к цели.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Возвращает логин автора комментария к цели.
     * @return Логин автора комментария к цели.
     */
    public String getAuthorLogin() {
        return authorLogin;
    }

    /**
     * Возвращает имя автора комментария к цели.
     * @return Имя автора комментария к цели.
     */
    public String getAuthorName() {
        return authorName;
    }

    /**
     * Возвращает сжатую аватарку автора комментария к цели (изображение, сохранённое в виде Bitmap).
     * @return Сжатая аватарка автора комментария к цели (изображение, сохранённое в виде Bitmap).
     */
    public Bitmap getAuthorPhoto() {
        return authorPhoto;
    }

    /**
     * Возвращает дату публикации комментария к цели.
     * @return Дата публикации комментария к цели.
     */
    public Date getDate() {
        return date;
    }
}
