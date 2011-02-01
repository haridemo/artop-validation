/**
 * <copyright>
 * 
 * Copyright (c) see4Sys and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Artop Software License 
 * Based on Released AUTOSAR Material (ASLR) which accompanies this 
 * distribution, and is available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     see4Sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucparameterdef.GContainerDef;
import gautosar.gecucparameterdef.GModuleDef;

import org.artop.aal.common.resource.AutosarURIFactory;
import org.artop.aal.gautosar.constraints.ecuc.util.EcucUtil;
import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * 
 */
public class GModuleDefContainerDefinitionMissingConstraint extends AbstractModelConstraintWithPrecondition {

	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		return ctx.getTarget() instanceof GModuleDef;
	}

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		GModuleDef moduleDef = (GModuleDef) ctx.getTarget();

		/* Try to get the Refined Module Definition. */
		GModuleDef refinedModuleDef = moduleDef.gGetRefinedModuleDef();

		/*
		 * If the Refined Module Definition is not null, the given target is well a Vendor Specific Module Definition.
		 */
		if (refinedModuleDef != null) {
			/* Container Definitions from the refined side. */
			EList<GContainerDef> refinedContainers = refinedModuleDef.gGetContainers();
			/* Container Definitions from the vendor specific side. */
			EList<GContainerDef> vSpecifContainers = moduleDef.gGetContainers();

			String[] results = EcucUtil.inspectContainersSub(refinedContainers, vSpecifContainers);

			if (results.length > 0) {
				String invalidConfigParameters = ""; //$NON-NLS-1$
				for (String invalid : results) {
					invalidConfigParameters += invalid + ", "; //$NON-NLS-1$
				}
				invalidConfigParameters = invalidConfigParameters.substring(0, invalidConfigParameters.length() - 2);

				return ctx.createFailureStatus(NLS.bind(Messages.moduleDef_containerDefMissing, new Object[] { invalidConfigParameters,
						AutosarURIFactory.getAbsoluteQualifiedName(moduleDef) }));
			}
		} else {
			//
			// Refined Module Definition is null; it means the target is not a Vendor Specific Module Definition.
			// Does nothing more.
			//
		}

		return ctx.createSuccessStatus();
	}

}