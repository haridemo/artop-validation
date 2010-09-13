/**
 * <copyright>
 * 
 * Copyright (c) OpenSynergy,  Continental Engineering Services  and others.
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
package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucdescription.GConfigReferenceValue;

import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.validation.IValidationContext;

/**
 * 
 * Abstract superclass for the constraints implementations on a config reference
 * value.
 * 
 */
public abstract class AbstractGConfigReferenceValueConstraint extends
		AbstractModelConstraintWithPrecondition
{

	/**
	 * Performs the validation on the definition of the given
	 * <code>gConfigReferenceValue</code>.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current
	 *            constraint evaluation environment
	 * @param gConfigReferenceValue
	 *            the element on which the validation is performed.
	 * @return a status object describing the result of the validation.
	 */
	protected IStatus validateDefinitionRef(IValidationContext ctx,
			GConfigReferenceValue gConfigReferenceValue)
	{
		// check if definition is set and available
		final IStatus status;
		if (null == gConfigReferenceValue.gGetDefinition())
		{
			status = ctx
					.createFailureStatus(Messages.generic_definitionReferenceNotSet);
		} else if (gConfigReferenceValue.gGetDefinition().eIsProxy())
		{
			status = ctx
					.createFailureStatus(Messages.generic_definitionReferenceNotResolved);
		} else
		{
			status = ctx.createSuccessStatus();
		}
		return status;
	}

	/**
	 * Checks whether the given <code>instance</code> is an instance of the
	 * destination with the given <code>destinationTypeName</code>.
	 * 
	 * @param instance
	 * @param destinationTypeName
	 * @return
	 */
	protected boolean isInstanceOfDestinationType(EObject instance,
			String destinationTypeName)
	{
		boolean isInstanceOfDestinationType = false;

		EClass metaClass = instance.eClass();
		String metaClassName = metaClass.getName();

		if (metaClassName.equals(destinationTypeName))
		{
			isInstanceOfDestinationType = true;
		} else
		{
			// get all super types of the metaClass and check if destination
			// type is a
			// super type
			for (EClass superType : metaClass.getESuperTypes())
			{
				// check if destination type is a super type of value class
				if (superType.getName().equals(destinationTypeName))
				{
					isInstanceOfDestinationType = true;
					break;
				}
			}
		}
		return isInstanceOfDestinationType;
	}

	/**
	 * Performs the validation on the value of the given
	 * <code>gConfigReferenceValue</code>.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current
	 *            constraint evaluation environment
	 * @param gConfigReferenceValue
	 *            the element on which the validation is performed.
	 * @param value
	 *            the value of <code>gConfigReferenceValue</code>
	 * @return a status object describing the result of the validation.
	 */
	protected IStatus validateValueSet(IValidationContext ctx,
			GConfigReferenceValue gConfigReferenceValue, Object value)
	{
		if (null == value || value.equals(""))
		{
			return ctx.createFailureStatus(Messages.generic_valueNotSet);
		}

		return ctx.createSuccessStatus();

	}
}