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

/**
 * The MarkerNode class is the class that handles category nodes and concrete markers.
 */
public abstract class MarkerNode {

	/**
	 * Get the children of the node.
	 * 
	 * @return MarkerNode[]
	 */
	public abstract MarkerNode[] getChildren();

	/**
	 * Return the parent node or <code>null</code> if this is a top level element.
	 * 
	 * @return MarkerNode
	 */
	public abstract MarkerNode getParent();

	/**
	 * Return whether or not this is a concrete node
	 * 
	 * @return boolean
	 */
	public abstract boolean isConcrete();

	/**
	 * Return the description of the receiver.
	 * 
	 * @return String
	 */
	public abstract String getDescription();

	/**
	 * Get a concrete marker from the receiver. If the receiver is concrete return the receiver otherwise return one of
	 * the concrete markers it contains.
	 * 
	 * @return ConcreteMarker
	 */
	public abstract ConcreteMarker getConcreteRepresentative();

}
