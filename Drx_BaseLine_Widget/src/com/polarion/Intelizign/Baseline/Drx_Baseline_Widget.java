/*
 * Copyright (C) 2004-2015 Polarion Software
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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

import com.polarion.alm.shared.api.SharedContext;
import com.polarion.alm.shared.api.model.PrototypeEnum;
import com.polarion.alm.shared.api.model.rp.parameter.ParameterFactory;
import com.polarion.alm.shared.api.model.rp.parameter.RichPageParameter;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidget;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidgetContext;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidgetDependenciesContext;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidgetRenderingContext;
import com.polarion.alm.shared.api.utils.collections.StrictMap;
import com.polarion.alm.shared.api.utils.collections.StrictMapImpl;
import com.polarion.alm.tracker.model.IWorkItem;


public class Drx_Baseline_Widget extends RichPageWidget {

    private static final String WORK_RECORDS_WIDGET_ICON ="icon.png";
    static final String PARAMETER_DOCUMENTS ="documents";
    static final String PARAMETER_SPACES ="spaces";
    static final String PARAMETER_BASELINE ="baseline";
    static final String PARAMETER_BASELINECOLLECTION ="baselineCollection";
    
   
    


    @Override
    @NotNull
    public String getIcon(@NotNull final RichPageWidgetContext context) {
        return context.resourceUrl(WORK_RECORDS_WIDGET_ICON);
    }

  
	@Override
    @NotNull

    public InputStream getResourceStream(@NotNull final String path) throws IOException {
    	System.out.println("Its working");
        InputStream stream = getClass().getResourceAsStream(path);
        System.out.println("Get Resource Stream is"+path);
        if (stream == null) {
            throw new IOException(String.format("Requested resource '%s' was not found.", path));
        }
        return stream;
    }

	@Override
	@NotNull
	public String getLabel(@NotNull final SharedContext context) {
		return "BaseLineWidget";
	}

    @Override
    @NotNull
    public String getDetailsHtml(@NotNull final RichPageWidgetContext widgetContext) {
        return "Get Existing BaseLine For The Selected Document";
    }

    @Override
    @NotNull
    public StrictMap<String, RichPageParameter> getParametersDefinition(@NotNull final ParameterFactory factory) {
    	
    
        StrictMap<String, RichPageParameter> parameters = new StrictMapImpl<String, RichPageParameter>();     
        parameters.put(PARAMETER_DOCUMENTS, factory.objectSelector("Document").allowedPrototypes(PrototypeEnum.Document).dependencySource(true).build());
        return parameters;
    }

    @Override
    @NotNull
    public String renderHtml(@NotNull  RichPageWidgetRenderingContext context) {
        return new Drx_Baseline_Widget_Render(context).render();
        
    }

    @Override
    public void processParameterDependencies(@NotNull RichPageWidgetDependenciesContext context) {
          new Drx_Baseline_Widget_Dependency(context).process();
    }

   
}
