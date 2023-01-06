package dev.luzifer.antiac.api.exceptions;

import dev.luzifer.antiac.api.check.Check;

import java.text.MessageFormat;

public class IllegalClickModificationException extends RuntimeException {

    public IllegalClickModificationException(Check check) {
        super(MessageFormat.format("ICM found during {0}", check.getClass().getSimpleName()));
    }

}
