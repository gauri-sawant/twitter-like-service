package com.twitter.dto;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

public abstract class BuilderConstraints<T> {
	private static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	public T build() {
		T targetObject = getTargetObject();
		Set<ConstraintViolation<T>> violations = validator.validate(targetObject);
		if (violations.isEmpty()) {
			return targetObject;
		}
		throw new ConstraintViolationException((violations.iterator().next()).getMessage(), new HashSet<>(violations));

	}

	public abstract T getTargetObject();
}
