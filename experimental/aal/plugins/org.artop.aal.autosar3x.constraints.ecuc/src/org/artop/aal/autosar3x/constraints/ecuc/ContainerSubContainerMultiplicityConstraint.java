/**
 * <copyright>
 * 
 * Copyright (c) OpenSynergy and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     OpenSynergy - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.aal.autosar3x.constraints.ecuc;

import java.util.ArrayList;
import java.util.List;

import org.artop.aal.autosar3x.constraints.ecuc.internal.Activator;
import org.artop.aal.common.resource.AutosarURIFactory;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.emf.validation.IValidationContext;

import autosar3x.ecucdescription.Container;
import autosar3x.ecucparameterdef.ChoiceContainerDef;
import autosar3x.ecucparameterdef.ContainerDef;
import autosar3x.ecucparameterdef.ParamConfContainerDef;
import autosar3x.genericstructure.infrastructure.identifiable.Identifiable;

public class ContainerSubContainerMultiplicityConstraint extends AbstractModelConstraintWithPrecondition {
	@Override
	protected boolean isApplicable(IValidationContext ctx) {
		boolean isApplicable = false;
		if (ctx.getTarget() instanceof Container) {
			Container container = (Container) ctx.getTarget();
			ContainerDef containerDef = container.getDefinition();
			isApplicable = null != containerDef && false == containerDef.eIsProxy();
		}
		return isApplicable;
	}

	@Override
	public IStatus doValidate(IValidationContext ctx) {
		assert ctx.getTarget() instanceof Container;

		final IStatus status;

		Container container = (Container) ctx.getTarget();
		ContainerDef containerDef = container.getDefinition();

		if (containerDef instanceof ChoiceContainerDef) {
			status = validateChoiceContainer(ctx, container, (ChoiceContainerDef) containerDef);
		} else {
			status = validateParamConfContainer(ctx, container, (ParamConfContainerDef) containerDef);
		}

		return status;
	}

	private IStatus validateChoiceContainer(IValidationContext ctx, Container container, ChoiceContainerDef choiceContainerDef) {
		final IStatus status;

		List<Container> allSubContainers = EcucUtil.getAllSubContainersOf(container);

		List<Identifiable> identifiables = new ArrayList<Identifiable>();
		identifiables.addAll(allSubContainers);
		int numberOfUniqueShortNames = EcucUtil.getNumberOfUniqueShortNames(identifiables);

		// choice container may only contain a single subcontainer
		if (1 != numberOfUniqueShortNames) {
			status = ctx.createFailureStatus("ChoiceContainer must contain exactly one subcontainer");
			ctx.addResults(allSubContainers);
		} else {
			status = ctx.createSuccessStatus();
		}

		return status;
	}

	private IStatus validateParamConfContainer(IValidationContext ctx, Container container, ParamConfContainerDef paramConfContainerDef) {
		MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID, 0, this.getClass().getName(), null);

		List<Container> allSubContainers = EcucUtil.getAllSubContainersOf(container);
		List<ContainerDef> subContainerDefs = paramConfContainerDef.getSubContainers();
		for (ContainerDef currentSubContainerDef : subContainerDefs) {
			int numberOfSubContainers = EcucUtil.getNumberOfUniqueContainersByDefinition(allSubContainers, currentSubContainerDef);
			if (!EcucUtil.isValidLowerMultiplicity(numberOfSubContainers, currentSubContainerDef)) {
				multiStatus.add(ctx.createFailureStatus("Expected min " + EcucUtil.getLowerMultiplicity(currentSubContainerDef)
						+ " SubContainers with definition=" + AutosarURIFactory.getAbsoluteQualifiedName(currentSubContainerDef) + ". Found "
						+ numberOfSubContainers + "."));
			}
			if (!EcucUtil.isValidUpperMultiplicity(numberOfSubContainers, currentSubContainerDef)) {
				multiStatus.add(ctx.createFailureStatus("Expected max " + EcucUtil.getUpperMultiplicity(currentSubContainerDef)
						+ " SubContainers with definition=" + AutosarURIFactory.getAbsoluteQualifiedName(currentSubContainerDef) + ". Found "
						+ numberOfSubContainers + "."));
			}
		}
		return multiStatus;
	}
}
