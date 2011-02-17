/**
 * <copyright>
 * 
 * Copyright (c) OpenSynergy, Continental Engineering Services and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 * 
 * Contributors: 
 *     OpenSynergy - Initial API and implementation for AUTOSAR 3.x
 *     Continental Engineering Services - migration to gautosar
 *     see4Sys
 * </copyright>
 */
package org.artop.aal.gautosar.constraints.ecuc.util;

import gautosar.gecucdescription.GConfigReferenceValue;
import gautosar.gecucdescription.GContainer;
import gautosar.gecucdescription.GParameterValue;
import gautosar.gecucparameterdef.GChoiceContainerDef;
import gautosar.gecucparameterdef.GCommonConfigurationAttributes;
import gautosar.gecucparameterdef.GConfigParameter;
import gautosar.gecucparameterdef.GConfigReference;
import gautosar.gecucparameterdef.GContainerDef;
import gautosar.gecucparameterdef.GModuleDef;
import gautosar.gecucparameterdef.GParamConfContainerDef;
import gautosar.gecucparameterdef.GParamConfMultiplicity;
import gautosar.ggenericstructure.ginfrastructure.GIdentifiable;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.artop.aal.common.resource.AutosarURIFactory;
import org.artop.aal.gautosar.constraints.ecuc.internal.Activator;
import org.artop.aal.gautosar.services.DefaultMetaModelServiceProvider;
import org.artop.aal.gautosar.services.ecuc.IECUCService;
import org.artop.ecl.emf.metamodel.IMetaModelDescriptor;
import org.artop.ecl.emf.metamodel.MetaModelDescriptorRegistry;
import org.artop.ecl.platform.util.PlatformLogUtil;
import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.osgi.util.NLS;

public class EcucUtil {
	static final String MULTIPLICITY_ZERO = "0"; //$NON-NLS-1$
	static final String MULTIPLICITY_ONE = "1"; //$NON-NLS-1$
	static final String MULTIPLICITY_INFINITY = "*"; //$NON-NLS-1$

	public static String getFeatureValue(EObject eObject, String featureName) {
		EStructuralFeature eFeature = eObject.eClass().getEStructuralFeature(featureName);

		if (eFeature == null) {
			return null;
		}

		Object featureValue = eObject.eGet(eFeature);
		if (featureValue == null) {
			return null;
		}
		return featureValue.toString();
	}

	public static GContainerDef getContainerDefInRefinedModuleDef(GContainerDef vSpecifContainerDef) {
		/*
		 * The ContainerDef corresponding to the given one in the refined ModuleDef. That is the Container Definition to
		 * return.
		 */
		GContainerDef refinedContainerDef = null;

		/*
		 * The Module Definition which is refined by the Module Definition containing the given Container Definition.
		 */
		GModuleDef refinedModuleDef = getParentRefinedModuleDef(vSpecifContainerDef);
		if (refinedModuleDef != null) {

			/* Initialises cache with the Container Definition objects contained in the Refined Module Definition. */
			GContainerDef[] cache = refinedModuleDef.gGetContainers().toArray(new GContainerDef[0]);

			for (GContainerDef vSpecifContDefAncestor : getAncestors(vSpecifContainerDef)) {
				for (GContainerDef refinedContDefAncestor : cache) {
					if (refinedContDefAncestor.gGetShortName().equals(vSpecifContDefAncestor.gGetShortName())) {
						refinedContainerDef = refinedContDefAncestor;
						if (refinedContDefAncestor instanceof GParamConfContainerDef) {
							cache = ((GParamConfContainerDef) refinedContDefAncestor).gGetSubContainers().toArray(new GContainerDef[0]);
						} else if (refinedContDefAncestor instanceof GChoiceContainerDef) {
							cache = ((GChoiceContainerDef) refinedContDefAncestor).gGetChoices().toArray(new GContainerDef[0]);
						} else {
							cache = null;
						}
						break;
					}
				}
				if (cache == null) {
					break;
				}
			}
		}

		return refinedContainerDef;
	}

