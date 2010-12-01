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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.dynamichelpers.ExtensionTracker;
import org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler;
import org.eclipse.core.runtime.dynamichelpers.IExtensionTracker;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * The ProblemFilterRegistryReader is the registry reader for declarative problem filters. See the
 * org.eclipse.ui.markerSupport extension point.
 * 
 * @since 0.7.0
 */
public class MarkerSupportRegistry implements IExtensionChangeHandler {

	private static final String DESCRIPTION = "onDescription"; //$NON-NLS-1$

	private static final String ENABLED = "enabled"; //$NON-NLS-1$

	private static final Object ERROR = "ERROR";//$NON-NLS-1$

	static final String ID = "id"; //$NON-NLS-1$

	private static final Object INFO = "INFO";//$NON-NLS-1$

	private static final Object WARNING = "WARNING";//$NON-NLS-1$

	private static final String MARKER_ID = "markerId"; //$NON-NLS-1$

	/**
	 * The tag for the marker support extension
	 */
	public static final String MARKER_SUPPORT = "markerSupport";//$NON-NLS-1$

	private static final String NAME = "name"; //$NON-NLS-1$

	private static final Object ON_ANY = "ON_ANY"; //$NON-NLS-1$

	private static final Object ON_ANY_IN_SAME_CONTAINER = "ON_ANY_IN_SAME_CONTAINER";//$NON-NLS-1$

	private static final Object ON_SELECTED_AND_CHILDREN = "ON_SELECTED_AND_CHILDREN";//$NON-NLS-1$

	private static final Object ON_SELECTED_ONLY = "ON_SELECTED_ONLY"; //$NON-NLS-1$

	private static final Object PROBLEM_FILTER = "problemFilter";//$NON-NLS-1$

	private static final String SCOPE = "scope"; //$NON-NLS-1$

	private static final String SELECTED_TYPE = "selectedType"; //$NON-NLS-1$

	private static final String SEVERITY = "severity";//$NON-NLS-1$

	private static final String MARKER_TYPE_REFERENCE = "markerTypeReference"; //$NON-NLS-1$

	private static final String MARKER_CATEGORY = "markerTypeCategory";//$NON-NLS-1$

	private static final String ATTRIBUTE_MAPPING = "markerAttributeMapping"; //$NON-NLS-1$

	private static final String MARKER_GROUPING = "markerGrouping"; //$NON-NLS-1$

	private static final String ATTRIBUTE = "attribute"; //$NON-NLS-1$

	private static final String VALUE = "value"; //$NON-NLS-1$

	private static final String LABEL = "label"; //$NON-NLS-1$

	private static final String MARKER_ATTRIBUTE_GROUPING = "markerAttributeGrouping";//$NON-NLS-1$

	private static final String DEFAULT_GROUPING_ENTRY = "defaultGroupingEntry";//$NON-NLS-1$

	private static final String MARKER_TYPE = "markerType";//$NON-NLS-1$

	private static final String PRIORITY = "priority"; //$NON-NLS-1$

	private static final String MARKER_GROUPING_ENTRY = "markerGroupingEntry"; //$NON-NLS-1$

	private static final Object SEVERITY_ID = "org.eclipse.ui.ide.severity";//$NON-NLS-1$

	private static MarkerSupportRegistry singleton;

	// Create a lock so that initiization happens in one thread
	private static Object creationLock = new Object();

	/**
	 * Get the instance of the registry.
	 * 
	 * @return MarkerSupportRegistry
	 */
	public static MarkerSupportRegistry getInstance() {
		if (singleton == null) {
			synchronized (creationLock) {
				if (singleton == null) {
					// thread
					singleton = new MarkerSupportRegistry();
				}
			}
		}
		return singleton;
	}

	private Collection registeredFilters = new ArrayList();

	private Map markerGroups = new HashMap();

	private Map markerGroupingEntries = new HashMap();

	private HashMap categories = new HashMap();

	private HashMap hierarchyOrders = new HashMap();

	private MarkerType rootType;

