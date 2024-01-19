package com.polarion.Intelizign.Baseline;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


import com.polarion.alm.shared.api.Scope;
import com.polarion.alm.shared.api.model.document.Document;
import com.polarion.alm.shared.api.model.rp.parameter.ObjectSelectorParameter;
import com.polarion.alm.shared.api.model.rp.widget.RichPageWidgetDependenciesContext;


public class Drx_Baseline_Widget_Dependency{
	  @NotNull
	    private final RichPageWidgetDependenciesContext context;
	    @Nullable
	    
	    private final Scope DocumentProjectScope;
	    
	    
		public Drx_Baseline_Widget_Dependency(@NotNull RichPageWidgetDependenciesContext context) {

			this.context = context;
			ObjectSelectorParameter objParameter =context.parameter(Drx_Baseline_Widget.PARAMETER_DOCUMENTS);
			Document documentName = (Document) objParameter.value();
			
			DocumentProjectScope = documentName == null ? null :documentName.getReferenceToCurrent().scope();
			
			
		}

		public void process() {
			try {
				ObjectSelectorParameter parameter = context.parameter(Drx_Baseline_Widget.PARAMETER_DOCUMENTS);
				parameter.set().addCurrentScopeToDefaultQuery(true);
				
				
			} catch (Exception e) {
				System.out.println("The get message is" + e.getMessage());
			}

		}
	}