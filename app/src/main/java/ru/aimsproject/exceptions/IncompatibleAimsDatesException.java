package ru.aimsproject.exceptions;

import ru.aimsproject.models.Aim;
import ru.aimsproject.models.User;

/**
 * Представляет исключение, возникающее при несовместимости дат цели (целей).
 * Created by Антон on 06.11.2016.
 */
public class IncompatibleAimsDatesException extends Exception {
    /**
     * Цель, ставшая причиной возникновения исключения.
     */
    private Aim aimCause;

    /**
     * Конструктор, инициализирует объект исключения.
     * @param detailMessage Сообщение о причине возникновения исключения.
     * @param aimCause Цель, ставшая причиной возникновения исключения.
     */
    public IncompatibleAimsDatesException(String detailMessage, Aim aimCause) {
        super(detailMessage);
        this.aimCause = aimCause;
    }

    /**
     * Возвращает цель, ставшую причиной возникновения исключения.
     * @return Цель, ставшая причиной возникновения исключения.
     */
    public Aim getAimCause() {
        return aimCause;
    }
}
