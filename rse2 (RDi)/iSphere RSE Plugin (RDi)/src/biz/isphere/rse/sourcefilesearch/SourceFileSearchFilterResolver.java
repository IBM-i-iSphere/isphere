/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.rse.sourcefilesearch;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.rse.core.filters.ISystemFilter;
import org.eclipse.rse.core.filters.ISystemFilterStringReference;
import org.eclipse.rse.core.filters.SystemFilterReference;
import org.eclipse.swt.widgets.Shell;

import com.ibm.etools.iseries.comm.filters.ISeriesMemberFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectFilterString;
import com.ibm.etools.iseries.comm.filters.ISeriesObjectTypeAttrList;
import com.ibm.etools.iseries.rse.ui.ResourceTypeUtil;
import com.ibm.etools.iseries.services.qsys.api.IQSYSMember;
import com.ibm.etools.iseries.services.qsys.api.IQSYSResource;
import com.ibm.etools.iseries.subsystems.qsys.api.IBMiConnection;

import biz.isphere.core.internal.ISeries;
import biz.isphere.core.sourcefilesearch.SearchElement;

/**
 * This class produces a list of {@link SearchElement} elements from a mixed
 * list of all kind of objects around an filter of the <i>Remote Systems</i>
 * view.
 */
public class SourceFileSearchFilterResolver {

    private Shell _shell;
    private IBMiConnection _connection;

    private Map<String, SearchElement> _searchElements;
    private ISeriesObjectFilterString _objectFilterString;
    private SourceFileSearchDelegate _delegate;
    private IProgressMonitor monitor;

    public SourceFileSearchFilterResolver(Shell shell, IBMiConnection connection) {
        this(shell, connection, null);
    }

    public SourceFileSearchFilterResolver(Shell shell, IBMiConnection connection, IProgressMonitor monitor) {
        this._shell = shell;
        this._connection = connection;
        this.monitor = monitor;
    }

    public Map<String, SearchElement> resolveRSEFilter(List<Object> _selectedElements) throws InterruptedException, Exception {

        _searchElements = new LinkedHashMap<String, SearchElement>();

        for (int idx = 0; idx < _selectedElements.size(); idx++) {

            if (isCanceled()) {
                break;
            }

            Object _object = _selectedElements.get(idx);

            if ((_object instanceof IQSYSResource)) {

                IQSYSResource element = (IQSYSResource)_object;

                if (ResourceTypeUtil.isLibrary(element)) {
                    addElementsFromLibrary(element);
                } else if ((ResourceTypeUtil.isSourceFile(element))) {
                    addElementsFromSourceFile(element.getLibrary(), element.getName());
                } else if (ResourceTypeUtil.isMember(element)) {
                    addElement(element);
                }

            } else if ((_object instanceof SystemFilterReference)) {

                SystemFilterReference filterReference = (SystemFilterReference)_object;
                String[] _filterStrings = filterReference.getReferencedFilter().getFilterStrings();
                addElementsFromFilterString(_filterStrings);

            } else if ((_object instanceof ISystemFilterStringReference)) {

                ISystemFilterStringReference filterStringReference = (ISystemFilterStringReference)_object;
                String[] _filterStrings = filterStringReference.getParent().getReferencedFilter().getFilterStrings();
                addElementsFromFilterString(_filterStrings);

            } else if ((_object instanceof ISystemFilter)) {

                ISystemFilter systemFilter = (ISystemFilter)_object;
                for (String filterString : systemFilter.getFilterStrings()) {
                    addElementsFromFilterString(filterString);
                }
            }

        }

        return _searchElements;
    }

    private void addElement(IQSYSResource element) {

        String library = element.getLibrary();
        String file = ((IQSYSMember)element).getFile();
        String member = element.getName();

        String key = SearchElement.produceKey(library, file, member);

        if (!_searchElements.containsKey(key)) {

            SearchElement _searchElement = new SearchElement();
            _searchElement.setLibrary(element.getLibrary());
            _searchElement.setFile(((IQSYSMember)element).getFile());
            _searchElement.setMember(element.getName());
            _searchElement.setType(element.getType());
            _searchElement.setDescription(((IQSYSMember)element).getDescription());
            _searchElements.put(key, _searchElement);

        }

    }

    private void addElementsFromSourceFile(String library, String sourceFile) throws InterruptedException, Exception {

        ISeriesMemberFilterString _memberFilterString = new ISeriesMemberFilterString();
        _memberFilterString.setLibrary(library);
        _memberFilterString.setFile(sourceFile);
        _memberFilterString.setMember("*"); //$NON-NLS-1$
        _memberFilterString.setMemberType("*"); //$NON-NLS-1$

        addElementsFromFilterString(_memberFilterString.toString());
    }

    private void addElementsFromLibrary(IQSYSResource element) throws InterruptedException, Exception {

        getObjectFilterString().setLibrary(element.getName());

        addElementsFromFilterString(_objectFilterString.toString());
    }

    private void addElementsFromFilterString(String... filterStrings) throws InterruptedException, Exception {

        getSourceFileSearchDelegate().addElementsFromFilterString(_searchElements, filterStrings);
    }

    private ISeriesObjectFilterString getObjectFilterString() {

        if (_objectFilterString == null) {
            _objectFilterString = new ISeriesObjectFilterString();
            _objectFilterString.setObject("*"); //$NON-NLS-1$
            _objectFilterString.setObjectType(ISeries.FILE);
            String attributes = "*FILE:PF-SRC *FILE:PF38-SRC"; //$NON-NLS-1$
            _objectFilterString.setObjectTypeAttrList(new ISeriesObjectTypeAttrList(attributes));
        }

        return _objectFilterString;
    }

    private SourceFileSearchDelegate getSourceFileSearchDelegate() {

        if (_delegate == null) {
            _delegate = new SourceFileSearchDelegate(_shell, _connection, monitor);
        }

        return _delegate;
    }

    private boolean isCanceled() {

        if (monitor == null) {
            return false;
        }

        return monitor.isCanceled();
    }
}
