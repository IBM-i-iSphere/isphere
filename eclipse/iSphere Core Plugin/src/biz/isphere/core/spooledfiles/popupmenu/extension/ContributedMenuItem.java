/*******************************************************************************
 * Copyright (c) 2012-2021 iSphere Project Team
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 *******************************************************************************/

package biz.isphere.core.spooledfiles.popupmenu.extension;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

import biz.isphere.core.spooledfiles.SpooledFile;
import biz.isphere.core.spooledfiles.popupmenu.extension.point.ISpooledFilePopupMenuContributionItem;

public class ContributedMenuItem {

    private static final String CONTRIBUTION_ITEM = "CONTRIBUTION_ITEM";

    private Shell shell;
    private ISpooledFilePopupMenuContributionItem contributionItem;
    private MenuItem menuItem;
    private SelectionListener selectionListener;

    public static ISpooledFilePopupMenuContributionItem getContributionItem(MenuItem menuItem) {
        return (ISpooledFilePopupMenuContributionItem)menuItem.getData(CONTRIBUTION_ITEM);
    }

    public ContributedMenuItem(Shell shell, Menu parent, ISpooledFilePopupMenuContributionItem contributionItem) {
        this.shell = shell;
        this.contributionItem = contributionItem;

        this.menuItem = new MenuItem(parent, SWT.PUSH);
        this.menuItem.setText(contributionItem.getText());
        // Not available in WDSCi 7.0
        // this.menuItem.setToolTipText(contributionItem.getTooltipText());
        this.menuItem.setImage(contributionItem.getImage());
        this.menuItem.setData(CONTRIBUTION_ITEM, contributionItem);
    }

    public void setSelectionListener(SelectionListener listener) {
        this.selectionListener = listener;
        menuItem.addSelectionListener(listener);
    }

    public void setSelection(SpooledFile[] spooledFiles) {
        contributionItem.setSelection(shell, spooledFiles);
        menuItem.setEnabled(contributionItem.isEnabled());
    }

    public void execute() throws Exception {
        contributionItem.execute();
    }

    public boolean isDisposed() {
        return menuItem.isDisposed();
    }

    public void dispose() {
        menuItem.removeSelectionListener(selectionListener);
        menuItem.dispose();
    }
}