	public static GConfigParameter getConfigParameterInRefinedModuleDef(GConfigParameter configParameter) {
		/*
		 * The ConfigParameter corresponding to the given one in the refined ModuleDef.
		 */
		GConfigParameter refinedConfigParameter = null;

		/*
		 * The Module Definition which is refined by the Module Definition containing the given Configuration Parameter.
		 */
		GModuleDef refinedModuleDef = getParentRefinedModuleDef(configParameter);

		if (refinedModuleDef != null) {

			GContainerDef refinedContainerDef = null;

			GContainerDef[] tmpRefinedContainers = refinedModuleDef.gGetContainers().toArray(new GContainerDef[0]);

			for (GContainerDef containerDef : getAncestors(configParameter)) {
				for (GContainerDef rcd : tmpRefinedContainers) {
					refinedContainerDef = rcd;
					if (rcd.gGetShortName().equals(containerDef.gGetShortName())) {
						if (rcd instanceof GParamConfContainerDef) {
							GParamConfContainerDef pccd = (GParamConfContainerDef) rcd;
							tmpRefinedContainers = pccd.gGetSubContainers().toArray(new GContainerDef[0]);
							break;
						} else if (rcd instanceof GChoiceContainerDef) {
							GChoiceContainerDef ccd = (GChoiceContainerDef) rcd;
							tmpRefinedContainers = ccd.gGetChoices().toArray(new GContainerDef[0]);
							break;
						}

					}
				}
			}

			if (refinedContainerDef != null && refinedContainerDef instanceof GParamConfContainerDef) {
				for (GConfigParameter refinedConfigParam : ((GParamConfContainerDef) refinedContainerDef).gGetParameters()) {
					if (refinedConfigParam.gGetShortName().equals(configParameter.gGetShortName())) {
						refinedConfigParameter = refinedConfigParam;
						break;
					}
				}
			}
		}

		return refinedConfigParameter;
	}

	public static GModuleDef getParentRefinedModuleDef(GContainerDef containerDef) {
		/*
		 * The Module Definition containing the Container Definition being validated.
		 */
		GModuleDef moduleDef = getParentModuleDefForContainerDef(containerDef);

		/*
		 * The Module Definition which is refined by the current Module Definition.
		 */
		GModuleDef refinedModuleDef = moduleDef.gGetRefinedModuleDef();

		/*
		 * Log a warning if the Refined Module Definition is the Module Definition itself.
		 */
		if (moduleDef.equals(refinedModuleDef)) {
			String m = "ModuleDef \"" + AutosarURIFactory.getAbsoluteQualifiedName(moduleDef) + "\" references itself as 'Refined Module Definition'"; //$NON-NLS-1$ //$NON-NLS-2$
			PlatformLogUtil.logAsWarning(Activator.getDefault(), m);
		}

		return refinedModuleDef;
	}

	public static GModuleDef getParentRefinedModuleDef(GConfigParameter configParameter) {
		/*
		 * The Module Definition containing the Configuration Parameter being validated.
		 */
		GModuleDef moduleDef = getParentModuleDef(configParameter);

		/*
		 * The Module Definition which is refined by the current Module Definition.
		 */
		GModuleDef refinedModuleDef = moduleDef.gGetRefinedModuleDef();

		/*
		 * Log a warning if the Refined Module Definition is the Module Definition itself.
		 */
		if (moduleDef.equals(refinedModuleDef)) {
			PlatformLogUtil.logAsWarning(Activator.getDefault(), NLS.bind(Messages.moduleDef_selfReference, moduleDef.gGetShortName()));
		}

		return refinedModuleDef;
	}

	public static GModuleDef getParentModuleDef(GConfigParameter configParameter) {
		/* Configuration Parameter ancestors hierarchy. */
		ArrayList<GContainerDef> ancestors = getAncestors(configParameter);
		if (ancestors.isEmpty()) {
			throw new RuntimeException(NLS.bind(Messages.configParameter_ancestorEmptylist, configParameter.gGetShortName()));
		}
		return (GModuleDef) ancestors.get(0).eContainer();
	}

	public static GModuleDef getParentModuleDefForContainerDef(GContainerDef containerDef) {
		EObject parent = containerDef.eContainer();
		while (!(parent instanceof GModuleDef) && parent != null) {
			parent = parent.eContainer();
		}
		if (parent instanceof GModuleDef) {
			return (GModuleDef) parent;
		}
		return null;
	}

	public static ArrayList<GContainerDef> getAncestors(GConfigParameter configParameter) {
		ArrayList<GContainerDef> ancestors = new ArrayList<GContainerDef>();

		EObject eContainer = configParameter.eContainer();
		while (eContainer != null && eContainer instanceof GContainerDef) {
			ancestors.add(0, (GContainerDef) eContainer);
			eContainer = eContainer.eContainer();
		}
		;

		return ancestors;
	}

	public static ArrayList<GContainerDef> getAncestors(GContainerDef containerDef) {
		ArrayList<GContainerDef> ancestors = new ArrayList<GContainerDef>();

		ancestors.add(containerDef);

		EObject eContainer = containerDef.eContainer();
		while (eContainer != null && eContainer instanceof GContainerDef) {
			ancestors.add(0, (GContainerDef) eContainer);
			eContainer = eContainer.eContainer();
		}
		return ancestors;
	}

	public static int getNumberOfUniqueShortNames(List<GIdentifiable> identifiables) {
		Set<String> shortNames = new HashSet<String>();
		for (GIdentifiable gIdentifiable : identifiables) {
			shortNames.add(gIdentifiable.gGetShortName());
		}
		return shortNames.size();
	}

