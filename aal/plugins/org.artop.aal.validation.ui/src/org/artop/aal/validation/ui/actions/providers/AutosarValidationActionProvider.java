/**
 * <copyright>
 *
 * Copyright (c) BMW Car IT, See4sys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 *
 * Contributors:
 *     See4sys - Initial API and implementation
 *     Continental AG - Contribute merged AUTOSAR model validation.
 * </copyright>
 */
package org.artop.aal.validation.ui.actions.providers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.artop.aal.validation.ui.actions.AutosarValidateAction;
import org.artop.aal.validation.ui.actions.FixUuidConflictsAction;
import org.artop.aal.validation.ui.actions.MergedAutosarValidationAction;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.sphinx.emf.validation.ui.actions.BasicValidateAction;
import org.eclipse.sphinx.emf.validation.ui.actions.providers.BasicValidationActionProvider;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * Implementation of {@linkplain BasicValidationActionProvider validation action provider} dedicated to actions related
 * to validation of AUTOSAR models. It provides actions already provided by the basic implementation of
 * {@linkplain BasicValidationActionProvider validation action provider} and provides one more action:
 * <ul>
 * <li>a {@linkplain FixUuidConflictsAction fix UUID conflicts action} that is responsible for computing conflicts of
 * UUID between all Identifiable objects and then fixing theses conflicts by generating new UUIDs and setting them on
 * concerned Identifiable objects.</li>
 * </ul>
 */
public class AutosarValidationActionProvider extends BasicValidationActionProvider {

	/**
	 * Actions to be contributed.
	 */
	protected Set<BaseSelectionListenerAction> actions;

	/**
	 * Action responsible for fixing UUID conflicts on Identifiable objects.
	 */
	protected FixUuidConflictsAction fixUuidConflictsAction;

	/**
	 * Action responsible for merged AUTOSAR model validation.
	 */
	protected MergedAutosarValidationAction mergedValidationAction;

	/*
	 * @see org.eclipse.sphinx.emf.validation.ui.actions.providers.BasicValidationActionProvider#doInit()
	 */
	@Override
	public void doInit() {
		super.doInit();
		actions = new HashSet<BaseSelectionListenerAction>(
				Arrays.asList(fixUuidConflictsAction = createFixUuidConflictsAction(), mergedValidationAction = createMergedValidationAction()));
	}

	/**
	 * @return The fix UUID action to contribute to the "Validate" menu item.
	 */
	protected FixUuidConflictsAction createFixUuidConflictsAction() {
		return new FixUuidConflictsAction();
	}

	@Override
	protected BasicValidateAction createValidateAction() {
		return new AutosarValidateAction();
	}

	/**
	 * @return The merged AUTOSAR model validation action to contribute to the "Validate" menu item.
	 */
	protected MergedAutosarValidationAction createMergedValidationAction() {
		return new MergedAutosarValidationAction();
	}

	/*
	 * @see org.eclipse.sphinx.emf.validation.ui.actions.providers.BasicValidationActionProvider#populateActions(org.eclipse
	 * .jface .action.IMenuManager, org.eclipse.jface.viewers.IStructuredSelection, boolean)
	 */
	@Override
	protected void populateActions(IMenuManager menu, IStructuredSelection selection, boolean enabled) {
		super.populateActions(menu, selection, enabled);

		for (BaseSelectionListenerAction action : actions) {
			if (action != null) {
				action.selectionChanged(selection);
				action.setEnabled(enabled);
				menu.add(new ActionContributionItem(action));
			}
		}
	}
}
