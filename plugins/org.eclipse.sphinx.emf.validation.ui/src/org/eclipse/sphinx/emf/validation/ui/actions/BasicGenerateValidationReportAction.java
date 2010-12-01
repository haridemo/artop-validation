/**
 * <copyright>
 * 
 * Copyright (c) See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     See4sys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.validation.ui.actions;

import org.eclipse.sphinx.platform.ui.util.ExtendedPlatformUI;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sphinx.emf.validation.ui.util.Messages;
import org.eclipse.sphinx.emf.validation.ui.wizards.ValidationReportWizard;
import org.eclipse.ui.actions.SelectionListenerAction;

public class BasicGenerateValidationReportAction extends SelectionListenerAction {

	/**
	 * Default constructor
	 */
	public BasicGenerateValidationReportAction() {
		super(Messages._UI_ValidationReport_menu_item);
		setDescription(Messages._UI_ValidationReport_simple_description);
	}

	@Override
	public void run() {

		// TODO Run wizard with selectedObjects rather than IResources
		ValidationReportWizard validationWizard = new ValidationReportWizard(getSelectedResources());

		WizardDialog dilaog = new WizardDialog(ExtendedPlatformUI.getActiveShell(), validationWizard);
		dilaog.setBlockOnOpen(true);
		dilaog.open();

	}
}