	public static int getNumberOfUniqueContainersByDefinition(List<GContainer> containers, GContainerDef gContainerDef)
			throws IllegalArgumentException {
		final int numberOfUniqueContainersByDefinition;

		if (null == containers || null == gContainerDef) {
			throw new IllegalArgumentException(Messages.generic_nullParametersException);
		} else {
			Set<String> uniqueContainers = new HashSet<String>();
			for (GContainer currentContainer : containers) {
				if (gContainerDef.equals(currentContainer.gGetDefinition())) {
					uniqueContainers.add(currentContainer.gGetShortName());
				}
			}
			numberOfUniqueContainersByDefinition = uniqueContainers.size();
		}
		return numberOfUniqueContainersByDefinition;
	}

	public static ArrayList<GIdentifiable> getIdentifiableAncestors(GIdentifiable eObject) {
		ArrayList<GIdentifiable> ancestors = new ArrayList<GIdentifiable>();

		ancestors.add(eObject);
		EObject eContainer = eObject.eContainer();
		while (eContainer != null && (eContainer instanceof GCommonConfigurationAttributes || eContainer instanceof GContainerDef)) {
			ancestors.add(0, (GIdentifiable) eContainer);
			eContainer = eContainer.eContainer();
		}
		return ancestors;
	}

	public static GConfigParameter getFromRefined(GConfigParameter vSpecif) {
		/* The object form the Refined side to return. */
		EObject refined = null;

		/* The parent Vendor Specific Module Definition. */
		GModuleDef vSpecifModuleDef = getParentModuleDef(vSpecif);

		/* The Refined Module Definition. */
		GModuleDef refinedModuleDef = vSpecifModuleDef.gGetRefinedModuleDef();

		if (refinedModuleDef != null) {
			/* Initializes cache with the Container Definition objects contained in the Refined Module Definition. */
			for (GIdentifiable ancestor : getIdentifiableAncestors(vSpecif)) {
				GIdentifiable[] cache = null;
				if (ancestor instanceof GContainerDef) {
					if (refined == null) {
						cache = refinedModuleDef.gGetContainers().toArray(new GContainerDef[0]);
					} else if (refined instanceof GParamConfContainerDef) {
						cache = ((GParamConfContainerDef) refined).gGetSubContainers().toArray(new GContainerDef[0]);
					} else if (refined instanceof GChoiceContainerDef) {
						cache = ((GChoiceContainerDef) refined).gGetChoices().toArray(new GContainerDef[0]);
					} else {
						cache = null;
					}
				} else if (ancestor instanceof GCommonConfigurationAttributes) {
					if (refined instanceof GParamConfContainerDef) {
						if (ancestor instanceof GConfigParameter) {
							cache = ((GParamConfContainerDef) refined).gGetParameters().toArray(new GConfigParameter[0]);
						} else if (ancestor instanceof GConfigReference) {
							cache = ((GParamConfContainerDef) refined).gGetReferences().toArray(new GConfigReference[0]);
						} else {
							cache = null;
						}
					}
				}

				boolean matchFound = false;
				String vSpecifShortName = ancestor.gGetShortName();
				for (EObject retrieved : cache) {
					String refinedShortName = ((GIdentifiable) retrieved).gGetShortName();
					if (refinedShortName.equals(vSpecifShortName)) {
						refined = retrieved;
						matchFound = true;
						break;
					}
				}
				if (!matchFound) {
					refined = null;
					cache = null;
				}
				if (cache == null) {
					break;
				}
			}
		} else {
			//
			// Refined Module Definition is null; the parent Module Definition is a Vendor Specific.
			// Does nothing more but return.
			//
		}
		return (GConfigParameter) refined;
	}

	public static boolean validateLower(GParamConfMultiplicity refinedConfMultiplicity, GParamConfMultiplicity vSpecifConfMultiplicity) {
		/* Flag used to mark the lower multiplicity as valid or not. */
		boolean valid = true;

		/*
		 * Lower multiplicity of the Common Configuration Attribute from the Refined Module Definition side.
		 */
		String refinedLowerMultiplicity = refinedConfMultiplicity.gGetLowerMultiplicityAsString();

		/*
		 * Lower multiplicity of the Common Configuration Attribute from the Vendor Specific Module Definition side.
		 */
		String vSpecifLowerMultiplicity = vSpecifConfMultiplicity.gGetLowerMultiplicityAsString();

		if (refinedLowerMultiplicity != null && refinedLowerMultiplicity.length() > 0 && vSpecifLowerMultiplicity != null
				&& vSpecifLowerMultiplicity.length() > 0) {
			if (!refinedLowerMultiplicity.equals(MULTIPLICITY_INFINITY)) {
				if (vSpecifLowerMultiplicity.equals(MULTIPLICITY_INFINITY)) {
					valid = false;
				} else {
					if (Integer.valueOf(vSpecifLowerMultiplicity) < Integer.valueOf(refinedLowerMultiplicity)) {
						valid = false;
					}
				}
			}
		}
		return valid;
	}

