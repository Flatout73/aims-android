package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Антон on 27.10.2016.
 * Представляет цель. Абстрактный класс.
 */
public abstract class Aim implements Comparable<Aim> {
    /**
     * Список подцелей.
     */
    private ArrayList<Aim> subAims;

    /**
     * Текст цели.
     */
    private String text;

    /**
     * Название цели.
     */
    private String header;

    /**
     * Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     */
    private int type;

    /**
     * Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     */
    private int flag;

    /**
     * Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     */
    private int modif;

    /**
     * Автор цели.
     */
    private User author;

    /**
     * Дата публикации цели.
     */
    private Date date;

    /**
     * Дата начала выполнения цели.
     */
    private Date startDate;

    /**
     * Дата окончания выполнения цели.
     */
    private Date endDate;

    /**
     * Конструктор, инициализирует объект цели.
     * @param subAims Список подцелей.
     * @param text Текст цели.
     * @param header Название цели.
     * @param type Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @param flag Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @param modif Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param author Автор цели.
     * @param date Дата публикации цели.
     * @param startDate Дата начала выполнения цели.
     * @param endDate Дата окончания выполнения цели.
     */
    public Aim(ArrayList<Aim> subAims, String text, String header, int type, int flag, int modif, User author, Date date, Date startDate, Date endDate) {
        this.subAims = subAims;
        this.text = text;
        this.header = header;
        this.type = type;
        this.flag = flag;
        this.modif = modif;
        this.author = author;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Возвращает список поцелей.
     * @return Список подцелей.
     */
    public ArrayList<Aim> getSubAims() {
        return subAims;
    }

    /**
     * Возвращает текст цели.
     * @return Текст цели.
     */
    public String getText() {
        return text;
    }

    /**
     * Возвращает название цели.
     * @return Название цели.
     */
    public String getHeader() {
        return header;
    }

    /**
     * Возвращает тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @return Тип цели (1 - обычная, 2 - с подтверждением не позднее, чем через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     */
    public int getType() {
        return type;
    }

    /**
     * Возвращает состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @return Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     */
    public int getFlag() {
        return flag;
    }

    /**
     * Возвращает модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @return Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     */
    public int getModif() {
        return modif;
    }

    /**
     * Возвращает дату публикации цели.
     * @return Дата публикации цели.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Возвращает дату начала выполнения цели.
     * @return Дата начала выполнения цели.
     */
    public Date getStartDate() {
        return startDate;
    }

    /**
     * Возвращает дату окончания выполнения цели.
     * @return Дата окончания выполнения цели.
     */
    public Date getEndDate() {
        return endDate;
    }

    /**
     * Сравнивает цель с другой для сортировки по убыванию дат публикации целей.
     * @param other Другая цель.
     * @return Значение для метода сортировки по убыванию дат публикации целей.
     */
    @Override
    public int compareTo(Aim other) {
        return -date.compareTo(other.getDate());
    }
}
