/**
 * <copyright>
 * 
 * Copyright (c) OpenSynergy, Continental Engineering Services and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     OpenSynergy - Initial API and implementation for AUTOSAR 3.x
 *     Continental Engineering Services - migration to gautosar
 * 
 * </copyright>
 */
package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucdescription.GParameterValue;
import gautosar.gecucdescription.GStringValue;
import gautosar.gecucparameterdef.GStringParamDef;

import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract superclass for the constraints implementations on a string value.
 */
public abstract class AbstractGStringValueBasicConstraint extends AbstractGParameterValueConstraint {
	final String STRING_PATTERN = "[a-zA-Z]([a-zA-Z0-9_])*"; //$NON-NLS-1$

	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		// return
		// GecucdescriptionPackage.eINSTANCE.getGStringValue().equals(ctx.getTarget().eClass());
		return ctx.getTarget() instanceof GStringValue;
	}

	@Override
	public IStatus doValidate(IValidationContext ctx) {
		GStringValue stringValue = (GStringValue) ctx.getTarget();
		IStatus status = validateDefinition(ctx, stringValue);
		if (status.isOK()) {
			status = validateValue(ctx, stringValue);
		}
		return status;
	}

	/**
	 * Performs the validation on the definition of the given <code>gParameterValue</code>. It checks if the value is
	 * set and that the value's length is between the allowed limits.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current constraint evaluation environment
	 * @param gParameterValue
	 *            the element on which the validation is performed.
	 * @return a status object describing the result of the validation.
	 */
	protected IStatus validateDefinition(IValidationContext ctx, GParameterValue gParameterValue) {
		// check if definition is set and available
		IStatus status = super.validateDefinitionRef(ctx, gParameterValue);
		if (status.isOK()) {
			if (!(gParameterValue.gGetDefinition() instanceof GStringParamDef)) {
				status = ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.generic_definitionNotOfType, "string param def")); //$NON-NLS-1$
			}
		}
		return status;
	}

	/**
	 * Performs the validation on the value of the given <code>gStringValue</code>.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current constraint evaluation environment
	 * @param gStringValue
	 *            the element on which the validation is performed.
	 * @return a status object describing the result of the validation.
	 */
	protected IStatus validateValue(IValidationContext ctx, GStringValue gStringValue) {
		String value = gStringValue.gGetValue();
		IStatus status = validateValueSet(ctx, gStringValue, value);
		if (!status.isOK()) {
			return status;
		}

		// Note: as String is used directly in C code, verify common constraint is necessary
		// check that value length is between 1 and 255 characters
		if (0 == value.length()) {
			return ctx.createFailureStatus(EcucConstraintMessages.generic_emptyValue);
		} else if (255 < value.length()) {
			return ctx.createFailureStatus(EcucConstraintMessages.string_valueTooBig);
		} else if (false == value.matches(STRING_PATTERN)) {
			return ctx.createFailureStatus(EcucConstraintMessages.string_valueNoIdentifier);
		}

		return ctx.createSuccessStatus();
	}

}
