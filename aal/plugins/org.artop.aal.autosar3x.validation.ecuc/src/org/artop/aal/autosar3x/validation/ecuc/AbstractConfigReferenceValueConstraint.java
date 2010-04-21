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

import autosar3x.ecucdescription.ConfigReferenceValue;
import autosar3x.ecucdescription.EcucdescriptionPackage;
import autosar3x.ecucparameterdef.ConfigReference;

public abstract class AbstractConfigReferenceValueConstraint extends AbstractModelConstraint {

	protected IStatus validateDefinitionRef(IValidationContext ctx, ConfigReferenceValue configReferenceValue) {
		// check if definition is set and available
		final IStatus status;
		if (false == configReferenceValue.eIsSet(EcucdescriptionPackage.eINSTANCE.getParameterValue_Definition())) {
			status = ctx.createFailureStatus("definition reference not set");
		} else if (configReferenceValue.getDefinition().eIsProxy()) {
			status = ctx.createFailureStatus("reference to definition could not be resolved");
		} else {
			status = ctx.createSuccessStatus();
		}
		return status;
	}

	protected IStatus validateConfigReferenceValue(IValidationContext ctx, ConfigReferenceValue configReferenceValue, ConfigReference configReference) {
		assert false; // we should never
		return ctx.createSuccessStatus();
	}

}
