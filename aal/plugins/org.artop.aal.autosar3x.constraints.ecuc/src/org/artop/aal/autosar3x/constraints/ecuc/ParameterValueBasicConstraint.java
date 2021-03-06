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
 * 
 * </copyright>
 */
package org.artop.aal.autosar3x.constraints.ecuc;

import gautosar.gecucdescription.GParameterValue;

import org.artop.aal.gautosar.constraints.ecuc.AbstractGParameterValueBasicConstraint;

import autosar3x.ecucdescription.BooleanValue;
import autosar3x.ecucdescription.EnumerationValue;
import autosar3x.ecucdescription.FloatValue;
import autosar3x.ecucdescription.IntegerValue;
import autosar3x.ecucdescription.StringValue;

public class ParameterValueBasicConstraint extends AbstractGParameterValueBasicConstraint {

	@Override
	protected boolean isSetValue(GParameterValue parameterValue) {
		if (parameterValue instanceof BooleanValue) {
			return ((BooleanValue) parameterValue).isSetValue();
		} else if (parameterValue instanceof IntegerValue) {
			return ((IntegerValue) parameterValue).isSetValue();
		} else if (parameterValue instanceof FloatValue) {
			return ((FloatValue) parameterValue).isSetValue();
		} else if (parameterValue instanceof EnumerationValue) {
			return ((EnumerationValue) parameterValue).isSetValue();
		} else if (parameterValue instanceof StringValue) {
			return ((StringValue) parameterValue).isSetValue() && ((StringValue) parameterValue).getValue() != null;
		}
		return true;
	}

}