	/**
	 * Create a new instance of the receiver and read the registry.
	 */
	private MarkerSupportRegistry() {
		IExtensionTracker tracker = PlatformUI.getWorkbench().getExtensionTracker();
		IExtensionPoint point = Platform.getExtensionRegistry().getExtensionPoint(IDEWorkbenchPlugin.IDE_WORKBENCH, MARKER_SUPPORT);
		if (point == null) {
			return;
		}
		IExtension[] extensions = point.getExtensions();
		// initial population
		Map groupingEntries = new HashMap();
		Set attributeMappings = new HashSet();
		for (IExtension extension : extensions) {
			processExtension(tracker, extension, groupingEntries, attributeMappings);
		}
		postProcessExtensions(groupingEntries, attributeMappings);
		tracker.registerHandler(this, ExtensionTracker.createExtensionPointFilter(point));

	}

	/**
	 * Process the extension and register the result with the tracker. Fill the map of groupingEntries and
	 * attribueMappings processed for post processing.
	 * 
	 * @param tracker
	 * @param extension
	 * @param groupingEntries
	 *            Mapping of group names to the markerGroupingEntries registered for them
	 * @param attributeMappings
	 *            the markerAttributeGroupings found
	 * @see #postProcessExtensions(Map, Collection)
	 */
	private void processExtension(IExtensionTracker tracker, IExtension extension, Map groupingEntries, Collection attributeMappings) {
		IConfigurationElement[] elements = extension.getConfigurationElements();

		for (IConfigurationElement element : elements) {
			if (element.getName().equals(PROBLEM_FILTER)) {
				ProblemFilter filter = newFilter(element);
				registeredFilters.add(filter);
				tracker.registerObject(extension, filter, IExtensionTracker.REF_STRONG);

				continue;
			}
			if (element.getName().equals(MARKER_GROUPING)) {

				FieldMarkerGroup group = new FieldMarkerGroup(element.getAttribute(LABEL), element.getAttribute(ID));
				markerGroups.put(group.getId(), group);
				tracker.registerObject(extension, group, IExtensionTracker.REF_STRONG);
			}

			if (element.getName().equals(MARKER_GROUPING_ENTRY)) {

				MarkerGroupingEntry entry = new MarkerGroupingEntry(element.getAttribute(LABEL), element.getAttribute(ID), Integer.valueOf(
						element.getAttribute(PRIORITY)).intValue());

				String groupName = element.getAttribute(MARKER_GROUPING);

				Collection entries;
				if (groupingEntries.containsKey(groupName)) {
					entries = (Collection) groupingEntries.get(groupName);
				} else {
					entries = new HashSet();
				}

				entries.add(entry);
				groupingEntries.put(groupName, entries);

				tracker.registerObject(extension, entry, IExtensionTracker.REF_STRONG);
			}

			if (element.getName().equals(MARKER_ATTRIBUTE_GROUPING)) {

				AttributeMarkerGrouping grouping = new AttributeMarkerGrouping(element.getAttribute(ATTRIBUTE), element.getAttribute(MARKER_TYPE),
						element.getAttribute(DEFAULT_GROUPING_ENTRY), element);

				attributeMappings.add(grouping);

				tracker.registerObject(extension, grouping, IExtensionTracker.REF_STRONG);
			}

			if (element.getName().equals(MARKER_CATEGORY)) {

				String[] markerTypes = getMarkerTypes(element);
				String categoryName = element.getAttribute(NAME);

				for (String element2 : markerTypes) {
					categories.put(element2, categoryName);

				}
				tracker.registerObject(extension, categoryName, IExtensionTracker.REF_STRONG);
			}

		}
	}

	/**
	 * Process the cross references after all of the extensions have been read.
	 * 
	 * @param groupingEntries
	 * @param attributeMappings
	 * @param groupingEntries
	 *            Mapping of group names to the markerGroupingEntries registered for them
	 * @param attributeMappings
	 *            the markerAttributeGroupings found
	 */
	private void postProcessExtensions(Map groupingEntries, Collection attributeMappings) {
		processGroupingEntries(groupingEntries);
		processAttributeMappings(attributeMappings);
	}

