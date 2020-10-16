/**
 * <copyright>
 *
 * Copyright (c) Continental Engineering Services and others.
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Artop Software License Based on AUTOSAR
 * Released Material (ASLR) which accompanies this distribution, and is
 * available at http://www.artop.org/aslr.html
 *
 * Contributors:
 *     Continental Engineering Services - Initial API and implementation
 *
 * </copyright>
 */
package org.artop.aal.validation.adapter;

import java.util.List;
import java.util.Map;

import org.artop.aal.common.platform.AutosarMetaDataUtils;
import org.artop.aal.common.platform.EAutosarPlatform;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticChain;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.sphinx.emf.validation.evalidator.adapter.EValidatorAdapter;

import gautosar.util.GAutosarPackage;

/**
 * Customization of the Sphinx validator adapter.
 */
public class EAutosarValidatorAdapter extends EValidatorAdapter {

	private EAutosarPlatform platform;

	/**
	 * @param platform
	 *            the {@link EAutosarPlatform} to be considered or <code>null</code> if not specified
	 */
	public void set(EAutosarPlatform platform) {
		this.platform = platform;

	}

	// TODO: remove the overwriting of this method after the incorporation of a new sphinx. Please see
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=380616
	@Override
	protected void validateEMFRules(EClass eClass, EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		if (areEMFIntrinsicConstraintsEnabled() == Boolean.TRUE) {
			if (eClass.eContainer() == getEPackage()) {
				validate(eClass.getClassifierID(), eObject, diagnostics, context);
			} else {
				List<EClass> eSuperTypes = eClass.getESuperTypes();
				if (eSuperTypes.isEmpty()) {
					validate_EveryDefaultConstraint(eObject, diagnostics, context);
				} else {
					validate(eSuperTypes.get(0), eObject, diagnostics, context);
				}
			}
		}
	}

	@Override
	public boolean validate_EveryMultiplicityConforms(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		EClass eClass = eObject.eClass();
		for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
			EStructuralFeature feature = eClass.getEStructuralFeature(i);
			if (platform != null) {
				boolean isApplicableToPlatform = AutosarMetaDataUtils.isApplicableTo(feature, platform);
				if (!isApplicableToPlatform) {
					continue;
				}
			}

			EPackage ePackage = feature.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}
			result &= validate_MultiplicityConforms(eObject, feature, diagnostics, context);
			if (!result && diagnostics == null) {
				return false;
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryProxyResolves(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EContentsEList.FeatureIterator<EObject> i = (EContentsEList.FeatureIterator<EObject>) eObject.eCrossReferences().iterator(); i
				.hasNext();) {
			EObject eCrossReferenceObject = i.next();
			EStructuralFeature feature = i.feature();
			EPackage ePackage = feature.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}

			if (eCrossReferenceObject.eIsProxy()) {
				result = false;
				if (diagnostics != null) {
					diagnostics.add(
							createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, EOBJECT__EVERY_PROXY_RESOLVES, "_UI_UnresolvedProxy_diagnostic",
									new Object[] { getFeatureLabel(i.feature(), context), getObjectLabel(eObject, context),
											getObjectLabel(eCrossReferenceObject, context) },
									new Object[] { eObject, i.feature(), eCrossReferenceObject }, context));
				} else {
					break;
				}
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryReferenceIsContained(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		if (eObject.eResource() != null) {
			for (EContentsEList.FeatureIterator<EObject> i = (EContentsEList.FeatureIterator<EObject>) eObject.eCrossReferences().iterator(); i
					.hasNext();) {
				EObject eCrossReferenceObject = i.next();
				EStructuralFeature feature = i.feature();
				EPackage ePackage = feature.getEType().getEPackage();
				if (ePackage != null) {
					String nsURI = ePackage.getNsURI();
					if (nsURI != null) {
						// filter the features that come from generic packages in order to avoid duplicate validation
						// messages
						if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
							continue;
						}
					}
				}
				if (eCrossReferenceObject.eResource() == null && !eCrossReferenceObject.eIsProxy() && !i.feature().isTransient()) {
					result = false;
					if (diagnostics != null) {
						diagnostics.add(createDiagnostic(Diagnostic.ERROR, DIAGNOSTIC_SOURCE, EOBJECT__EVERY_REFERENCE_IS_CONTAINED,
								"_UI_DanglingReference_diagnostic",
								new Object[] { getFeatureLabel(i.feature(), context), getObjectLabel(eObject, context),
										getObjectLabel(eCrossReferenceObject, context) },
								new Object[] { eObject, i.feature(), eCrossReferenceObject }, context));
					} else {
						break;
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryBidirectionalReferenceIsPaired(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EReference eReference : eObject.eClass().getEAllReferences()) {
			EPackage ePackage = eReference.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}
			if (eReference.isResolveProxies()) {
				EReference eOpposite = eReference.getEOpposite();
				if (eOpposite != null) {
					result &= validate_BidirectionalReferenceIsPaired(eObject, eReference, eOpposite, diagnostics, context);
					if (!result && diagnostics == null) {
						return false;
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryDataValueConforms(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		for (EAttribute eAttribute : eObject.eClass().getEAllAttributes()) {
			EPackage ePackage = eAttribute.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}
			result &= validate_DataValueConforms(eObject, eAttribute, diagnostics, context);
			if (!result && diagnostics == null) {
				return false;
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryKeyUnique(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		EClass eClass = eObject.eClass();
		for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
			EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);

			EPackage ePackage = eStructuralFeature.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}
			if (eStructuralFeature instanceof EReference) {
				EReference eReference = (EReference) eStructuralFeature;
				if (eReference.isMany() && !eReference.getEKeys().isEmpty()) {
					result &= validate_KeyUnique(eObject, eReference, diagnostics, context);
					if (!result && diagnostics == null) {
						return false;
					}
				}
			}
		}
		return result;
	}

	@Override
	public boolean validate_EveryMapEntryUnique(EObject eObject, DiagnosticChain diagnostics, Map<Object, Object> context) {
		boolean result = true;
		EClass eClass = eObject.eClass();
		for (int i = 0, size = eClass.getFeatureCount(); i < size; ++i) {
			EStructuralFeature eStructuralFeature = eClass.getEStructuralFeature(i);

			EPackage ePackage = eStructuralFeature.getEType().getEPackage();
			if (ePackage != null) {
				String nsURI = ePackage.getNsURI();
				if (nsURI != null) {
					// filter the features that come from generic packages in order to avoid duplicate validation
					// messages
					if (nsURI.startsWith(GAutosarPackage.eNS_URI)) {
						continue;
					}
				}
			}

			if (eStructuralFeature.getEType().getInstanceClassName() == "java.util.Map$Entry" && eStructuralFeature instanceof EReference) {
				EReference eReference = (EReference) eStructuralFeature;
				result &= validate_MapEntryUnique(eObject, eReference, diagnostics, context);
				if (!result && diagnostics == null) {
					return false;
				}
			}
		}
		return result;
	}
}
