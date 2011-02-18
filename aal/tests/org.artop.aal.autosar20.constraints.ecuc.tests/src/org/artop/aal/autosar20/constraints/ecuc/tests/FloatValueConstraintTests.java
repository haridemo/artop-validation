/**
 * <copyright>
 * 
 * Copyright (c) Continental Engineering Services and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     Continental Engineering Services - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.aal.autosar20.constraints.ecuc.tests;

import org.artop.aal.gautosar.constraints.ecuc.tests.util.ValidationTestUtil;
import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

@SuppressWarnings("nls")
public class FloatValueConstraintTests extends AbstractAutosar20ValidationTestCase {

	public FloatValueConstraintTests() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar20.constraints.ecuc.FloatValueBasicConstraint_20";
	}

	public void testInvalidFloatValue_noValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FloatValue/noValue.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, Messages.generic_valueNotSet);
	}

	// test correctness
	public void testInvalidFloatValue_valueLowerThanMin() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FloatValue/valueLowerThanMin.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, Messages.boundary_valueUnderMin);
	}

	public void testInvalidFloatValue_valueBiggerThanMax() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FloatValue/valueBiggerThanMax.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, Messages.boundary_valueAboveMax);
	}
}