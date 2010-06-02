/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation, Geensys, and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Geensys - added support for problem markers on model objects (rather than 
 *               only on workspace resources). Unfortunately, there was no other 
 *               choice than copying the whole code from 
 *               org.eclipse.ui.views.markers.internal for that purpose because 
 *               many of the relevant classes, methods, and fields are private or
 *               package private.
 *******************************************************************************/
package org.artop.ecl.emf.validation.ui.views;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;

/**
 * The FilterEnablementAction is an action for enabling or disabling a filter.
 */
class FilterEnablementAction extends Action {

	private MarkerFilter markerFilter;
	private MarkerView markerView;

	/**
	 * Create a new action for the filter.
	 * 
	 * @param filter
	 * @param view
	 */
	public FilterEnablementAction(MarkerFilter filter, MarkerView view) {
		super(filter.getName(), SWT.CHECK);
		setChecked(filter.isEnabled());
		markerFilter = filter;
		markerView = view;

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	@Override
	public void run() {
		markerFilter.setEnabled(!markerFilter.isEnabled());
		setChecked(markerFilter.isEnabled());
		markerView.updateForFilterChanges();
	}

}