	public static String[] vendorSpecificCommonConfigurationAttributesLowerMultiplicity(GCommonConfigurationAttributes current) {
		/* Parent of the Common Configuration Attribute. */
		GParamConfContainerDef vSpecifParentParamConfContainerDef = null;
		EObject container = current.eContainer();

		if (container instanceof GParamConfContainerDef) {
			vSpecifParentParamConfContainerDef = (GParamConfContainerDef) container;
		}

		if (vSpecifParentParamConfContainerDef == null) {
			return null;
		}

		/* Retrieves the parent Module Definition. */
		GModuleDef vSpecifModuleDef = getParentModuleDefForContainerDef(vSpecifParentParamConfContainerDef);

		/* Try to get the Refined Module Definition. */
		GModuleDef refinedModuleDef = vSpecifModuleDef.gGetRefinedModuleDef();

		/*
		 * If Refined Module Definition is not null, the target is a Common Configuration Attribute from the Vendor
		 * Specific side.
		 */
		if (refinedModuleDef != null) {
			//
			// Retrieves the Container Definition from the Refined side corresponding to the given Container
			// Definition from the Vendor Specific side.
			//
			GContainerDef refinedParentParamConfContainerDef = findContainerDefInModuleDef(refinedModuleDef, vSpecifParentParamConfContainerDef);

			if (refinedParentParamConfContainerDef != null) {
				if (!(refinedParentParamConfContainerDef instanceof GParamConfContainerDef)) {
					return null;
				}

				GParamConfContainerDef def = (GParamConfContainerDef) refinedParentParamConfContainerDef;

				GCommonConfigurationAttributes[] refinedCommonConfigurationAttributes = null;
				if (current instanceof GConfigParameter) {
					refinedCommonConfigurationAttributes = def.gGetParameters().toArray(new GCommonConfigurationAttributes[0]);
				} else if (current instanceof GConfigReference) {
					refinedCommonConfigurationAttributes = def.gGetReferences().toArray(new GCommonConfigurationAttributes[0]);
				}

				if (refinedCommonConfigurationAttributes == null) {
					return null;
				}

				GCommonConfigurationAttributes refinedCommonConfigAtt = (GCommonConfigurationAttributes) find(current.gGetShortName(),
						refinedCommonConfigurationAttributes);

				if (refinedCommonConfigAtt instanceof GParamConfMultiplicity && current instanceof GParamConfMultiplicity) {
					//
					// Perform the comparison between the two lower multiplicity of Common Configuration Attributes.
					//
					if (!validateLower((GParamConfMultiplicity) refinedCommonConfigAtt, (GParamConfMultiplicity) current)) {
						return new String[] { AutosarURIFactory.getAbsoluteQualifiedName(current),
								AutosarURIFactory.getAbsoluteQualifiedName(refinedModuleDef) };
					}
				} else {
					//
					// Refined Common Configuration Attribute not retrieved; does nothing more.
					//
				}
			} else {
				//
				// Refined parent Parameter Configuration Container Definition is null.
				// Does nothing more.
				//
			}
		} else {
			//
			// Refined Module Definition is null; it means the target is not from the Vendor Specific side.
			// Does nothing more.
			//
		}

		return null;
	}

