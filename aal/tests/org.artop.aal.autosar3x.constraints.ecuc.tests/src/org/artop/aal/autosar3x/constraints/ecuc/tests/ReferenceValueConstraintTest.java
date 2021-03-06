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
public class ReferenceValueConstraintTest extends AbstractAutosar3xValidationTestCase {

	public ReferenceValueConstraintTest() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar3x.constraints.ecuc.ReferenceValueBasicConstraint_3x";//$NON-NLS-1$
	}

	// test completeness

	public void testInvalidReferenceValue_noDefinition() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/noDefinition.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.generic_definitionReferenceNotSet);
	}

	public void testInvalidReferenceValue_noValue() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/noValue.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.generic_valueNotSet);
	}

	// test consistency
	public void testInvalidReferenceValue_defTypeInvalid() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/defTypeInvalid.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR);
	}

	// test correctness
	public void testInvalidReferenceValue_choiceReferenceParamDefnotContainer() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/choiceReferenceParamDefNotContainer.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.reference_valueNotOfType, "container"));
	}

	public void testInvalidReferenceValue_choiceReferenceParamDefNoDest() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/choiceReferenceParamDefNoDest.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.generic_validationNotPossible, "/AUTOSAR/Os/OsApplication/OsAppScheduleTableRef"));
	}

	public void testInvalidReferenceValue_choiceReferenceParamDefDifferentDef() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/choiceReferenceParamDefDifferentDef.arxml");
		ValidationTestUtil.validateModel(
				invalidModel,
				validator,
				IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.choiceref_containerNotInTheDest, new Object[] { "OS_OsScheduleTable", "/AUTOSAR/Os/OsScheduleTable",
						"/AUTOSAR/Os/OsScheduleTable_1, /AUTOSAR/Os/OsScheduleTable_2" }));
	}

	public void testInvalidReferenceValue_referenceParamDefnotContainer() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/referenceParamDefNotContainer.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.reference_valueNotOfType, "container"));
	}

	public void testInvalidReferenceValue_referenceParamDefNoDest() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/referenceParamDefNoDest.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.generic_validationNotPossible, "/AUTOSAR/Os/unresolvableReference"));
	}

	public void testInvalidReferenceValue_referenceParamDefContainerNotInDest() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/referenceParamDefContainerNotInDest.arxml");
		ValidationTestUtil
				.validateModel(invalidModel, validator, IStatus.ERROR, NLS.bind(EcucConstraintMessages.reference_valueDefinitionNotSet,
						"ar:/#/AUTOSAR/Os/unresolvableReference?type=ParamConfContainerDef"));
	}

	public void testInvalidReferenceValue_referenceParamDefDifferentDef() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/referenceParamDefDifferentDef.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.reference_differentDefAndDestination, "OsResource", "OsScheduleTable"));
	}

	public void testInvalidReferenceValue_noForeignDestinationType() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/noForeignDestinationType.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR, EcucConstraintMessages.reference_targetDestinationTypeNotAvailable);
	}

	public void testInvalidReferenceValue_valueTypeNotMatchWithForeignDestination() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/valueTypeNotMatchWithForeignDestination.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.reference_valueNotInstanceOfDestType, "Container"));
	}

	public void testInvalidReferenceValue_notContainedInDefinitionOfParentContainer() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/notContainedInDefinitionOfParentContainer.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.structuralIntegrity_containmentProblem, "reference value", "OsAppScheduleTableRef2"));
	}

	public void testInvalidReferenceValue_notAllowedInChoiceContainers() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ReferenceValue/notAllowedInChoiceContainers.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.structuralIntegrity_NotAllowedInChoiceContainer, "reference value"));
	}

	// test valid
	public void testValidReferenceValue() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/valid.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validChoiceReferenceParamDef() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validChoiceReferenceParamDef.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validVendorSpecificChoiceReferenceParamDef() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validVendorSpecificChoiceReferenceParamDef.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validReferenceParamDef() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validReferenceParamDef.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validVendorSpecificReferenceParamDef() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validVendorSpecificReferenceParamDef.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validForeignReferenceParamDef() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validForeignReferenceParamDef.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	public void testValidReferenceValue_validForeignReferenceParamDefValueWithSuperTypes() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/validForeignReferenceParamDefValueWithSuperTypes.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	/**
	 * Checks that a reference value with a choice container definition, is not validated with respect to the possible
	 * destinations, if the referred container does not exist. <br>
	 * In such a case, container's definition cannot be retrieved, thus it does not make sense to validate the reference
	 * value with respect to the choice container def's destinations. <br>
	 * <b>NOTE:</b> This check should be updated when the native EMF constraint that checks for unresolved references
	 * will be in place and the test will fail
	 * 
	 * @throws Exception
	 */
	public void testInvalidRefereceValue_valueNotResolved() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/referenceValue_ValueRefNotResolved.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}

	/**
	 * Checks that a proper validation error is received if a reference value with unresolved definition is validated
	 */
	public void testInvalidReferenceValue_definitionOfReferredContainerNotResolved() throws Exception {
		EObject validModel = loadInputFile("ecuc/ReferenceValue/referenceValue_DefinitionOfValueRefNotResolved.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.generic_validationNotPossible, "/AUTOSAR/myVendor_Module/TargetContainer"));
	}
}
