package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Антон on 27.10.2016.
 * Представляет цель 1-го типа (обычную).
 */
public class AimType1 extends Aim {
    /**
     * Конструктор, инициализирует объект цели 1-го типа (обычную).
     * @param subAims Список подцелей.
     * @param text Текст цели.
     * @param header Название цели.
     * @param type Тип цели (1 - обычная, 2 - с подтверждением через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @param flag Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @param modif Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param author Автор цели.
     * @param date Дата публикации цели.
     * @param startDate Дата начала выполнения цели.
     * @param endDate Дата окончания выполнения цели.
     */
    public AimType1(ArrayList<Aim> subAims, String text, String header, int type, int flag, int modif, User author, Date date, Date startDate, Date endDate) {
        super(subAims, text, header, type, flag, modif, author, date, startDate, endDate);
    }
}
