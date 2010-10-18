/**
 * <copyright>
 * 
 * Copyright (c) Geensys and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 *     Geensys - Initial API and implementation
 * 
 * </copyright>
 */
package org.eclipse.sphinx.emf.validation.markers;

import java.util.EventObject;

public class MarkerChangedEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3415310768653818533L;

	public MarkerChangedEvent(Object source) {
		super(source);
	}

}
