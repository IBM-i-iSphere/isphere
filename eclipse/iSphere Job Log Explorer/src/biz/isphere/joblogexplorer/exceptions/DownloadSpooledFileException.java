/*******************************************************************************
 * Copyright (c) 2012-2020 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.joblogexplorer.exceptions;

public class DownloadSpooledFileException extends BasicJobLogLoaderException {

    private static final long serialVersionUID = -5774432537841998966L;

    public DownloadSpooledFileException(String message) {
        super(message);
    }

    @Override
    public String getMessage() {
        String message = super.getMessage();
        return message;
    }
}
