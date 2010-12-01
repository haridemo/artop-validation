/*******************************************************************************
 * Copyright (c) 2000, 2010 IBM Corporation, See4sys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     See4sys - added support for problem markers on model objects (rather than 
 *               only on workspace resources). Unfortunately, there was no other 
 *               choice than copying the whole code from 
 *               org.eclipse.ui.views.markers.internal for that purpose because 
 *               many of the relevant classes, methods, and fields are private or
 *               package private.
 *******************************************************************************/
package org.eclipse.sphinx.emf.validation.ui.views;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ide.undo.UpdateMarkersOperation;

/**
 * ActionMarkCompleted is the action for marking task completion.
 */
public class ActionMarkCompleted extends MarkerSelectionProviderAction {

	/**
	 * Create a new instance of the reciever.
	 * 
	 * @param provider
	 */
	public ActionMarkCompleted(ISelectionProvider provider) {
		super(provider, MarkerMessages.markCompletedAction_title);
		setEnabled(false);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		IMarker[] markers = getSelectedMarkers();
		Map attrs = new HashMap();
		attrs.put(IMarker.DONE, Boolean.TRUE);
		IUndoableOperation op = new UpdateMarkersOperation(markers, attrs, getText(), true);
		execute(op, getText(), null, null);

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.ui.actions.SelectionProviderAction#selectionChanged(org.eclipse.jface.viewers.IStructuredSelection)
	 */
	@Override
	public void selectionChanged(IStructuredSelection selection) {
		setEnabled(false);
		if (selection == null || selection.isEmpty()) {
			return;
		}
		for (Iterator iterator = selection.iterator(); iterator.hasNext();) {
			Object obj = iterator.next();
			if (!(obj instanceof ConcreteMarker)) {
				return;
			}
			IMarker marker = ((ConcreteMarker) obj).getMarker();
			if (!marker.getAttribute(IMarker.USER_EDITABLE, true)) {
				return;
			}
			if (marker.getAttribute(IMarker.DONE, false)) {
				return;
			}
		}
		setEnabled(true);
	}
}
