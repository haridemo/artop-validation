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
package org.artop.aal.autosar3x.constraints.ecuc;

import gautosar.gecucdescription.GFloatValue;
import gautosar.gecucdescription.GParameterValue;
import gautosar.gecucparameterdef.GFloatParamDef;

import org.artop.aal.gautosar.constraints.ecuc.GFloatValueBasicConstraint;
import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

import autosar3x.ecucdescription.FloatValue;
import autosar3x.ecucparameterdef.FloatParamDef;

public class FloatValueBasicConstraint extends GFloatValueBasicConstraint {

	@Override
	protected IStatus validateBoundary(IValidationContext ctx, GFloatValue gFloatValue) {
		IStatus status = ctx.createSuccessStatus();
		GFloatParamDef gFloatParamDef = (GFloatParamDef) gFloatValue.gGetDefinition();
		FloatParamDef definition = (FloatParamDef) gFloatParamDef;
		Double value = gFloatValue.gGetValue();
		if (value != null) {

			// min limit
			if (true == definition.isSetMin()) {
				Double minLimit = definition.getMin();
				if (value.compareTo(minLimit) < 0) {
					status = ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.boundary_valueUnderMin, new Object[] { value, minLimit }));
				}
			}

			// max limit
			if (true == definition.isSetMax()) {
				Double maxLimit = definition.getMax();
				if (value.compareTo(maxLimit) > 0) {
					status = ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.boundary_valueAboveMax, new Object[] { value, maxLimit }));
				}
			}
		}
		return status;
	}

	@Override
	protected boolean isValueSet(IValidationContext ctx, GParameterValue gParameterValue) {
		FloatValue value = (FloatValue) gParameterValue;
		return value.isSetValue();
	}

}
