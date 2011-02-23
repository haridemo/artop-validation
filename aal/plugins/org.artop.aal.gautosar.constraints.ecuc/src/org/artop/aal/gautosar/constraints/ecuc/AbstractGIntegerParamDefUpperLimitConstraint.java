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

import gautosar.gecucparameterdef.GConfigParameter;
import gautosar.gecucparameterdef.GIntegerParamDef;
import gautosar.gecucparameterdef.GParamConfContainerDef;

import java.math.BigInteger;

import org.artop.aal.common.resource.AutosarURIFactory;
import org.artop.aal.gautosar.constraints.ecuc.util.EcucUtil;
import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * Superclass for the constraints implementations on a IntegerParamDef upper limit.
 */
public abstract class AbstractGIntegerParamDefUpperLimitConstraint extends AbstractModelConstraintWithPrecondition {

	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		return ctx.getTarget() instanceof GIntegerParamDef;
	}

	@Override
	protected IStatus doValidate(IValidationContext ctx) {
		IStatus status = ctx.createSuccessStatus();

		GIntegerParamDef integerParamDef = (GIntegerParamDef) ctx.getTarget();
		/*
		 * The corresponding Integer Parameter Definition from the Refined Module Definition
		 */
		GConfigParameter refinedIntegerParamDef = EcucUtil.getFromRefined(integerParamDef);

		if (refinedIntegerParamDef != null) {
			/* Flag used to mark the upper limit as valid or not. */
			boolean valid = true;

			/*
			 * Upper limit of the Integer Parameter Definition in the Refined Module Definition.
			 */
			BigInteger refinedMaxLimit = getMax(refinedIntegerParamDef);
			if (isSetMax(refinedIntegerParamDef)) {
				/*
				 * Upper limit of the Integer Parameter Definition in the Vendor Specific Module Definition.
				 */
				BigInteger vSpecifMinLimit = getMax(integerParamDef);

				/*
				 * A warning is raised if Upper limit has been modified in the Vendor Specific ModuleDef.
				 */
				valid = vSpecifMinLimit.equals(refinedMaxLimit);
			}

			if (!valid) {
				GParamConfContainerDef parent = (GParamConfContainerDef) refinedIntegerParamDef.eContainer();
				EObject refineModuleDef = EcucUtil.getParentModuleDefForContainerDef(parent);

				return ctx.createFailureStatus(NLS.bind(Messages.integerParamDef_upperLimitChanged, new Object[] {
						AutosarURIFactory.getAbsoluteQualifiedName(integerParamDef), AutosarURIFactory.getAbsoluteQualifiedName(refineModuleDef) }));
			}
		} else {
			//
			// Refined Integer Parameter Definition is null.
			// Does nothing more.
			//
		}

		return status;
	}

	protected abstract BigInteger getMax(EObject obj);

	protected abstract boolean isSetMax(EObject obj);

}