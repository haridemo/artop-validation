/**
 * <copyright>
 * 
 * Copyright (c) Geensys and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on Released
 * AUTOSAR Material (ASLR) which accompanies this distribution, and is available
 * at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     Geensys - Initial API and implementation
 * 
 * </copyright>
 */
package org.artop.ecl.examples.sphinx.validation.internal;

import org.eclipse.emf.common.EMFPlugin;
import org.eclipse.emf.common.util.ResourceLocator;

/**
 * The activator class controls the plug-in life cycle
 */
public final class Activator extends EMFPlugin {

	/**
	 * Keep track of the singleton.
	 */
	public static final Activator INSTANCE = new Activator();

	/**
	 * Keep track of the singleton.
	 */
	private static Implementation plugin;

	/**
	 * Create the instance.
	 */
	public Activator() {
		super(new ResourceLocator[] {});

	}

	/**
	 * Returns the singleton instance of the Eclipse plug-in.
	 * 
	 * @return the singleton instance.
	 */
	@Override
	public ResourceLocator getPluginResourceLocator() {
		return plugin;
	}

	/**
	 * Returns the singleton instance of the Eclipse plug-in.
	 * 
	 * @return the singleton instance.
	 */
	public static Implementation getPlugin() {
		return plugin;
	}

	public static Implementation getDefault() {
		return plugin;
	}

	/**
	 * The actual implementation of the Eclipse <b>Plug-in</b>.
	 */
	public static class Implementation extends EclipsePlugin {

		/**
		 * Creates an instance.
		 */
		public Implementation() {
			super();

			// Remember the static instance
			plugin = this;
		}
	}
}