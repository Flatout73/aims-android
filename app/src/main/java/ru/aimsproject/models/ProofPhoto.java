package ru.aimsproject.models;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by Антон on 17.12.2016.
 * Представляет подтверждение выполнения цели в виде фотографии.
 */
public class ProofPhoto extends Proof {
    /**
     * Фотография подтверждения выполнения цели.
     */
    private Bitmap photo;

    /**
     * Конструктор, инициализирует объект подтверждения выполнения цели в виде фотографии.
     * @param text Текст подтверждения выполнения цели.
     * @param date Дата публикации подтверждения выполнения цели.
     * @param photo Фотография подтверждения выполнения цели.
     */
    public ProofPhoto(String text, Date date, Bitmap photo) {
        super(text, date);
        this.photo = photo;
    }

    /**
     * Возвращает фотографию подтверждения выполнения цели.
     * @return Фотография подтверждения выполнения цели.
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Устанавливает фотографию подтверждения выполнения цели.
     * @param photo Фотография подтверждения выполнения цели.
     */
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
