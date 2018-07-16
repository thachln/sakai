/**
 * 
 */
package org.sakaiproject.scorm.ui.console.pages;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.include.Include;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.service.api.ScormContentService;


/**
 * @author Dell
 *
 */
public class UpPage extends ConsoleBasePage {
	private static final long serialVersionUID = 1L;
	private static final Log LOG = LogFactory.getLog(PackageRemovePage.class);

	@SpringBean(name="org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;

	public UpPage( final PageParameters params )
	{
		String title = params.getString( "title" );
		final long contentPackageId = params.getLong( "contentPackageId" );
		
		List<ContentPackage> lst = contentService.getContentPackages();
		int index = getIndexContentPackage(lst,contentPackageId);
		
		if(index > 0){
			ContentPackage pre = lst.get(index - 1);
			ContentPackage cur = lst.get(index);
			lst.set(index, pre);
			lst.set(index-1, cur);
			for(int i = 0; i< lst.size(); i++){
				ContentPackage tmp = new ContentPackage();
				tmp = lst.get(i);
				tmp.setViewSortOrder(i + 1);
				contentService.updateContentPackage(tmp);
			}			
		}
		
		setResponsePage( PackageListPage.class );
	}
	
	private int getIndexContentPackage(List<ContentPackage> lst, long contentPackageId){
		int result = 0;
		for(int i = 0; i< lst.size(); i++){
			if(contentPackageId == lst.get(i).getContentPackageId().longValue()){
				result = i;
			}
		}
		return result;
	}
}
