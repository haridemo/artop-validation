/**
 * <copyright>
 *
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     Continental AG - Mark class as Splitable aware.
 * </copyright>
 */
package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucdescription.GParameterValue;

import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.artop.aal.validation.constraints.AbstractSplitModelConstraintWithPrecondition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * Abstract superclass for the constraints implementations on a parameter value.
 */
public abstract class AbstractGParameterValueBasicConstraint extends AbstractSplitModelConstraintWithPrecondition {

	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		return ctx.getTarget() instanceof GParameterValue;
	}

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		IStatus status = ctx.createSuccessStatus();
		GParameterValue parameterValue = (GParameterValue) ctx.getTarget();

		if (parameterValue.gGetDefinition() == null) {
			return status;
		}

		if (!isSetValue(parameterValue)) {
			return ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.parameterValue_valueNotSet, parameterValue.gGetDefinition()
					.gGetShortName()));
		}

		return status;
	}

	/**
	 * Return <code>true</code> if value is set, otherwise <code>false</code>
	 *
	 * @param parameterValue
	 *            The GParameterValue
	 * @return <code>true</code> if value is set, otherwise <code>false</code>
	 */
	protected abstract boolean isSetValue(GParameterValue parameterValue);

}
