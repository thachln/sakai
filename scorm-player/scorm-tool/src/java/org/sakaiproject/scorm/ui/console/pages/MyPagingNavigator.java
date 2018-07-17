package org.sakaiproject.scorm.ui.console.pages;

import org.apache.wicket.markup.html.navigation.paging.IPageable;
import org.apache.wicket.markup.html.navigation.paging.IPagingLabelProvider;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;

public class MyPagingNavigator extends PagingNavigator {
	public MyPagingNavigator(String id, IPageable pageable) {
        super(id, pageable);
    }

    public MyPagingNavigator(String id, IPageable pageable, IPagingLabelProvider labelProvider) {
        super(id, pageable, labelProvider);
    }
}
