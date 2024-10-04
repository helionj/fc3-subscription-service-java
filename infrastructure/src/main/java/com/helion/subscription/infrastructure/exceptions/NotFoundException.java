package com.helion.subscription.infrastructure.exceptions;

import com.helion.subscription.domain.exceptions.InternalErrorException;

public class NotFoundException extends InternalErrorException {

    protected NotFoundException(final String aMessage, Throwable t) {
        super(aMessage, t);
    }

    public static NotFoundException with(final String message){
        return new NotFoundException(message, null);
    }
}
