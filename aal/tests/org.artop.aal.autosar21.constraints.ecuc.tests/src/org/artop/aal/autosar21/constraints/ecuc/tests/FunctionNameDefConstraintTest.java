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
package org.artop.aal.autosar21.constraints.ecuc.tests;

import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.artop.aal.gautosar.constraints.ecuc.tests.util.ValidationTestUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("nls")
public class FunctionNameDefConstraintTest extends AbstractAutosar21ValidationTestCase {

	public FunctionNameDefConstraintTest() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar21.constraints.ecuc.FunctionNamelDefDefaultValueConstraint_21";//$NON-NLS-1$
	}

	public void testValidStringParamDef_noDefValue() throws Exception {
		EObject validModel = loadInputFile("ecuc/FunctionNameDef/defValueNotSet.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testInvalidStringParamDef_invalidDefValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/FunctionNameDef/defValueInvalid.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.paramDef_defaultValueNoIdentifier, "Function0"));
	}

	public void testValidStringParamDef_validDefValue() throws Exception {
		EObject validModel = loadInputFile("ecuc/FunctionNameDef/defValueSet.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

}
