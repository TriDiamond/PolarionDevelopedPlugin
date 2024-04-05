package com.intelizign.documenttailoring;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polarion.alm.projects.model.IFolder;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.ILinkRoleOpt;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.ITypeOpt;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.core.util.types.Text;
import com.polarion.platform.ITransactionService;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.UnresolvableObjectException;
import com.polarion.platform.security.ISecurityService;
import com.polarion.subterra.base.location.ILocation;
import com.polarion.subterra.base.location.Location;
import com.polarion.core.util.logging.Logger;

public class DocumentTailoringPostService {
	private ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private ISecurityService securityService = (ISecurityService) PlatformContext.getPlatform()
			.lookupService(ISecurityService.class);
	private ITransactionService transactionservice = (ITransactionService) PlatformContext.getPlatform()
			.lookupService(ITransactionService.class);
	private static final Logger log = Logger.getLogger(DocumentTailoringPostService.class);
	private Map<WorkItemModulePojo, WorkItemModulePojo> mappedObject;
	private Map<IWorkItem, IModule> emptyHeadings;
	private LinkedHashSet<IWorkItem> headingAndSubheadingIds = new LinkedHashSet<IWorkItem>();
	private LinkedHashSet<IModule> moduleList = new LinkedHashSet<IModule>();
	private Map<Integer, Map<IWorkItem, IModule>> subheadingAndDocumentMap = new LinkedHashMap<>();
	private boolean hasPermissionInCreateDocument;
	private String warningMessage;

	
	/* 	
	 * multiExtractingContent method Extracting heading content From the selected
	 * Document and append to the sb object
	 */
	public StringBuilder multiExtractingContent(LinkedHashSet<IWorkItem> headingandSubheadingid,
			LinkedHashSet<IModule> moduleList) throws IllegalArgumentException {
		StringBuilder sb = new StringBuilder();
		moduleList.stream().forEach(module -> {
			String homePageContent = module.getHomePageContent().toString();
			Element document = Jsoup.parse(homePageContent);
			headingandSubheadingid.stream().forEach(headings -> {
				if (module.containsWorkItem(headings)) {
					String idToFind = "polarion_wiki macro name=module-workitem;params=id=" + headings.getId() + "";
					Element element = document.getElementById(idToFind);
					if (element.tagName().equals("h3") || element.tagName().equals("h2")) {
						sb.append(element.outerHtml());

						Node next = element.nextSibling();
						while (next != null) {
							if (next instanceof Element) {
								if (((Element) next).tagName().equals("h3")
										|| ((Element) next).tagName().equals("h2")) {
									break;
								} else {
									sb.append(((Element) next).outerHtml());
								}
							}
							next = next.nextSibling();
						}
					}
				}
			});
		});
		return sb;
	}

	/*
	 * addheadingSubheadingModuleTosubheadingAndDocumentMapObject method get
	 * heading,subheading and moduleName from the emptyHeadings object,mappedObject
	 * Add to subheadingAndDocumentMap object
	 */
	public void addheadingSubheadingModuleToSubheadingAndDocumentMapObject(List<IModule> selectedDocumentList,
			List<String> headingList) throws IllegalArgumentException, NullPointerException {
		AtomicInteger id = new AtomicInteger(0);
		subheadingAndDocumentMap.clear();	
		selectedDocumentList.forEach(s -> headingList.forEach(iterateHeadings -> {
			if (emptyHeadings != null) {
				emptyHeadings.entrySet().stream()
						.filter(entry -> entry.getKey().getId().equalsIgnoreCase(iterateHeadings)
								&& entry.getValue().getId().equalsIgnoreCase(s.getModuleName()))
						.forEach(entry -> {
							subheadingAndDocumentMap.put(id.get(), new LinkedHashMap<>());
							subheadingAndDocumentMap.get(id.get()).put(entry.getKey(), entry.getValue());
							id.getAndIncrement();
						});
			}
			if (mappedObject != null) {
				mappedObject.entrySet().stream()
						.filter(iterateMappedObject -> iterateMappedObject.getValue().getValue().getId()
								.equalsIgnoreCase(iterateHeadings)
								|| iterateMappedObject.getKey().getKey().getId().equalsIgnoreCase(iterateHeadings))
						.filter(iterateMappedObject -> iterateMappedObject.getValue().getModule().getId()
								.equalsIgnoreCase(s.getModuleName()))
						.forEach(iterateMappedObject -> {
							IWorkItem subheading = iterateMappedObject.getKey().getKey();
							IModule documentName = iterateMappedObject.getValue().getModule();
							IWorkItem heading = iterateMappedObject.getValue().getValue();
							subheadingAndDocumentMap.put(id.get(), new LinkedHashMap<>());
							if (iterateMappedObject.getValue().getValue().getId().equalsIgnoreCase(iterateHeadings)) {
								subheadingAndDocumentMap.get(id.get()).put(heading, documentName);
							} else if (iterateMappedObject.getKey().getKey().getId()
									.equalsIgnoreCase(iterateHeadings)) {
								subheadingAndDocumentMap.get(id.get()).put(subheading, documentName);
							}
							id.getAndIncrement();
						});
			}
		}));
	}

