package ru.aimsproject.exceptions;

import ru.aimsproject.models.Aim;
import ru.aimsproject.models.User;

/**
 * Представляет исключение, возникающее при несовместимости состояний выполнения надцели и подцели.
 * Created by Антон on 06.11.2016.
 */
public class IncompatibleSubAndSuperAimsFlagsException extends Exception {
    /**
     * Подцель, ставшая причиной возникновения исключения.
     */
    private Aim aimCause;

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
    public IncompatibleSubAndSuperAimsFlagsException(String detailMessage, Aim aimCause, Aim superAimCause) {
        super(detailMessage);
        this.aimCause = aimCause;
        this.superAimCause = superAimCause;
    }

    /**
     * Возвращает подцель, ставшую причиной возникновения исключения.
     * @return Подцель, ставшая причиной возникновения исключения.
     */
    public Aim getAimCause() {
        return aimCause;
    }

    /**
     * Возвращает надцель, ставшая причиной возникновения исключения.
     * @return Надцель, ставшая причиной возникновения исключения.
     */
    public Aim getSuperAimCause() {
        return superAimCause;
    }
}
