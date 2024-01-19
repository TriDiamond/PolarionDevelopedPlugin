/*
` * Copyright (C) 2004-2015 Polarion Software
 * All rights reserved.
 * Email: dev@polarion.com
 *
 *
 * Copyright (C) 2004-2015 Polarion Software
 * All Rights Reserved.  No use, copying or distribution of this
 * work may be made except in accordance with a valid license
 * agreement from Polarion Software.  This notice must be
 * included on all copies, modifications and derivatives of this
 * work.
 *
 * POLARION SOFTWARE MAKES NO REPRESENTATIONS OR WARRANTIES
 * ABOUT THE SUITABILITY OF THE SOFTWARE, EITHER EXPRESSED OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE, OR NON-INFRINGEMENT. POLARION SOFTWARE
 * SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT
 * OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
 *
 */
package com.polarion.Intelizign.Baseline;


import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.polarion.alm.shared.api.model.ModelObject;
import com.polarion.alm.shared.api.model.baseline.Baseline;
import com.polarion.alm.shared.api.model.document.Document;
import com.polarion.alm.shared.api.model.rp.parameter.ObjectSelectorParameter;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidgetRenderingContext;
import com.polarion.alm.shared.api.utils.html.HtmlAttributesBuilder;
import com.polarion.alm.shared.api.utils.html.HtmlFragmentBuilder;
import com.polarion.alm.shared.api.utils.html.HtmlTagBuilder;
import com.polarion.alm.shared.api.utils.links.PortalLink;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IBaseline;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.model.IPObject;
import com.polarion.platform.persistence.model.IPObjectList;



public class Drx_Baseline_Widget_Render{
    @NotNull
    private final RichPageWidgetRenderingContext context;
    private String document;
    private static String projectId;
    private static String document_Name;
    private static String document_space;
    private static String warningMessage ;
	
	List<IBaseline> baseRef = new ArrayList<>();

	ArrayList<ModelObject> baselineObject;
	
	public Drx_Baseline_Widget_Render(@NotNull final RichPageWidgetRenderingContext context) {
		this.context = context;

		document = getDocument();

		if (document == null) {
			return;
		}
	}

	@NotNull
	public String render() {
		HtmlFragmentBuilder builders = context.createHtmlFragmentBuilder();
		if (document != null) {
			HtmlFragmentBuilder builder = context.createHtmlFragmentBuilder();
			
		  HtmlTagBuilder openScriptTag =  builder.tag().style();
		  HtmlAttributesBuilder attributeFirst =openScriptTag.attributes().className("div-class");
		  attributeFirst.style("border-color:black;border-style:solid;");
		  openScriptTag.finished();
		   
		HtmlTagBuilder  divclass = builder.tag().div();
		divclass.attributes().className("div-class");
		divclass.append().text("");
		divclass.finished();
	        
			getTableHeadingsRow(builder);
			
			return builder.toString();
		}
		return builders.toString();
	}

	private void reportWarning(@NotNull String message) {
		warningMessage = message;
	}

	@SuppressWarnings("unused")
	@Nullable
	private String getDocument() {

		ObjectSelectorParameter parameter = context.parameter(Drx_Baseline_Widget.PARAMETER_DOCUMENTS);

		Document documentName = (Document) parameter.value();

		if (documentName == null) {
			reportWarning("Document was not selected");
			return null;
		}

		projectId = documentName.getOldApi().getProjectId();

		document_Name = documentName.getOldApi().getId();

		document_space = documentName.getOldApi().getModuleFolder();

		ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
				.lookupService(ITrackerService.class);

		String documentQuery = "select baseline.c_uri from baseline inner join module on baseline.fk_uri_baseobject=module.c_uri inner join "
				+ "project on module.fk_uri_project=project.c_uri where true  and project.c_id='"
				+ projectId + "' and  module.c_id='" + documentName.getOldApi().getId() + "'";

		IPObjectList<IPObject> sqlSearch = trackerService.getDataService().sqlSearch(documentQuery);

		baselineObject = context.transaction().objects().searchBySql(documentQuery).toArrayList();

		for (int searchValues = 0; searchValues < sqlSearch.size(); searchValues++) {
			IBaseline baseline = (IBaseline) sqlSearch.get(searchValues);
			baseRef.add(baseline);
		}

		return documentName.getOldApi().getModuleName();

	}

