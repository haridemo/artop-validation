/**
 * <copyright>
 * 
 * Copyright (c) Geensys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     Geensys - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.ecl.emf.validation.markers;

public interface IValidationMarker {

	/**
	 * Validation problem marker type.
	 */
	public static final String ECL_VALIDATION_PROBLEM = "ecl.emf.validation.problem.marker"; //$NON-NLS-1$

	/**
	 * This is the id of the marker attribute in order to hold an understandable eObject name. not used yet
	 */

	public static final String EOBJECT_ATTRIBUTE = "eobject_id"; //$NON-NLS-1$

	/**
	 * This is the id of the marker in order to hold the target features of this markers
	 */

	public static final String FEATURES_ATTRIBUTE = "features_id"; //$NON-NLS-1$

	/**
	 * This is the id of the violated rule
	 */

	public static final String RULE_ID_ATTRIBUTE = "rule_id"; //$NON-NLS-1$

}