/**
 * <copyright>
 * 
 * Copyright (c) OpenSynergy and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     OpenSynergy - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.aal.autosar3x.validation.ecuc;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.AbstractModelConstraint;
import org.eclipse.emf.validation.IValidationContext;

import autosar3x.ecucdescription.EcucdescriptionPackage;
import autosar3x.ecucdescription.ParameterValue;

public abstract class AbstractParameterValueConstraint extends AbstractModelConstraint {

	protected IStatus validateDefinitionRef(IValidationContext ctx, ParameterValue parameterValue) {
		// check if definition is set and available
		final IStatus status;
		if (false == parameterValue.eIsSet(EcucdescriptionPackage.eINSTANCE.getParameterValue_Definition())) {
			status = ctx.createFailureStatus("definition reference not set");
		} else if (parameterValue.getDefinition().eIsProxy()) {
			status = ctx.createFailureStatus("reference to definition could not be resolved");
		} else {
			status = ctx.createSuccessStatus();
		}
		return status;
	}

}