	public static String[] vendorSpecificCommonConfigurationAttributesUpperMultiplicity(GCommonConfigurationAttributes configAttributes) {
		/* Parent of the Common Configuration Attribute. */
		GParamConfContainerDef vSpecifParentParamConfContainerDef = null;
		EObject container = configAttributes.eContainer();

		if (container instanceof GParamConfContainerDef) {
			vSpecifParentParamConfContainerDef = (GParamConfContainerDef) container;
		}

		if (vSpecifParentParamConfContainerDef == null) {
			return null;
		}

		/* Retrieves the parent Module Definition. */
		GModuleDef vSpecifModuleDef = EcucUtil.getParentModuleDefForContainerDef(vSpecifParentParamConfContainerDef);

		/* Try to get the Refined Module Definition. */
		GModuleDef refinedModuleDef = vSpecifModuleDef.gGetRefinedModuleDef();

		/*
		 * If Refined Module Definition is not null, the target is a Common Configuration Attribute from the Vendor
		 * Specific side.
		 */
		if (refinedModuleDef != null) {
			//
			// Retrieves the Container Definition from the Refined side corresponding to the given Container
			// Definition from the Vendor Specific side.
			//
			GContainerDef refinedParentParamConfContainerDef = EcucUtil.findContainerDefInModuleDef(refinedModuleDef,
					vSpecifParentParamConfContainerDef);

			if (refinedParentParamConfContainerDef != null) {
				if (!(refinedParentParamConfContainerDef instanceof GParamConfContainerDef)) {
					return null;
				}

				GParamConfContainerDef def = (GParamConfContainerDef) refinedParentParamConfContainerDef;

				GCommonConfigurationAttributes[] refinedCommonConfigurationAttributes = null;
				if (configAttributes instanceof GConfigParameter) {
					refinedCommonConfigurationAttributes = def.gGetParameters().toArray(new GCommonConfigurationAttributes[0]);
				} else if (configAttributes instanceof GConfigReference) {
					refinedCommonConfigurationAttributes = def.gGetReferences().toArray(new GCommonConfigurationAttributes[0]);
				}

				if (refinedCommonConfigurationAttributes == null) {
					return null;
				}

				GCommonConfigurationAttributes refinedCommonConfigAtt = (GCommonConfigurationAttributes) EcucUtil.find(
						configAttributes.gGetShortName(), refinedCommonConfigurationAttributes);

				if (refinedCommonConfigAtt != null) {
					//
					// Perform the comparison between the two upper multiplicity of Common Configuration Attributes.
					//
					if (!validateUpper((GParamConfMultiplicity) refinedCommonConfigAtt, (GParamConfMultiplicity) configAttributes)) {
						return new String[] { AutosarURIFactory.getAbsoluteQualifiedName(configAttributes),
								AutosarURIFactory.getAbsoluteQualifiedName(refinedModuleDef) };
					}
				}
			} else {
				//
				// Refined parent Parameter Configuration Container Definition is null.
				// Does nothing more.
				//
			}
		} else {
			//
			// Refined Module Definition is null; it means the target is not from the Vendor Specific side.
			// Does nothing more.
			//
		}
		return null;
	}

	/**
	 * Validate the upper multiplicity consistency for the given Parameter Configuration Multiplicity. In the Vendor
	 * Specific side, upper multiplicity must be smaller or equal to upper multiplicity from the Refined side.
	 * 
	 * @param refinedConfMultiplicity
	 *            The Refined one.
	 * @param vSpecifConfMultiplicity
	 *            The Vendor Specific one.
	 * @return <ul>
	 *         <li><b><tt>true&nbsp;&nbsp;</tt></b> if upper multiplicity from the Vendor Specific side is not greater
	 *         than one in Refined side;</li>
	 *         <li><b><tt>false&nbsp;</tt></b> otherwise.</li>
	 *         </ul>
	 */
	public static boolean validateUpper(GParamConfMultiplicity refinedConfMultiplicity, GParamConfMultiplicity vSpecifConfMultiplicity) {

		/* Flag used to mark the upper multiplicity as valid or not. */
		boolean valid = true;

		// Upper multiplicity of the Common Configuration Attribute from the Refined Module Definition side.
		String refinedUpperMultiplicity = refinedConfMultiplicity.gGetUpperMultiplicityAsString();

		// Upper multiplicity of the Common Configuration Attribute from the Vendor Specific Module Definition side.
		String vSpecifUpperMultiplicity = vSpecifConfMultiplicity.gGetUpperMultiplicityAsString();

		if (refinedUpperMultiplicity != null && refinedUpperMultiplicity.length() > 0 && vSpecifUpperMultiplicity != null
				&& vSpecifUpperMultiplicity.length() > 0) {
			if (refinedUpperMultiplicity.equals(MULTIPLICITY_INFINITY)) {
				if (vSpecifUpperMultiplicity.equals(MULTIPLICITY_ZERO)) {
					valid = false;
				}
			} else {
				if (vSpecifUpperMultiplicity.equals(MULTIPLICITY_INFINITY)) {
					valid = false;
				} else if (Integer.valueOf(vSpecifUpperMultiplicity) > Integer.valueOf(refinedUpperMultiplicity)) {
					valid = false;
				}
			}
		}
		return valid;
	}

	public static List<GParameterValue> filterParameterValuesByDefinition(List<GParameterValue> gParameterValues, GConfigParameter gConfigParameter)
			throws IllegalArgumentException {
		List<GParameterValue> filteredParameterValues = new ArrayList<GParameterValue>();

		if (null == gParameterValues || null == gConfigParameter) {
			throw new IllegalArgumentException(Messages.generic_nullParametersException);
		} else {
			for (GParameterValue currentParameterValue : gParameterValues) {
				if (gConfigParameter.equals(currentParameterValue.gGetDefinition())) {
					filteredParameterValues.add(currentParameterValue);
				}
			}
		}
		return filteredParameterValues;
	}

