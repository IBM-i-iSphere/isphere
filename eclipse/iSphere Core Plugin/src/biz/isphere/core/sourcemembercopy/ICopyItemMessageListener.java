/*******************************************************************************
 * Copyright (c) 2012-2024 iSphere Project Owners
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.sourcemembercopy;

import biz.isphere.core.sourcemembercopy.rse.CopyMembersJob;

public interface ICopyItemMessageListener {

    /**
     * Methods called by the {@link CopyMembersJob} for each member error.
     * 
     * @param errorId - ID identifying the error
     * @param item - copy member item in error
     * @param errorMessage - error message text
     * @return true for canceling the job; otherwise false
     */
    public SynchronizeMembersAction reportCopyMemberMessage(MemberCopyError errorId, CopyMemberItem item, String errorMessage);
}