	/**
	 * Process the grouping entries into thier required grouping entries.
	 * 
	 * @param groupingEntries
	 */
	private void processGroupingEntries(Map groupingEntries) {
		Iterator entriesIterator = groupingEntries.keySet().iterator();
		while (entriesIterator.hasNext()) {
			String nextGroupId = (String) entriesIterator.next();
			Iterator nextEntriesIterator = ((Collection) groupingEntries.get(nextGroupId)).iterator();
			if (markerGroups.containsKey(nextGroupId)) {
				while (nextEntriesIterator.hasNext()) {
					MarkerGroupingEntry next = (MarkerGroupingEntry) nextEntriesIterator.next();
					markerGroupingEntries.put(next.getId(), next);
					next.setGroupingEntry((FieldMarkerGroup) markerGroups.get(nextGroupId));

				}
			} else {
				while (nextEntriesIterator.hasNext()) {
					MarkerGroupingEntry next = (MarkerGroupingEntry) nextEntriesIterator.next();
					IDEWorkbenchPlugin.log(NLS.bind("markerGroupingEntry {0} defines invalid group {1}",//$NON-NLS-1$
							new String[] { next.getId(), nextGroupId }));
				}
			}
		}
	}

	/**
	 * Process the attribute mappings into their required grouping entries.
	 * 
	 * @param attributeMappings
	 */
	private void processAttributeMappings(Collection attributeMappings) {
		Iterator mappingsIterator = attributeMappings.iterator();
		while (mappingsIterator.hasNext()) {
			AttributeMarkerGrouping next = (AttributeMarkerGrouping) mappingsIterator.next();
			String defaultEntryId = next.getDefaultGroupingEntry();
			if (defaultEntryId != null) {
				if (markerGroupingEntries.containsKey(defaultEntryId)) {
					MarkerGroupingEntry entry = (MarkerGroupingEntry) markerGroupingEntries.get(defaultEntryId);
					entry.setAsDefault(next.getMarkerType());
				} else {
					IDEWorkbenchPlugin.log(NLS.bind("Reference to invalid markerGroupingEntry {0}",//$NON-NLS-1$
							defaultEntryId));
				}
			}
			IConfigurationElement[] mappings = next.getElement().getChildren(ATTRIBUTE_MAPPING);

			for (IConfigurationElement element : mappings) {
				String entryId = element.getAttribute(MARKER_GROUPING_ENTRY);

				if (markerGroupingEntries.containsKey(entryId)) {
					MarkerGroupingEntry entry = (MarkerGroupingEntry) markerGroupingEntries.get(entryId);
					entry.mapAttribute(next.getMarkerType(), next.getAttribute(), element.getAttribute(VALUE));
				} else {
					IDEWorkbenchPlugin.log(NLS.bind("Reference to invaild markerGroupingEntry {0}", //$NON-NLS-1$
							defaultEntryId));
				}

			}
		}

	}