	private String getTableHeadingsRow(HtmlFragmentBuilder builder) {

		HtmlTagBuilder table = builder.tag().table();
		table.attributes().style("border-collapse:collapse;border-spacing:0;width:90%;");

		HtmlTagBuilder tableHeadingRow = table.append().tag().tr();
		HtmlTagBuilder dataHeading1 = tableHeadingRow.append().tag().th();
		dataHeading1.attributes().style(
				"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;"
						+ "overflow:hidden;padding:10px 5px;word-break:normal;width: 10%;background-color:#D9D9D9;font-weight:bold;text-align:center;vertical-align:bottom");

		dataHeading1.append().text("BaseLine Revision");
		dataHeading1.finished();

		HtmlTagBuilder dataHeading2 = tableHeadingRow.append().tag().th();
		dataHeading2.attributes().style(
				"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;"
						+ "overflow:hidden;padding:10px 5px;word-break:normal;width: 25%;background-color:#D9D9D9;font-weight:bold;text-align:center;vertical-align:bottom");

		dataHeading2.append().text("BaseLine Name");
		dataHeading2.finished();

		HtmlTagBuilder dataHeading3 = tableHeadingRow.append().tag().th();
		dataHeading3.attributes().style(
				"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;"
						+ "overflow:hidden;padding:10px 5px;word-break:normal;width: 20%;background-color:#D9D9D9;font-weight:bold;text-align:center;vertical-align:bottom");

		dataHeading3.append().text("Author");
		dataHeading3.finished();

		HtmlTagBuilder dataHeading4 = tableHeadingRow.append().tag().th();
		dataHeading4.attributes().style(
				"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;"
						+ "overflow:hidden;padding:10px 5px;word-break:normal;width: 15%;background-color:#D9D9D9;font-weight:bold;text-align:center;vertical-align:bottom");

		dataHeading4.append().text("Description");

		HtmlTagBuilder dataHeading5 = tableHeadingRow.append().tag().th();
		dataHeading5.attributes().style(
				"border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;font-weight:normal;"
						+ "overflow:hidden;padding:10px 5px;word-break:normal;width: 20%;background-color:#D9D9D9;font-weight:bold;text-align:center;vertical-align:bottom");
		dataHeading5.append().text("Created");

		dataHeading5.finished();
		tableHeadingRow.finished();

		getTableContentRow(table, builder);

		return builder.toString();

	}

	private void getTableContentRow(HtmlTagBuilder table, HtmlFragmentBuilder builder) {

		
		for (ModelObject iterateBaseRef : baselineObject) {

			Baseline obj = (Baseline) iterateBaseRef;
			PortalLink baseRevisionUrl = context.transaction().context().createPortalLink().project(projectId)
					.document(document_space, document_Name).revision(obj.fields().baseRevision().get());

			HtmlTagBuilder tableContentRow = table.append().tag().tr();
			HtmlTagBuilder dataContentCell1 = tableContentRow.append().tag().td();
			dataContentCell1.attributes()
					.style("border-color:black;border-style:solid;border-width:1px;font-family:Arial, "
							+ "sans-serif;font-size:14px;overflow:hidden;width: 10%;;padding:10px 5px;word-break:normal;text-align:center;vertical-align:bottom");
			HtmlTagBuilder rowContentCell2 = dataContentCell1.append().tag().a();
			rowContentCell2.attributes().href(baseRevisionUrl);
			obj.fields().baseRevision().render().htmlTo(rowContentCell2.append());
			dataContentCell1.finished();

			HtmlTagBuilder dataContentCell2 = tableContentRow.append().tag().td();
			dataContentCell2.attributes()
					.style("border-color:black;border-style:solid;border-width:1px;font-family:Arial, "
							+ "sans-serif;font-size:14px;overflow:hidden;width: 25%;padding:10px 5px;word-break:normal;text-align:left;vertical-align:bottom");
			obj.render().htmlTo(dataContentCell2.append());
			dataContentCell2.finished();

			HtmlTagBuilder dataContentCell3 = tableContentRow.append().tag().td();
			dataContentCell3.attributes()
					.style("border-color:black;border-style:solid;border-width:1px;font-family:Arial, "
							+ "sans-serif;font-size:14px;overflow:hidden;width: 20%;padding:10px 5px;word-break:normal;text-align:center;vertical-align:bottom");
			obj.fields().author().render().htmlTo(dataContentCell3.append());
			dataContentCell3.finished();

			HtmlTagBuilder dataContentCell4 = tableContentRow.append().tag().td();
			dataContentCell4.attributes()
					.style("border-color:black;border-style:solid;border-width:1px;font-family:Arial, "
							+ "sans-serif;font-size:14px;width: 15%;overflow:hidden;padding:10px 5px;word-break:normal;text-align:left;vertical-align:bottom");
			obj.fields().description().render().htmlTo(dataContentCell4.append());
			dataContentCell4.finished();

			for (IBaseline baseObjRef : baseRef) {
				if (baseObjRef.getBaseRevision().equalsIgnoreCase(obj.fields().baseRevision().get())) {
					HtmlTagBuilder dataContentCell5 = tableContentRow.append().tag().td();
					dataContentCell5.attributes()
							.style("border-color:black;border-style:solid;border-width:1px;font-family:Arial, "
									+ "sans-serif;font-size:14px;width: 20%;overflow:hidden;padding:10px 5px;word-break:normal;text-align:left;vertical-align:bottom");
					dataContentCell5.append().text(baseObjRef.getBaseRevisionObject().getCreated().toString());
					dataContentCell5.finished();
				}

			}

			tableContentRow.finished();

		}

		table.finished();

	}

}
