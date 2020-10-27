package org.artop.aal.validation.diagnostic;

import org.artop.aal.common.platform.EAutosarPlatform;
import org.artop.aal.validation.adapter.EAutosarValidatorAdapter;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.util.EObjectValidator;
import org.eclipse.sphinx.emf.validation.diagnostic.ExtendedDiagnostician;

/**
 * An AUTOSAR platform-aware diagnostician
 */
public class AutosarExtendedDiagnostician extends ExtendedDiagnostician {

	private EAutosarPlatform platform;

	/**
	 * @param platform
	 *            the {@link EAutosarPlatform} to be taken into consider or <code>null</code> if not specified
	 */
	public AutosarExtendedDiagnostician(EAutosarPlatform platform) {
		this.platform = platform;
	}

	@Override
	protected EObjectValidator findEValidator(EClass eClass) {

		EObjectValidator validator = super.findEValidator(eClass);
		if (validator instanceof EAutosarValidatorAdapter) {
			((EAutosarValidatorAdapter) validator).set(platform);
		}
		return validator;
	}
}
