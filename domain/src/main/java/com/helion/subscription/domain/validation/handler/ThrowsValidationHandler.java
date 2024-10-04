package com.helion.subscription.domain.validation.handler;

import com.helion.subscription.domain.exceptions.DomainException;
import com.helion.subscription.domain.validation.Error;
import com.helion.subscription.domain.validation.ValidationHandler;

import java.util.List;

public class ThrowsValidationHandler implements ValidationHandler {
    @Override
    public ValidationHandler append(final Error anError) {
        throw DomainException.with(anError);
    }

    @Override
    public ValidationHandler append(ValidationHandler anHandler) {
        throw DomainException.with(anHandler.getErrors());
    }

    @Override
    public <T> T validate(final Validation<T> aValidation) {
        try {
            return aValidation.validate();
        } catch (final Exception ex) {
            throw DomainException.with(List.of(new Error(ex.getMessage())));
        }

    }

    @Override
    public List<Error> getErrors() {
        return null;
    }
}
