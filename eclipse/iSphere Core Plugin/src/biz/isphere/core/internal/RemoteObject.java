/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.internal;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.QSYSObjectPathName;

import biz.isphere.core.ibmi.contributions.extension.handler.IBMiHostContributionsHandler;

/**
 * The RemoteObject class is used by the RDI and WDSCi plug-in as an adapter to
 * pass QSYSRemoteObjects (RDi) and ISeriesObjects (WDSCi) to the core plug-in.
 */
public class RemoteObject {

    private String connectionName;
    private String name;
    private String library;
    private String objectType;
    private String description;

    private AS400 system;

    public static RemoteObject newLibrary(String connectionName, String libraryName) {
        return new RemoteObject(connectionName, libraryName, "QSYS", ISeries.LIB, null);
    }

    public static RemoteObject newFile(String connectionName, String fileName, String libraryName) {
        return new RemoteObject(connectionName, fileName, libraryName, ISeries.FILE, null);
    }

    public RemoteObject(String connectionName, String name, String library, String objectType, String description) {
        this.connectionName = connectionName;
        this.name = name;
        this.library = library;
        this.objectType = objectType;
        this.description = description;

        if (!objectType.startsWith("*")) {
            throw new IllegalArgumentException("Invalid object type. Object type must start with an asterisk: " + objectType);
        }
    }

    public RemoteObject(AS400 system, String name, String library, String objectType, String description) {
        this.system = system;
        this.name = name;
        this.library = library;
        this.objectType = objectType;
        this.description = description;

        if (!objectType.startsWith("*")) {
            throw new IllegalArgumentException("Invalid object type. Object type must start with an asterisk: " + objectType);
        }
    }

    public void setConnectionName(String connectionName) {
        if (this.connectionName != null) {
            throw new IllegalAccessError("Attribute 'connectionName' cannot be changed.");
        }
        this.connectionName = connectionName;
    }

    public AS400 getSystem() {
        if (system == null) {
            system = IBMiHostContributionsHandler.getSystem(connectionName);
        }
        return system;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public String getName() {
        return name;
    }

    public String getLibrary() {
        return library;
    }

    public String getObjectType() {
        return objectType;
    }

    public String getDescription() {
        return description;
    }

    public String getQSYSName() {

        StringBuilder buffer = new StringBuilder();

        buffer.append(library);

        if (!ISeries.LIB.equals(objectType)) {
            buffer.append(library);
            buffer.append("/"); //$NON-NLS-1$
            buffer.append(name);
        }

        return buffer.toString();
    }

    public String getQualifiedObject() {
        return library + "/" + name + " (" + objectType + ")";
    }

    public String getAbsoluteName() {
        return connectionName + ":" + library + "/" + name + "(" + objectType + ")";
    }

    public QSYSObjectPathName getObjectPathName() {
        return new QSYSObjectPathName(library, name, objectType.substring(1));
    }

    public String getToolTipText() {
        return "\\\\" + connectionName + "\\QSYS.LIB\\" + library + ".LIB\\" + name + "." + objectType;
    }

    @Override
    public String toString() {
        return getAbsoluteName();
    }
}
