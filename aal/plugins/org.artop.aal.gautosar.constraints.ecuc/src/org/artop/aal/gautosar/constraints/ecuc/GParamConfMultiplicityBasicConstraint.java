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
 *     Continental AG - Mark class as Splitable aware.
 * </copyright>
 */
package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucparameterdef.GParamConfMultiplicity;

import org.artop.aal.gautosar.constraints.ecuc.internal.Activator;
import org.artop.aal.gautosar.constraints.ecuc.messages.EcucConstraintMessages;
import org.artop.aal.validation.constraints.AbstractSplitModelConstraintWithPrecondition;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * Superclass for the constraints implementations on the lower and upper multiplicity of a param conf multiplicity
 * object.
 */
public class GParamConfMultiplicityBasicConstraint extends AbstractSplitModelConstraintWithPrecondition {

	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		return ctx.getTarget() instanceof GParamConfMultiplicity;
	}

	@Override
	public IStatus doValidate(IValidationContext ctx) {
		GParamConfMultiplicity gParamConfMultiplicity = (GParamConfMultiplicity) ctx.getTarget();

		MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID, 0, this.getClass().getName(), null);

		// validate lower multiplicity
		String lowerMultiplicityString = gParamConfMultiplicity.gGetLowerMultiplicityAsString();
		if (lowerMultiplicityString != null && !lowerMultiplicityString.equals("")) //$NON-NLS-1$
		{
			try {
				int lowerMultiplicity = Integer.parseInt(lowerMultiplicityString);
				if (lowerMultiplicity < 0) {
					multiStatus.add(ctx.createFailureStatus(EcucConstraintMessages.multiplicity_lowerMultNegative));
				}
			} catch (NumberFormatException nfe) {
				multiStatus.add(ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.multiplicity_lowerMultException, nfe.getMessage())));
			}
		}

		// validate upper multiplicity
		String upperMultiplicityString = gParamConfMultiplicity.gGetUpperMultiplicityAsString();
		if (upperMultiplicityString != null && !upperMultiplicityString.equals("")) //$NON-NLS-1$
		{
			if (gParamConfMultiplicity.gGetUpperMultiplicityInfinite()) {
				multiStatus.add(ctx.createSuccessStatus());
			} else {
				try {
					int upperMultiplicity = Integer.parseInt(upperMultiplicityString);
					if (upperMultiplicity < 0) {
						multiStatus.add(ctx.createFailureStatus(EcucConstraintMessages.multiplicity_upperMultNegative));
					}
				} catch (NumberFormatException nfe) {
					multiStatus.add(ctx.createFailureStatus(NLS.bind(EcucConstraintMessages.multiplicity_upperMultException, nfe.getMessage())));
				}
			}
		}

		if (multiStatus.getChildren().length == 0) {
			return ctx.createSuccessStatus();
		}

		return multiStatus;
	}
}
