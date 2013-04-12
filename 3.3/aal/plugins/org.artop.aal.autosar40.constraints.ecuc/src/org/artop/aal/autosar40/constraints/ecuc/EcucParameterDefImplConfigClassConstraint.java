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

import gautosar.gecucparameterdef.GConfigParameter;

import org.artop.aal.gautosar.constraints.ecuc.AbstractGConfigParameterImplConfigClassConstraint;
import org.eclipse.emf.common.util.EList;

import autosar40.ecucparameterdef.EcucImplementationConfigurationClass;
import autosar40.ecucparameterdef.EcucParameterDef;

/**
 * 
 */
public class EcucParameterDefImplConfigClassConstraint extends AbstractGConfigParameterImplConfigClassConstraint {

	@Override
	protected boolean compareImplConfigurationClass(GConfigParameter cfParam1, GConfigParameter cfParam2) {
		boolean valid = true;
		EcucParameterDef paramdef1 = (EcucParameterDef) cfParam1;
		EcucParameterDef paramdef2 = (EcucParameterDef) cfParam2;

		/*
		 * List of ImplConfigClass of the Parameter Definition 1
		 */
		EList<EcucImplementationConfigurationClass> configClassList1 = paramdef1.getImplementationConfigClass();

		/*
		 * List of ImplConfigClass of the Parameter Definition 2
		 */
		EList<EcucImplementationConfigurationClass> configClassList2 = paramdef2.getImplementationConfigClass();

		/*
		 * A warning is raised if list of ImplConfigClass of ParamDef1 is different with ParamDef2's
		 */
		if (configClassList1 == null) {
			valid = configClassList2 == null;
		} else {
			valid = configClassList1.size() == configClassList2.size();
			if (valid) {
				for (int i = 0; i < configClassList1.size(); i++) {
					EcucImplementationConfigurationClass cfClass1 = configClassList1.get(i);
					EcucImplementationConfigurationClass cfClass2 = configClassList2.get(i);
					if (cfClass1 == null) {
						valid = cfClass2 == null;
					} else {
						valid = cfClass1.equals(cfClass2);
					}
					if (valid == false) {
						break;
					}
				}
			}
		}

		return valid;
	}

}
