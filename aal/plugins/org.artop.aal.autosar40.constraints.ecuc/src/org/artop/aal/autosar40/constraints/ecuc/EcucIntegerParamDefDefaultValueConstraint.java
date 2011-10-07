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
package org.artop.aal.autosar40.constraints.ecuc;

import gautosar.gecucparameterdef.GIntegerParamDef;

import java.math.BigInteger;

import org.artop.aal.autosar40.constraints.ecuc.util.EcucUtil40;
import org.artop.aal.gautosar.constraints.ecuc.AbstractGIntegerParamDefDefaultValueConstraint;

public class EcucIntegerParamDefDefaultValueConstraint extends AbstractGIntegerParamDefDefaultValueConstraint {

	@Override
	protected BigInteger getMin(GIntegerParamDef integerParamDef) {
		return (BigInteger) EcucUtil40.getMin(integerParamDef);
	}

	@Override
	protected BigInteger getMax(GIntegerParamDef integerParamDef) {
		return (BigInteger) EcucUtil40.getMax(integerParamDef);
	}

	@Override
	protected BigInteger getDefaultValue(GIntegerParamDef integerParamDef) {
		try {
			return new BigInteger(EcucUtil40.getDefaultValue(integerParamDef));
		} catch (NumberFormatException ex) {
		}
		return null;
	}

	@Override
	protected boolean isDefaultValueSet(GIntegerParamDef integerParamDef) {
		return getDefaultValue(integerParamDef) != null;
	}

}