	/**
	 * Get the markerTypes defined in element.
	 * 
	 * @param element
	 * @return String[]
	 */
	private String[] getMarkerTypes(IConfigurationElement element) {
		IConfigurationElement[] types = element.getChildren(MARKER_TYPE_REFERENCE);
		String[] ids = new String[types.length];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = types[i].getAttribute(ID);
		}
		return ids;
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler#addExtension(org.eclipse.core.runtime.dynamichelpers
	 * .IExtensionTracker, org.eclipse.core.runtime.IExtension)
	 */
	public void addExtension(IExtensionTracker tracker, IExtension extension) {
		Map groupingEntries = new HashMap();
		Set attributeMappings = new HashSet();
		processExtension(tracker, extension, groupingEntries, attributeMappings);
		postProcessExtensions(groupingEntries, attributeMappings);
	}

	/**
	 * Get the collection of currently registered filters.
	 * 
	 * @return Collection of ProblemFilter
	 */
	public Collection getRegisteredFilters() {
		Collection filteredFilters = new ArrayList();
		Iterator registeredIterator = registeredFilters.iterator();
		while (registeredIterator.hasNext()) {
			ProblemFilter next = (ProblemFilter) registeredIterator.next();
			if (next.isFilteredOutByActivity()) {
				continue;
			}
			filteredFilters.add(next);
		}

		return filteredFilters;
	}

	/**
	 * Get the constant for scope from element. Return -1 if there is no value.
	 * 
	 * @param element
	 * @return int one of MarkerView#ON_ANY MarkerView#ON_SELECTED_ONLY MarkerView#ON_SELECTED_AND_CHILDREN
	 *         MarkerView#ON_ANY_IN_SAME_CONTAINER
	 */
	private int getScopeValue(IConfigurationElement element) {
		String scope = element.getAttribute(SCOPE);
		if (scope == null) {
			return -1;
		}
		if (scope.equals(ON_ANY)) {
			return MarkerFilter.ON_ANY;
		}
		if (scope.equals(ON_SELECTED_ONLY)) {
			return MarkerFilter.ON_SELECTED_ONLY;
		}
		if (scope.equals(ON_SELECTED_AND_CHILDREN)) {
			return MarkerFilter.ON_SELECTED_AND_CHILDREN;
		}
		if (scope.equals(ON_ANY_IN_SAME_CONTAINER)) {
			return MarkerFilter.ON_ANY_IN_SAME_CONTAINER;
		}

		return -1;
	}

	/**
	 * Get the constant for scope from element. Return -1 if there is no value.
	 * 
	 * @param element
	 * @return int one of MarkerView#ON_ANY MarkerView#ON_SELECTED_ONLY MarkerView#ON_SELECTED_AND_CHILDREN
	 *         MarkerView#ON_ANY_IN_SAME_CONTAINER
	 */
	private int getSeverityValue(IConfigurationElement element) {
		String severity = element.getAttribute(SEVERITY);
		if (severity == null) {
			return -1;
		}
		if (severity.equals(INFO)) {
			return ProblemFilter.SEVERITY_INFO;
		}
		if (severity.equals(WARNING)) {
			return ProblemFilter.SEVERITY_WARNING;
		}
		if (severity.equals(ERROR)) {
			return ProblemFilter.SEVERITY_ERROR;
		}

		return -1;
	}

	/**
	 * Read the problem filters in the receiver.
	 * 
	 * @param element
	 *            the filter element
	 * @return ProblemFilter
	 */
	private ProblemFilter newFilter(IConfigurationElement element) {
		ProblemFilter filter = new ProblemFilter(element.getAttribute(NAME));

		filter.createContributionFrom(element);

		String enabledValue = element.getAttribute(ENABLED);
		filter.setEnabled(enabledValue == null || Boolean.valueOf(enabledValue).booleanValue());

		int scopeValue = getScopeValue(element);
		if (scopeValue >= 0) {
			filter.setOnResource(scopeValue);
		}

		String description = element.getAttribute(DESCRIPTION);
		if (description != null) {
			boolean contains = true;
			if (description.charAt(0) == '!') {// does not contain flag
				description = description.substring(1, description.length());
				contains = false;
			}
			filter.setContains(contains);
			filter.setDescription(description);
		}

		int severityValue = getSeverityValue(element);
		if (severityValue > 0) {
			filter.setSelectBySeverity(true);
			filter.setSeverity(severityValue);
		} else {
			filter.setSelectBySeverity(false);
		}

		List selectedTypes = new ArrayList();
		IConfigurationElement[] types = element.getChildren(SELECTED_TYPE);
		for (IConfigurationElement element2 : types) {
			String markerId = element2.getAttribute(MARKER_ID);
			if (markerId != null) {
				MarkerType type = filter.getMarkerType(markerId);
				if (type == null) {
					IStatus status = new Status(IStatus.WARNING, IDEWorkbenchPlugin.IDE_WORKBENCH, IStatus.WARNING,
							MarkerMessages.ProblemFilterRegistry_nullType, null);
					IDEWorkbenchPlugin.getDefault().getLog().log(status);
				} else {
					selectedTypes.add(type);
				}
			}
		}

		if (selectedTypes.size() > 0) {
			// specified
			filter.setSelectedTypes(selectedTypes);
		}

		return filter;

	}

	/*
	 * (non-Javadoc)
	 * @see
	 * org.eclipse.core.runtime.dynamichelpers.IExtensionChangeHandler#removeExtension(org.eclipse.core.runtime.IExtension
	 * , java.lang.Object[])
	 */
	public void removeExtension(IExtension extension, Object[] objects) {

		Collection removedGroups = new ArrayList();

		for (Object element : objects) {
			if (element instanceof ProblemFilter) {
				registeredFilters.remove(element);
			}

			if (element instanceof FieldMarkerGroup) {
				markerGroups.remove(((FieldMarkerGroup) element).getId());
				removedGroups.add(element);
			}

			if (element instanceof MarkerGroupingEntry) {
				MarkerGroupingEntry entry = (MarkerGroupingEntry) element;
				entry.getMarkerGroup().remove(entry);
				markerGroupingEntries.remove(entry.getId());
			}

			if (element instanceof String) {
				removeValues(element, categories);
			}

		}

		Iterator entriesIterator = markerGroupingEntries.keySet().iterator();
		Collection removedKeys = new ArrayList();
		while (entriesIterator.hasNext()) {
			String entryId = (String) entriesIterator.next();
			MarkerGroupingEntry entry = (MarkerGroupingEntry) markerGroupingEntries.get(entryId);
			if (removedGroups.contains(entry.getMarkerGroup())) {
				removedKeys.add(entryId);
			}
		}

		Iterator removedIterator = removedKeys.iterator();
		while (removedIterator.hasNext()) {
			markerGroupingEntries.remove(removedIterator.next());
		}

	}

	/**
	 * Remove the value from all of the collection sets in cache. If the collection is empty remove the key as well.
	 * 
	 * @param value
	 * @param cache
	 */
	private void removeValues(Object value, HashMap cache) {
		Collection keysToRemove = new ArrayList();
		Iterator keys = cache.keySet().iterator();
		while (keys.hasNext()) {
			String key = (String) keys.next();
			Object next = cache.get(key);
			if (next instanceof Collection) {
				Collection collection = (Collection) next;
				if (collection.contains(value)) {
					collection.remove(value);
					if (collection.isEmpty()) {
						keysToRemove.add(key);
					}
					break;
				}
			} else {
				if (cache.get(key).equals(value)) {
					keysToRemove.add(key);
				}
			}
		}
		Iterator keysToRemoveIterator = keysToRemove.iterator();
		while (keysToRemoveIterator.hasNext()) {
			cache.remove(keysToRemoveIterator.next());
		}
	}

	/**
	 * Get the category associated with marker. Return <code>null</code> if there are none.
	 * 
	 * @param marker
	 * @return String or <code>null</code>
	 */
	public String getCategory(IMarker marker) {
		try {
			return getCategory(marker.getType());
		} catch (CoreException e) {
			Util.log(e);
		}
		return null;
	}

	/**
	 * Get the category associated with markerType. Return <code>null</code> if there are none.
	 * 
	 * @param markerType
	 * @return String or <code>null</code>
	 */
	public String getCategory(String markerType) {
		if (categories.containsKey(markerType)) {
			return (String) categories.get(markerType);
		}
		return null;
	}

	/**
	 * Return the TableSorter that corresponds to type.
	 * 
	 * @param type
	 * @return TableSorter
	 */
	public TableComparator getSorterFor(String type) {
		if (hierarchyOrders.containsKey(type)) {
			return (TableComparator) hierarchyOrders.get(type);
		}

		TableComparator sorter = findSorterInChildren(type, getRootType());
		if (sorter == null) {
			return new TableComparator(new IField[0], new int[0], new int[0]);
		}
		return sorter;
	}

	/**
	 * Return the list of root marker types.
	 * 
	 * @return List of MarkerType.
	 */
	private MarkerType getRootType() {
		if (rootType == null) {
			rootType = MarkerTypesModel.getInstance().getType(IMarker.PROBLEM);
		}
		return rootType;
	}

	/**
	 * Find the best match sorter for typeName in the children. If it cannot be found then return <code>null</code>.
	 * 
	 * @param typeName
	 * @param type
	 * @return TableSorter or <code>null</code>.
	 */
	private TableComparator findSorterInChildren(String typeName, MarkerType type) {

		MarkerType[] types = type.getAllSubTypes();
		TableComparator defaultSorter = null;
		if (hierarchyOrders.containsKey(type.getId())) {
			defaultSorter = (TableComparator) hierarchyOrders.get(type.getId());
		}

		for (MarkerType element : types) {
			MarkerType[] subtypes = element.getAllSubTypes();
			for (MarkerType element2 : subtypes) {
				TableComparator sorter = findSorterInChildren(typeName, element2);
				if (sorter != null) {
					return sorter;
				}
			}
		}
		return defaultSorter;

	}

	/**
	 * Return the FieldMarkerGroups in the receiver.
	 * 
	 * @return Collection of FieldMarkerGroup
	 */
	public Collection getMarkerGroups() {
		return markerGroups.values();
	}

	/**
	 * Return the default group.
	 * 
	 * @return IField
	 */
	public IField getDefaultGroup() {

		return (IField) markerGroups.get(SEVERITY_ID);
	}

	public void addMarkerGroups(IField group) {
		markerGroups.put(group.getDescription(), group);
	}
}