	public static List<GConfigReferenceValue> filterConfigReferenceValuesByDefinition(List<GConfigReferenceValue> gConfigReferenceValues,
			GConfigReference gConfigReference) throws IllegalArgumentException {
		List<GConfigReferenceValue> filteredConfigReferenceValues = new ArrayList<GConfigReferenceValue>();

		if (null == gConfigReferenceValues || null == gConfigReference) {
			throw new IllegalArgumentException(Messages.generic_nullParametersException);
		} else {
			for (GConfigReferenceValue currentConfigReferenceValue : gConfigReferenceValues) {
				if (gConfigReference.equals(currentConfigReferenceValue.gGetDefinition())) {
					filteredConfigReferenceValues.add(currentConfigReferenceValue);
				}
			}
		}
		return filteredConfigReferenceValues;
	}

	public static ArrayList<GContainerDef> getContainerDefAncestors(GContainerDef containerDef) {
		ArrayList<GContainerDef> ancestors = new ArrayList<GContainerDef>();
		ancestors.add(containerDef);
		EObject eContainer = containerDef.eContainer();
		while (eContainer != null && eContainer instanceof GContainerDef) {
			ancestors.add(0, (GContainerDef) eContainer);
			eContainer = eContainer.eContainer();
		}
		return ancestors;
	}

	public static GContainerDef findContainerDefInModuleDef(GModuleDef moduleDef, GContainerDef containerDef) {
		GContainerDef containerDefRetrieved = null;

		/* Initializes cache with the Container Definition objects contained in the Refined Module Definition. */
		GContainerDef[] cache = moduleDef.gGetContainers().toArray(new GContainerDef[0]);

		for (GContainerDef containerDefAncestor : getContainerDefAncestors(containerDef)) {
			if (cache.length == 0) {
				containerDefRetrieved = null;
				break;
			}
			for (GContainerDef containerDefAncestorRetrieved : cache) {
				String containerDefAncestorShortNameRetrieved = containerDefAncestorRetrieved.gGetShortName();
				String containerDefAncestorShortName = containerDefAncestor.gGetShortName();
				if (containerDefAncestorShortNameRetrieved.equals(containerDefAncestorShortName)) {
					containerDefRetrieved = containerDefAncestorRetrieved;
					if (containerDefAncestorRetrieved instanceof GParamConfContainerDef) {
						cache = ((GParamConfContainerDef) containerDefAncestorRetrieved).gGetSubContainers().toArray(new GContainerDef[0]);
					} else if (containerDefAncestorRetrieved instanceof GChoiceContainerDef) {
						cache = ((GChoiceContainerDef) containerDefAncestorRetrieved).gGetChoices().toArray(new GContainerDef[0]);
					} else {
						cache = null;
					}
					break;
				}
			}
			if (cache == null) {
				break;
			}
		}

		return containerDefRetrieved;
	}

	public static EObject find(String shortName, EObject[] eObjects) {
		Assert.isNotNull(shortName);
		Assert.isNotNull(eObjects);

		for (EObject eObject : eObjects) {
			if (eObject instanceof GIdentifiable && shortName.equals(((GIdentifiable) eObject).gGetShortName())) {
				return eObject;
			}
		}
		return null;
	}

	public static String[] inspectContainersSub(EList<GContainerDef> refinedContainers, EList<GContainerDef> vSpecifContainers) {
		/* List of invalid config parameters */
		List<String> failures = new ArrayList<String>();

		for (GContainerDef refinedContainerDef : refinedContainers) {
			/* Retrieves the Container Definition with the specified short name from the Vendor Specific side. */
			EObject vSpecifContainerDef = find(refinedContainerDef.gGetShortName(), vSpecifContainers.toArray(new EObject[0]));

			if (vSpecifContainerDef == null) {
				/*
				 * 'Container Definition' not found in 'Vendor Specific'. If the current 'Refined Container Definition'
				 * has a non-zero lower multiplicity, the current 'Vendor Specific Container Definition' is marked as
				 * missing in 'Vendor Specific Module Definition'.
				 */
				String lowerMultiplicity = refinedContainerDef.gGetLowerMultiplicityAsString();
				if (lowerMultiplicity != null && !"".equals(lowerMultiplicity) && !"0".equals(lowerMultiplicity)) { //$NON-NLS-1$ //$NON-NLS-2$
					String commonConfAttShortName = refinedContainerDef.gGetShortName();
					failures.add(commonConfAttShortName);
				}

			} else {
				// Container Definition has been found in the Vendor Specific side. Just verifies it is really an
				// instance of ContainerDef and continue.
				// apiContainerDef.assertInstanceOfContainerDef(vSpecifContainerDef);
			}
		}

		return failures.toArray(new String[0]);
	}

