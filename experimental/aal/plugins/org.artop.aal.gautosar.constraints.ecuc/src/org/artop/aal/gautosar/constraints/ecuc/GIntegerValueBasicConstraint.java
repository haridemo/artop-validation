package org.artop.aal.gautosar.constraints.ecuc;

import gautosar.gecucdescription.GIntegerValue;
import gautosar.gecucdescription.GParameterValue;
import gautosar.gecucparameterdef.GIntegerParamDef;

import org.artop.aal.gautosar.constraints.ecuc.util.Messages;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.emf.validation.IValidationContext;
import org.eclipse.osgi.util.NLS;

/**
 * 
 * Superclass for the constraints implementations on an integer value.
 * 
 */
public abstract class GIntegerValueBasicConstraint extends
		AbstractGParameterValueConstraint
{
	@Override
	protected boolean isApplicable(IValidationContext ctx)
	{
		return ctx.getTarget() instanceof GIntegerValue;
	}

	@Override
	protected IStatus doValidate(IValidationContext ctx)
	{

		GIntegerValue gIntegerValue = (GIntegerValue) ctx.getTarget();
		IStatus status = validateDefinitionRef(ctx, gIntegerValue);
		if (status.isOK())
		{
			// the validation of the value requires valid access to the
			// GIntegerParamDef
			status = validateValue(ctx, gIntegerValue);
		}

		return status;
	}

	@Override
	protected IStatus validateDefinitionRef(IValidationContext ctx,
			GParameterValue parameterValue)
	{
		// check if definition is set and available
		IStatus status = super.validateDefinitionRef(ctx, parameterValue);
		if (status.isOK())
		{
			if (!(parameterValue.gGetDefinition() instanceof GIntegerParamDef))
			{
				status = ctx.createFailureStatus(NLS.bind(
						Messages.generic_definitionNotOfType,
						"integer param def"));
			}
		}
		return status;
	}

	/**
	 * Performs the validation on the value of the given
	 * <code>gIntegerValue</code>.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current
	 *            constraint evaluation environment
	 * @param gIntegerValue
	 *            the element on which the validation is performed.
	 * @return a status object describing the result of the validation.
	 */
	protected IStatus validateValue(IValidationContext ctx,
			GIntegerValue gIntegerValue)
	{
		return validateValueSet(ctx, gIntegerValue, gIntegerValue.gGetValue());
	}

	/**
	 * Performs the validation on the boundaries of the definition of the given
	 * <code>gIntegerValue</code>.
	 * 
	 * @param ctx
	 *            the validation context that provides access to the current
	 *            constraint evaluation environment
	 * @param gIntegerValue
	 *            the element on which the validation is performed.
	 * @return a status object describing the result of the validation.
	 */
	protected abstract IStatus validateBoundary(IValidationContext ctx,
			GIntegerValue gIntegerValue);

}
