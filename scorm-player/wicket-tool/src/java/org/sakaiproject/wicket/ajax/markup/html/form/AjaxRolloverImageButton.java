/*
 * #%L
 * SCORM Wicket Toolset
 * %%
 * Copyright (C) 2007 - 2016 Sakai Project
 * %%
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *             http://opensource.org/licenses/ecl2
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package org.sakaiproject.wicket.ajax.markup.html.form;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.resources.CompressedResourceReference;
import org.apache.wicket.model.Model;

/**
 * Ajax Button that implements javascript rollover images. By implementing getDisabledSrc(), 
 * getInactiveSrc(), and getActiveSrc(), the developer is able to indicate which images should be
 * displayed to the user under those conditions.
 * 
 * @author jrenfro
 */
public abstract class AjaxRolloverImageButton extends AjaxButton implements IHeaderContributor {
	private static final String ARIA_BUTTON_ROLE = "wairole:button";
	private static final ResourceReference ROLLOVER_SCRIPT = new CompressedResourceReference(AjaxRolloverImageButton.class, "res/ajax-image-button-rollover.js");
	
	public AjaxRolloverImageButton(String id) {
		super(id);
		add(new AttributeModifier("role", new Model(ARIA_BUTTON_ROLE)));
	}

	public AjaxRolloverImageButton(String id, Form form) {
		super(id, form);
		add(new AttributeModifier("role", new Model(ARIA_BUTTON_ROLE)));
	}

	protected abstract String getDisabledSrc();
	
	protected abstract String getInactiveSrc();
	
	protected abstract String getActiveSrc();
		
	protected void onComponentTag(final ComponentTag tag)
	{
		// Default handling for component tag
		super.onComponentTag(tag);

		String name = tag.getName();

		try
		{
			String value = getDefaultModelObjectAsString();
			if (value != null && !"".equals(value))
			{
				tag.put("value", value);
			}
		}
		catch (Exception e)
		{
			// ignore.
		}

		// If the subclass specified javascript, use that
		final String onClickJavaScript = getOnClickScript();
		if (onClickJavaScript != null)
		{
			tag.put("onclick", onClickJavaScript);
		}
		
		if (isEnabled()) {
			tag.put("src", getInactiveSrc());
			tag.put("onmouseover", "Wicket.ajaxRolloverImageButtonManager.activateBtn('" + this.getMarkupId() + "');");
			tag.put("onmouseout", "Wicket.ajaxRolloverImageButtonManager.inactivateBtn('" + this.getMarkupId() + "');");
		} else
			tag.put("src", getDisabledSrc());
	}
		
	public void renderHead(IHeaderResponse response) {
		response.renderJavascriptReference(ROLLOVER_SCRIPT);
		
		StringBuilder registerBtnScript = new StringBuilder();
		registerBtnScript.append("var regBtn = Wicket.registerAjaxRolloverImageButton('")
			.append(getMarkupId()).append("','").append(getActiveSrc()).append("','")
			.append(getInactiveSrc()).append("')");
		response.renderOnLoadJavascript(registerBtnScript.toString());
	}

		
}