	public static String[] inspectContainersChoice(EList<GParamConfContainerDef> refinedContainers, EList<GParamConfContainerDef> vSpecifContainers) {
		/* List of invalid config parameters */
		List<String> failures = new ArrayList<String>();

		for (GContainerDef refinedContainerDef : refinedContainers) {

			/* Retrieves the Container Definition with the specified short name from the Vendor Specific side. */
			EObject vSpecifContainerDef = find(refinedContainerDef.gGetShortName(), vSpecifContainers.toArray(new EObject[0]));

			if (vSpecifContainerDef == null) {

				/*
				 * 'Container Definition' not found in 'Vendor Specific'. If the current 'Refined Container Definition'
				 * has a non-zero lower multiplicity, the current 'Vendor Specific Container Definition' is marked as
				 * missing in 'Vendor Specific Module Definition'.
				 */
				String lowerMultiplicity = refinedContainerDef.gGetLowerMultiplicityAsString();
				if (lowerMultiplicity != null && !"".equals(lowerMultiplicity) && !"0".equals(lowerMultiplicity)) { //$NON-NLS-1$ //$NON-NLS-2$
					String commonConfAttShortName = refinedContainerDef.gGetShortName();
					failures.add(commonConfAttShortName);
				}
			} else {
				// Container Definition has been found in the Vendor Specific side. Just verifies it is really an
				// instance of ContainerDef and continue.
				// apiContainerDef.assertInstanceOfContainerDef(vSpecifContainerDef);
			}
		}

		return failures.toArray(new String[0]);
	}

	public static String[] inspectCommonConfigurationParameter(EList<GConfigParameter> refinedCommonConfigurationAttributes,
			EList<GConfigParameter> vSpecifCommonConfigurationAttributes) {
		/* List of invalid config parameters */
		List<String> failures = new ArrayList<String>();

		for (GConfigParameter refinedCommonConfAtt : refinedCommonConfigurationAttributes) {
			if (GCommonConfigurationAttributes.class.isInstance(refinedCommonConfAtt)) {
				/*
				 * Retrieves the Common Configuration Attributes with the specified short name from the Vendor Specific
				 * side.
				 */
				GConfigParameter vSpecifCommonConfAtt = (GConfigParameter) find(refinedCommonConfAtt.gGetShortName(),
						vSpecifCommonConfigurationAttributes.toArray(new EObject[0]));

				if (vSpecifCommonConfAtt == null) {
					/*
					 * 'Common Configuration Attributes' not found in 'Vendor Specific'. If the current 'Refined Common
					 * Configuration Attributes' has a non-zero lower multiplicity, the current 'Vendor Specific Common
					 * Configuration Attributes' is marked as missing in 'Vendor Specific Module Definition'.
					 */
					String lowerMultiplicity = refinedCommonConfAtt.gGetLowerMultiplicityAsString();
					if (lowerMultiplicity != null && !"".equals(lowerMultiplicity) && !"0".equals(lowerMultiplicity)) { //$NON-NLS-1$ //$NON-NLS-2$
						String commonConfAttShortName = refinedCommonConfAtt.gGetShortName();
						failures.add(commonConfAttShortName);
					}
				} else {
					// Common Configuration Attributes has been found in the Vendor Specific side. Does nothing more.
				}
			}
		}
		return failures.toArray(new String[0]);
	}

	public static String[] inspectCommonConfigurationReference(EList<GConfigReference> refinedCommonConfigurationAttributes,
			EList<GConfigReference> vSpecifCommonConfigurationAttributes) {
		/* List of invalid config parameters */
		List<String> failures = new ArrayList<String>();

		for (GConfigReference refinedCommonConfAtt : refinedCommonConfigurationAttributes) {
			if (GCommonConfigurationAttributes.class.isInstance(refinedCommonConfAtt)) {
				/*
				 * Retrieves the Common Configuration Attributes with the specified short name from the Vendor Specific
				 * side.
				 */
				GConfigReference vSpecifCommonConfAtt = (GConfigReference) find(refinedCommonConfAtt.gGetShortName(),
						vSpecifCommonConfigurationAttributes.toArray(new EObject[0]));

				if (vSpecifCommonConfAtt == null) {

					/*
					 * 'Common Configuration Attributes' not found in 'Vendor Specific'. If the current 'Refined Common
					 * Configuration Attributes' has a non-zero lower multiplicity, the current 'Vendor Specific Common
					 * Configuration Attributes' is marked as missing in 'Vendor Specific Module Definition'.
					 */
					String lowerMultiplicity = refinedCommonConfAtt.gGetLowerMultiplicityAsString();
					if (lowerMultiplicity != null && !"".equals(lowerMultiplicity) && !"0".equals(lowerMultiplicity)) { //$NON-NLS-1$ //$NON-NLS-2$
						String commonConfAttShortName = refinedCommonConfAtt.gGetShortName();
						failures.add(commonConfAttShortName);
					}
				} else {
					// Common Configuration Attributes has been found in the Vendor Specific side. Does nothing more.
				}
			}
		}
		return failures.toArray(new String[0]);
	}

