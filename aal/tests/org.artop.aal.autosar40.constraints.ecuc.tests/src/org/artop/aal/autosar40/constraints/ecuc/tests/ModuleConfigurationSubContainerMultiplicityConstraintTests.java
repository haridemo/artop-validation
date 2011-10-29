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

import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.artop.aal.gautosar.constraints.ecuc.tests.util.ValidationTestUtil;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.osgi.util.NLS;

@SuppressWarnings("nls")
public class ModuleConfigurationSubContainerMultiplicityConstraintTests extends AbstractAutosar40ValidationTestCase {

	public ModuleConfigurationSubContainerMultiplicityConstraintTests() {
		super();
	}

	@Override
	protected String getConstraintID() {
		return "org.artop.aal.autosar40.constraints.ecuc.ModuleConfigurationSubContainerMultiplicityConstraint_40";//$NON-NLS-1$
	}

	public void testInvalidModuleConfiguration_upperMultiplicityOfSubContainerViolated() throws Exception {
		EObject invalidModel = loadInputFile("ecuc/ModuleConfiguration/upperMultiplicityOfSubContainerViolated.arxml");
		ValidationTestUtil.validateModel(invalidModel, validator, IStatus.ERROR,
				NLS.bind(EcucConstraintMessages.multiplicity_maxElementsExpected, new String[] { "1", "subcontainers", "/AUTOSAR/Os/OsAlarm", "2" }));
	}

	// valid
	public void testValidSubContainer_MultipleConfigurationContainer() throws Exception {
		EObject validModel = loadInputFile("ecuc/Container/upperMultiplicityOfMultipleConfigSubContainerValid.arxml");
		ValidationTestUtil.validateModel(validModel, validator, IStatus.OK);
	}
}
