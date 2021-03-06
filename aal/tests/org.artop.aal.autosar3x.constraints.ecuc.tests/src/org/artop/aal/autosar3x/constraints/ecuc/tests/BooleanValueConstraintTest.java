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
package org.artop.aal.autosar3x.constraints.ecuc.tests;

import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.artop.aal.gautosar.constraints.ecuc.tests.util.ValidationTestUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("nls")
public class BooleanValueConstraintTest extends AbstractAutosar3xValidationTestCase {

	public BooleanValueConstraintTest() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar3x.constraints.ecuc.BooleanValueBasicConstraint_3x";//$NON-NLS-1$

	}

	// completeness
	public void testInvalidBooleanValue_noDefinition() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/noDefinition.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.generic_definitionReferenceNotSet);
	}

	public void testInvalidBooleanValue_noValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/noValue.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.generic_valueNotSet);
	}

	public void testInvalidBooleanValue_invalidValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/invalidValue.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.generic_valueNotSet);
	}

	// consistency
	public void testInvalidBooleanValue_wrongParamDefType() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/wrongParamDefType.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.generic_definitionNotOfType, "boolean param def"));
	}

	public void testInvalidBooleanValue_notContainedInDefinitionOfParentContainer() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/notContainedInDefinitionOfParentContainer.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.structuralIntegrity_containmentProblem, "parameter value", "OsAppErrorHook2"));
	}

	public void testInvalidBooleanValue_notAllowedInChoiceContainers() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/BooleanValue/notAllowedInChoiceContainers.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.structuralIntegrity_NotAllowedInChoiceContainer, "parameter value"));
	}

	// correctness

	// valid
	public void testValidBooleanValue() throws Exception {
		EObject validModel = loadInputFile("ecuc/BooleanValue/valid.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

}