	/**
	 * getHeadingModuleFromsubheadingAndDocumentMapObject method get heading,Module
	 * From subheadingAndDocumentMap object and add module to the moduleList object
	 * heading add to the headingAndSubheadingIds object
	 */
	public void getHeadingModuleFromSubheadingAndDocumentMapObject()
			throws IllegalArgumentException, NullPointerException {
		if (!(headingAndSubheadingIds.isEmpty())) {
			headingAndSubheadingIds.clear();
		}
		if (!(moduleList.isEmpty())) {
			moduleList.clear();
		}
		subheadingAndDocumentMap.entrySet().stream()
				.flatMap(subheadingEntry -> subheadingEntry.getValue().values().stream()).forEach(moduleList::add);
		subheadingAndDocumentMap.entrySet().stream()
				.flatMap(subheadingEntry -> subheadingEntry.getValue().keySet().stream())
				.forEach(headingAndSubheadingIds::add);;
	}

	/**
	 * If user hasn't permission to create a document send warning message to the
	 * user
	 */
	public String reportWarningMessage(String user) throws IllegalArgumentException {
		String userString = "The user";
		String message = "doesn't have permission to create Document in this Project. Please contact Polarion Administrator";
		String warning = userString + " " + user + "  " + message;
		return warning;
	}

	/**
	 * getAllHeadingsAddInSession method set all heading in session
	 */
	public void getAllHeadingsAddInSession(HttpSession session, ObjectMapper mapper, String requestBody)
			throws NullPointerException, IOException, JsonMappingException {
		StringBuilder sb = new StringBuilder();
		BufferedReader reader = new BufferedReader(new StringReader(requestBody));
		String line;
		while ((line = reader.readLine()) != null) {
			sb.append(line);
		}
		requestBody = sb.toString();
		JsonNode root = mapper.readTree(requestBody);
		JsonNode allHeadingsNode = root.get("allheadings");
		List<String> allHeadings = new ArrayList<>();
		for (int i = 0; i < allHeadingsNode.size(); i++) {
			allHeadings.add(allHeadingsNode.get(i).textValue());
		}
		session.setAttribute("allHeadings", allHeadings);
	}

	// get currentDocument Module Type object
	public List<ITypeOpt> getModuleTypeObject(ITrackerProject trackerProject) {
		Map<String, String> mapModuleType = new LinkedHashMap<>();
		List<ITypeOpt> moduleTypes = trackerProject.getModuleTypeEnum().getAllOptions();
		moduleTypes.forEach(mod -> {
			mapModuleType.put(mod.getId(), mod.getName());
		});
		return moduleTypes;
	}

	@SuppressWarnings("unchecked")
	public void createDocument(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		HttpSession session = req.getSession();
		String requestBody = IOUtils.toString(req.getInputStream(), StandardCharsets.UTF_8);
		mappedObject = (Map<WorkItemModulePojo, WorkItemModulePojo>) session.getAttribute("mappingH2andH3Heading");
		emptyHeadings = (Map<IWorkItem, IModule>) session.getAttribute("emptyHeadingContent");
		List<IModule> selectedDocumentList = (List<IModule>) session.getAttribute("document");
		Gson gson = new Gson();
		Map<String, Object> data = gson.fromJson(requestBody, Map.class);
		ObjectMapper mapper = new ObjectMapper();
		String currentUser = securityService.getCurrentUser();
		String selectedProject = (String) data.get("selectedProjectId");
		String moduleTitle = (String) data.get("ModuleTitle");
		String moduleName = (String) data.get("ModuleName");
		String moduleType = (String) data.get("ModuleType");
		String space = (String) data.get("space");

		// check user has permission to create Document in this selected space
		hasPermissionInCreateDocument = trackerService.getTrackerProject(selectedProject).can().createDocument(space);
		getAllHeadingsAddInSession(session, mapper, requestBody);
		List<String> selectedHeadingsInSession = (List<String>) session.getAttribute("allHeadings");
        addheadingSubheadingModuleToSubheadingAndDocumentMapObject(selectedDocumentList, selectedHeadingsInSession);
		getHeadingModuleFromSubheadingAndDocumentMapObject();
		StringBuilder homePageContent = multiExtractingContent(headingAndSubheadingIds, moduleList);
		ITrackerProject trackerProject = trackerService.getTrackerProject(selectedProject);
		List<ITypeOpt> moduleTypes = getModuleTypeObject(trackerProject);
		List<ITypeOpt> workItemType = trackerProject.getWorkItemTypeEnum().getAllOptions();
		List<ILinkRoleOpt> linkRole = trackerProject.getWorkItemLinkRoleEnum().getAllOptions();
		if (hasPermissionInCreateDocument) {
			transactionservice.beginTx();
			List<IFolder> allFolders = trackerService.getFolderManager().getFolders(selectedProject);
			java.util.Optional<IFolder> selectedFolder = allFolders.stream()
					.filter(f -> f.getName().equalsIgnoreCase(space)).findFirst();
			ILinkRoleOpt link = linkRole.stream().findFirst().orElse(null);
			if (selectedFolder.isPresent()) {
				ILocation location = Location.getLocation(selectedFolder.get().getName());
				moduleTypes.stream().filter(type -> type.getId().equals(moduleType)).findFirst().ifPresent(type -> {
					IProject pro = trackerService.getProjectsService().getProject(selectedProject);
					IModule m = null;
					m = trackerService.getModuleManager().createModule(pro, location, moduleName, workItemType, link,
							true);
					m.setType(type);
					homePageContent.insert(0, "<h1> " + moduleTitle + " </h1>");
					Text selectedTextContent = Text.html(homePageContent.toString());
					m.setHomePageContent(selectedTextContent);
					m.setTitle(moduleTitle);
					try {
						m.save();
						transactionservice.endTx(false);
					} catch (Exception e) {
						log.error("Exception is" + e.getMessage());
					}
					DocumentCreationMessagePojo message = new DocumentCreationMessagePojo(m.getId(), m.getTitle(), m.getModuleFolder(),
							m.getProjectId(), m.getType().getId());
					String creationMessage = null;
					String errorModule = null;
					try {
						creationMessage = mapper.writeValueAsString(message);
						errorModule = mapper.writeValueAsString(moduleName);
					} catch (JsonProcessingException e) {
						log.error("Json Processing Exception" + e.getMessage());
					}
					resp.setContentType("application/json");
					try {
						resp.getWriter().write("{\"message\":" + creationMessage + ",\"ModuleName\":" + errorModule
								+ ",\"warningMessage\":" + null + "}");
					} catch (IOException e) {
						log.error("creationMessageException is " + e.getMessage());
					}
				});
			}
		} else {
			warningMessage = reportWarningMessage(currentUser);
			String warningMessageNotify = mapper.writeValueAsString(warningMessage);
			resp.setContentType("application/json");
			resp.getWriter().write("{\"warningMessage\":" + warningMessageNotify + "}");
		}
	}
	
