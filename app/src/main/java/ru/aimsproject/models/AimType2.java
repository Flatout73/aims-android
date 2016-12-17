package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.aimsproject.exceptions.IncompatibleAimsDatesException;

/**
 * Created by Антон on 27.10.2016.
 * Представляет цель 2-го типа (с подтверждением не позднее, чем через каждый определённый промежуток времени).
 */
public class AimType2 extends Aim {
    /**
     * Промежуток времени, не позднее, чем через который необходимо подтверждать цель.
     */
    private Date dateSection;

    /**
     * Конструктор, инициализирует объект цели 2-го типа (с подтверждением не позднее, чем через каждый определённый промежуток времени).
     // * @param subAims Список подцелей.
     * @param text Текст цели.
     * @param header Название цели.
     * @param type Тип цели (1 - обычная, 2 - с подтверждением через каждый определённый промежуток времени, 3 - с прогрессом выполнения цели).
     * @param flag Состояние выполнения цели (0 - не начата, 1 - в процессе выполнения, 2 - выполнена, 3 - провалена).
     * @param modif Модификатор доступа к цели (0 - всем, 1 - группе лиц, 2 - друзьям, 3 - себе).
     * @param author Автор цели.
     * @param date Дата публикации цели.
     * @param startDate Дата начала выполнения цели.
     * @param endDate Дата окончания выполнения цели.
     * @param likes Количество лайков цели.
     * @param dislikes Количество дислайков цели.
     * @param dateSection Промежуток времени, не позднее, чем через который, необходимо подтверждать цель.
     */
    public AimType2( /* List<Aim> subAims, */ String text, String header, int type, int flag, int modif, User author, Date date, Date startDate, Date endDate, int likes, int dislikes, Date dateSection)
            throws IncompatibleAimsDatesException {
        super( /* subAims, */ text, header, type, flag, modif, author, date, startDate, endDate, likes, dislikes);
        this.dateSection = dateSection;
    }

    /**
     * Возвращает промежуток времени, не позднее, чем через который необходимо подтверждать цель.
     * @return Промежуток времени, не позднее, чем через который необходимо подтверждать цель.
     */
    public Date getDateSection() {
        return dateSection;
    }
}
