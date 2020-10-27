package org.artop.aal.validation.ui.actions;

import java.util.ArrayList;
import java.util.List;

import org.artop.aal.common.platform.EAutosarPlatform;
import org.artop.aal.common.platform.PlatformProjectPreferenceAccessor;
import org.artop.aal.validation.diagnostic.AutosarExtendedDiagnostician;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.edit.ui.EMFEditUIPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sphinx.emf.util.EcorePlatformUtil;
import org.eclipse.sphinx.emf.validation.diagnostic.ExtendedDiagnostician;
import org.eclipse.sphinx.emf.validation.stats.ValidationPerformanceStats;
import org.eclipse.sphinx.emf.validation.ui.Activator;
import org.eclipse.sphinx.emf.validation.ui.actions.BasicValidateAction;
import org.eclipse.sphinx.emf.validation.ui.util.Messages;
import org.eclipse.sphinx.platform.util.PlatformLogUtil;

/**
 * Customization of the {@link BasicValidateAction} that uses an {@link AutosarExtendedDiagnostician}
 */
public class AutosarValidateAction extends BasicValidateAction {

	@Override
	protected List<Diagnostic> validateMulti(List<EObject> selectedModelObjects, IProgressMonitor progressMonitor) {

		List<Diagnostic> result = new ArrayList<Diagnostic>();

		ExtendedDiagnostician diagnostician = createDiagnostician(selectedModelObjects);
		/**
		 * Code below was copied entirely from BasicValidateAction.<br>
		 * TODO: add createDiagnostician() method in the base class in order to avoid overriding validateMulti(List<EObject>,
		 * IProgressMonitor)
		 **/
		if (selectedModelObjects.size() == 1) {
			EObject eObject = selectedModelObjects.get(0);
			if (eObject != null) {

				int count = getNumberOfObject(eObject);
				// ValidationPerformanceStats.INSTANCE.startEvent(enumerator, blameObject);

				progressMonitor.beginTask("", count); //$NON-NLS-1$
				progressMonitor.setTaskName(EMFEditUIPlugin.INSTANCE.getString("_UI_Validating_message", //$NON-NLS-1$
						new Object[] { diagnostician.getObjectLabel(eObject) }));
				ValidationPerformanceStats.INSTANCE.startNewEvent(ValidationPerformanceStats.ValidationEvent.EVENT_APPLY_CONSTRAINTS,
						eObject.toString());
				diagnostician.setProgressMonitor(progressMonitor);
				result.add(diagnostician.validate(eObject));
				ValidationPerformanceStats.INSTANCE.endEvent(ValidationPerformanceStats.ValidationEvent.EVENT_APPLY_CONSTRAINTS, eObject.toString());
				diagnostician.setProgressMonitor(null);
				progressMonitor.done();
			}

			return result;
		}

		else if (selectedModelObjects.size() > 1) {

			int count = 0;
			int[] subCount = new int[selectedModelObjects.size()];
			int cptObject = 0;
			for (EObject object : selectedModelObjects) {
				subCount[cptObject] = getNumberOfObject(object);
				count += subCount[cptObject++];
			}

			progressMonitor.beginTask("", count); //$NON-NLS-1$
			progressMonitor.setTaskName(Messages._UI_progressBar_InitialMsg);

			boolean isProgressMonitor = false;

			diagnostician.setProgressMonitor(progressMonitor);
			isProgressMonitor = true;

			Diagnostic diag = null;
			cptObject = 0;

			int[] nbE = { 0, 0, 0 }; // Error, Warning, Info
			final int ERRIdx = 0;
			final int WARNIdx = 1;
			final int INFOIdx = 2;

			for (Object current : selectedModelObjects) {
				if (progressMonitor.isCanceled()) {
					break;
				}

				IProgressMonitor subMonitor = null;
				if (isProgressMonitor) {
					subMonitor = new SubProgressMonitor(progressMonitor, subCount[cptObject++]);
					subMonitor.subTask(NLS.bind(Messages._UI_subValidationMonitorIntro, EcorePlatformUtil.getFile((EObject) current).getName()));
				}
				ValidationPerformanceStats.INSTANCE.startNewEvent(ValidationPerformanceStats.ValidationEvent.EVENT_APPLY_CONSTRAINTS,
						current.toString());
				diag = diagnostician.validate((EObject) current);
				if (diag != null) {
					result.add(diag);

					for (Diagnostic c : diag.getChildren()) {
						switch (c.getSeverity()) {
						case Diagnostic.ERROR:
							nbE[ERRIdx]++;
							break;
						case Diagnostic.WARNING:
							nbE[WARNIdx]++;
							break;
						case Diagnostic.INFO:
							nbE[INFOIdx]++;
							break;
						default: // do nothing
						}
					}
				}

				if (subMonitor != null) {
					subMonitor.done();
				}
				ValidationPerformanceStats.INSTANCE.endEvent(ValidationPerformanceStats.ValidationEvent.EVENT_APPLY_CONSTRAINTS, current.toString());
				progressMonitor
						.setTaskName(NLS.bind(Messages._UI_progressBarMulti_ErrWarnInfo, new Object[] { nbE[ERRIdx], nbE[WARNIdx], nbE[INFOIdx] }));
			}

			diagnostician.setProgressMonitor(null);
			progressMonitor.done();

			return result;
		}

		PlatformLogUtil.logAsWarning(Activator.getDefault(), new RuntimeException("Cannot perform validation on empty element selection.")); //$NON-NLS-1$
		return null;
	}

	/**
	 * @param selectedModelObjects
	 * @return
	 */
	private AutosarExtendedDiagnostician createDiagnostician(List<EObject> selectedModelObjects) {
		IProject project = getProject(selectedModelObjects);
		EAutosarPlatform platform = null;
		if (project != null) {
			platform = getPlatform(project);
		}
		return new AutosarExtendedDiagnostician(platform);
	}

	/**
	 * @param selectedModelObjects
	 * @return the project containing the file owning the first model element from the specified list, or <code>null</code>
	 *         if the project could not be determined
	 */
	private IProject getProject(List<EObject> selectedModelObjects) {
		if (!selectedModelObjects.isEmpty()) {
			// TBD: assume all selected EObject(s) belong to files that are part of the same project OR access the project
			// preference for each validated EObject in org.artop.aal.validation.adapter.EAutosarValidatorAdapter
			EObject selectedModelObject = selectedModelObjects.get(0);
			return getProject(selectedModelObject);
		}
		return null;
	}

	/**
	 * @param object
	 *            model object
	 * @return the project that contains the file owning the specified <code>object</code>.
	 */
	private static IProject getProject(EObject object) {

		IFile file = EcorePlatformUtil.getFile(object);
		if (file != null) {
			return file.getProject();
		} else {
			return null;
		}
	}

	/**
	 * @param project
	 * @return the {@link EAutosarPlatform} configured for the specified project or <code>null</code> if not configured or
	 *         not applicable
	 */
	private EAutosarPlatform getPlatform(IProject project) {
		EAutosarPlatform platform = null;
		if (project != null) {
			platform = PlatformProjectPreferenceAccessor.INSTANCE.getPlatform(project);
		}
		return platform;
	}
}