	/*************************************************/

	// Function to read JSON data from file
	public static String readJSONFile(String filePath) throws IOException {
		return new String(Files.readAllBytes(Paths.get(filePath)));
	}

	 // Function to get components by category
	public SimpleEntry<List<String>, List<String>> getComponentsByCategory(String jsonData, String category) throws IOException {
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<Map<String, String>> dataList = objectMapper.readValue(jsonData, new TypeReference<List<Map<String, String>>>() {});
	    System.out.println("Data List is" + dataList + "\n");
	    
	    // Collect components under the specified category
	    List<String> components = new ArrayList<>();
	    List<String> allComponents = new ArrayList<>();
	    
	    for (Map<String, String> item : dataList) {
	        allComponents.add(item.get("component"));
	        if (category.equalsIgnoreCase(item.get("category"))) {
	            components.add(item.get("component"));
	        }
	    }

	    return new SimpleEntry<>(components, allComponents);
	}

	
	

	public void getPartEvaluationData(HttpServletRequest req, HttpServletResponse resp)
			throws UnresolvableObjectException, IOException {
		System.out.println("Its working");
		String filePath = "D:/JSON/sample.json";
		 StringBuilder requestBody = new StringBuilder();
	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(req.getInputStream()))) {
	            String line;
	            while ((line = reader.readLine()) != null) {
	                requestBody.append(line);
	            }
	        }

	        // Parse the JSON data
	        ObjectMapper mapper = new ObjectMapper();
	        JsonNode requestData = mapper.readTree(requestBody.toString());

	        // Extract values from the JSON object
	        String wiID = requestData.get("wiID").asText();
	        String projectID = requestData.get("projectID").asText();
	        String componentVal = requestData.get("componentVal").asText();
	        String categoryVal = requestData.get("categoryVal").asText();

	        // Now you can use these values as needed
	        System.out.println("wiID: " + wiID);
	        System.out.println("projectID: " + projectID);
	        System.out.println("componentVal: " + componentVal);
	        System.out.println("categoryVal: " + categoryVal);
        try {
            String jsonData = readJSONFile(filePath);

            // Get components by category
            SimpleEntry<List<String>, List<String>> result = getComponentsByCategory(jsonData, categoryVal);
            List<String> components = result.getKey();
            List<String> allComponents = result.getValue();
            System.out.println("List of Components"+components);
            // Create response object
            Map<String, Object> responseObject = new HashMap<>();
            responseObject.put("components", components);
            responseObject.put("allComponents", allComponents);
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonResponse = objectMapper.writeValueAsString(responseObject);

            resp.setContentType("application/json");

            resp.getWriter().write(jsonResponse);
            
        } catch (IOException e) {
            System.err.println("Error reading JSON file: " + e.getMessage());
        }
	}

	/*****************************************************/
	
	
	
	
}
