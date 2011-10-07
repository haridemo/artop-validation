/**
 * <copyright>
 * 
 * Copyright (c) BMW Car IT and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     BMW Car IT - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.aal.examples.validation.constraints.tests.testsuite;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.artop.aal.examples.validation.constraints.tests.UniqueApplicationErrorCodesConstraintIntegrationTest;

public class ExampleConstraintsIntegrationTestSuite {

	/**
	 * Suite.
	 * 
	 * @return the test
	 */
	public static Test suite() {
		TestSuite suite = new TestSuite("Integration test for the Artop example constraints"); //$NON-NLS-1$
		suite.addTestSuite(UniqueApplicationErrorCodesConstraintIntegrationTest.class);
		return suite;
	}
}
