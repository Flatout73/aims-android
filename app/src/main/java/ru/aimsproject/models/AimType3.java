package ru.aimsproject.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Антон on 27.10.2016.
 * Представляет цель 3-го типа (с прогрессом выполнения цели).
 */

public class AimType3 extends Aim {
    /**
     * Общее количество однотипных задач, которые необходимо решить для выполнения цели.
     */
    private int allTasks;

    /**
     * Текущее количество решённых однотипных задач, необходимых для выполнения цели.
     */
    private int currentTasks;

    /**
     * Конструктор, инициализирует объект цели 3-го типа (с прогрессом выполнения цели).
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
     * @param allTasks Общее количество однотипных задач, которые необходимо решить для выполнения цели.
     * @param currentTasks Текущее количество решённых однотипных задач, необходимых для выполнения цели.
     */
    public AimType3(ArrayList<Aim> subAims, String text, String header, int type, int flag, int modif, User author, Date date, Date startDate, Date endDate, int allTasks, int currentTasks) {
        super(subAims, text, header, type, flag, modif, author, date, startDate, endDate);
        this.allTasks = allTasks;
        this.currentTasks = currentTasks;
    }

    /**
     * Возвращает общее количество однотипных задач, которые необходимо решить для выполнения цели.
     * @return Общее количество однотипных задач, которые необходимо решить для выполнения цели.
     */
    public int getAllTasks() {
        return allTasks;
    }

    /**
     * Возвращает текущее количество решённых однотипных задач, необходимых для выполнения цели.
     * @return Текущее количество решённых однотипных задач, необходимых для выполнения цели.
     */
    public int getCurrentTasks() {
        return currentTasks;
    }
}
