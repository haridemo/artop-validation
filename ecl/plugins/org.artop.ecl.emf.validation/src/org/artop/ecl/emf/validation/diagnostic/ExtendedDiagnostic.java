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
package org.artop.ecl.emf.validation.diagnostic;

import java.util.List;

import org.eclipse.emf.common.util.BasicDiagnostic;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.validation.model.IModelConstraint;

public class ExtendedDiagnostic extends BasicDiagnostic {

	/**
	 * ID of the violated constraint for which a diagnostic is being created.
	 */
	private String constraintId;

	public ExtendedDiagnostic() {
		super();
	}

	public ExtendedDiagnostic(String source, int code, String message, Object[] data) {
		super(source, code, message, data);
	}

	public ExtendedDiagnostic(int severity, String source, IModelConstraint constraint, int code, String message, Object[] data) {
		super(severity, source, code, message, data);
		constraintId = constraint.getDescriptor().getId();
	}

	public ExtendedDiagnostic(String source, int code, List<? extends Diagnostic> children, String message, Object[] data) {
		super(source, code, children, message, data);
	}

	public String getConstraintId() {
		return constraintId;
	}

}