	public static boolean isValidLowerMultiplicity(int numberOfObjects, GParamConfMultiplicity gParamConfMultiplicity) {

		IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(gParamConfMultiplicity);

		IECUCService ecucService = new DefaultMetaModelServiceProvider().getService(descriptor, IECUCService.class);
		BigInteger lowerMultiplicity = ecucService.getLowerMultiplicity(gParamConfMultiplicity, new BigInteger(MULTIPLICITY_ONE, 10));

		return numberOfObjects >= lowerMultiplicity.intValue();

	}

	public static String getLowerMultiplicity(GParamConfMultiplicity gParamConfMultiplicity) {
		final String lowerMultiplicity;
		if (gParamConfMultiplicity.gGetLowerMultiplicityAsString() != null) {
			lowerMultiplicity = gParamConfMultiplicity.gGetLowerMultiplicityAsString();
		} else {
			lowerMultiplicity = MULTIPLICITY_ONE;
		}
		return lowerMultiplicity;
	}

	public static String getUpperMultiplicity(GParamConfMultiplicity gParamConfMultiplicity) {

		IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(gParamConfMultiplicity);

		IECUCService ecucService = new DefaultMetaModelServiceProvider().getService(descriptor, IECUCService.class);

		BigInteger upperMultiplicity = ecucService.getUpperMultiplicity(gParamConfMultiplicity, new BigInteger(MULTIPLICITY_ONE, 10));
		if (IECUCService.MULTIPLICITY_STAR == upperMultiplicity) {
			return MULTIPLICITY_INFINITY;
		} else {

			return upperMultiplicity.toString(10);
		}

	}

	public static boolean isValidUpperMultiplicity(int numberOfObjects, GParamConfMultiplicity gParamConfMultiplicity) {

		IMetaModelDescriptor descriptor = MetaModelDescriptorRegistry.INSTANCE.getDescriptor(gParamConfMultiplicity);

		IECUCService ecucService = new DefaultMetaModelServiceProvider().getService(descriptor, IECUCService.class);
		BigInteger upperMultiplicity = ecucService.getUpperMultiplicity(gParamConfMultiplicity, new BigInteger(MULTIPLICITY_ONE, 10));

		if (upperMultiplicity == IECUCService.MULTIPLICITY_STAR) {
			return true;
		} else {
			return numberOfObjects <= upperMultiplicity.intValue();
		}
	}

	public static String enumeratorToString(Collection<? extends Enumerator> enumInstances) {
		Iterator<? extends Enumerator> enumInstancesIterator = enumInstances.iterator();
		StringBuilder stringBuilder = new StringBuilder();

		stringBuilder.append("{"); //$NON-NLS-1$
		if (enumInstancesIterator.hasNext()) {
			stringBuilder.append(enumInstancesIterator.next().getName());
			while (enumInstancesIterator.hasNext()) {
				stringBuilder.append(","); //$NON-NLS-1$
				stringBuilder.append(enumInstancesIterator.next().getName());
			}
		}
		stringBuilder.append("}"); //$NON-NLS-1$

		return stringBuilder.toString();
	}

	/**
	 * This utility method return the first container of type className for a given EObject. We do not test the given
	 * EObject itself.
	 * 
	 * @param eObject
	 * @param className
	 * @return the corresponding EObject, if it exists, null otherwise
	 */
	public static EObject getFirstContainerOfType(EObject eObject, String className) {
		Assert.isNotNull(eObject);
		Assert.isNotNull(className);

		EObject current = eObject.eContainer();
		while (current != null) {
			if (current.eClass().getName().compareTo(className) == 0) {
				return current;
			}
			current = current.eContainer();
		}
		return null;
	}

	/**
	 * Return the {@link GModuleDef} which aggregate the {@link GConfigParameter} cp
	 * 
	 * @param cp
	 *            a GConfigParameter
	 * @return the reached {@link GModuleDef} if this one exists, null otherwise
	 */
	public static GModuleDef getModuleDef(GConfigParameter cp) {
		if (!(cp.eContainer() instanceof GContainerDef)) {
			return null; // NdSam: sometimes a configuration parameter is not owned by a ContainerDef but by an
			// IdentifiableExtensionsMapEntryImpl
		}

		GContainerDef cd = (GContainerDef) cp.eContainer();

		while (cd != null && cd.eContainer() instanceof GContainerDef) {
			cd = (GContainerDef) cd.eContainer();
		}

		return cd.eContainer() != null && cd.eContainer() instanceof GModuleDef ? (GModuleDef) cd.eContainer() : null;

	}
}
