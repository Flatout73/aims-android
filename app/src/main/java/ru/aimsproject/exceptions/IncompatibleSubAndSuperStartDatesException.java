package ru.aimsproject.exceptions;

import ru.aimsproject.models.Aim;
import ru.aimsproject.models.User;

/**
 * Представляет исключение, возникающее при несовместимости дат окончания выполнения надцели и подцели.
 * Created by Антон on 06.11.2016.
 */
public class IncompatibleSubAndSuperStartDatesException extends IncompatibleAimsDatesException {
    /**
     * Надцель, ставшая причиной возникновения исключения.
     */
    private Aim superAimCause;

    /**
     * Конструктор, инициализирует объект исключения.
     * @param detailMessage Сообщение о причине возникновения исключения.
     * @param aimCause Подцель, ставшая причиной возникновения исключения.
     * @param superAimCause Надцель, ставшая причиной возникновения исключения.
     */
    public IncompatibleSubAndSuperStartDatesException(String detailMessage, Aim aimCause, Aim superAimCause) {
        super(detailMessage, aimCause);
        this.superAimCause = superAimCause;
    }

    /**
     * Возвращает надцель, ставшую причиной возникновения исключения.
     * @return Надцель, ставшая причиной возникновения исключения.
     */
    public Aim getSuperAimCause() {
        return superAimCause;
    }
}
