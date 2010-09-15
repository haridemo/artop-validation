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
package org.artop.aal.autosar40.constraints.ecuc.tests;

import org.artop.aal.gautosar.constraints.ecuc.tests.util.ValidationTestUtil;
import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;

public class FunctionNameValueConstraintTests extends AbstractAutosar40ValidationTestCase
{

	public FunctionNameValueConstraintTests() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar40.constraints.ecuc.EcucTextualParamValueBasicConstraint_40";//$NON-NLS-1$
	}

	// completeness
	public void testInvalidFunctionNameValue_noValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FunctionNameValue/emptyValue.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,Messages.generic_valueNotSet);
	}
	
	public void testInvalidFunctionNameValue_valueNoIdentifier() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FunctionNameValue/valueNoIdentifier.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,Messages.string_valueNoIdentifier);
	}

	// should be reported by LinkerSymbolConstraint and not reported again
	public void testInvalidFunctionNameValue_valueTooLong() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FunctionNameValue/valueTooLong.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,Messages.string_valueTooBig);
	}

	public void testValidFunctionNameValue() throws Exception {
		EObject validModel = loadInputFile("ecuc/FunctionNameValue/valid.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}
}
