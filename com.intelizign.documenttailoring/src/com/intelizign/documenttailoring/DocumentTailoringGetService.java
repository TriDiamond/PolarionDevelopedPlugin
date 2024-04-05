package com.intelizign.documenttailoring;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.polarion.alm.projects.model.IFolder;
import com.polarion.alm.projects.model.IProject;
import com.polarion.alm.tracker.ITrackerService;
import com.polarion.alm.tracker.model.IModule;
import com.polarion.alm.tracker.model.ITrackerProject;
import com.polarion.alm.tracker.model.ITypeOpt;
import com.polarion.alm.tracker.model.IWorkItem;
import com.polarion.platform.core.PlatformContext;
import com.polarion.platform.persistence.UnresolvableObjectException;
import com.polarion.platform.persistence.model.IPObjectList;
import com.polarion.subterra.base.location.ILocation;
import com.polarion.subterra.base.location.Location;
import com.polarion.core.util.logging.Logger;

public class DocumentTailoringGetService {

	ITrackerService trackerService = (ITrackerService) PlatformContext.getPlatform()
			.lookupService(ITrackerService.class);
	private static final Logger log = Logger.getLogger(DocumentTailoringGetService.class);
	private LinkedHashMap<WorkItemModulePojo, WorkItemModulePojo> h2h3Map = new LinkedHashMap<WorkItemModulePojo, WorkItemModulePojo>();
	private Map<IWorkItem, IModule> emptyHeadingContent = new LinkedHashMap<>();
	private Map<Integer, Map<IWorkItem, IModule>> mappingDocumentHeadings = new LinkedHashMap<>();
	private List<MappingHeadingPojo> mappingHeadings = new ArrayList<>();
	private List<SubHeadingInfoPojo> subHeadingInfo = new ArrayList<>();
	private Map<String, Object> responseData = new HashMap<>();
	private List<IModule> loadedModules = new ArrayList<IModule>();
	private Gson gson = new Gson();
	private List<String> documentList = new ArrayList<>();
	private Map<String, String> projectList = new HashMap<String, String>();
	private String headingTag = "h2";
	private String subHeadingTag = "h3";

	/**
	 * The getEmptyHeadingIdFromHomePageContent method stores the selected Module
	 * and heading in an emptyHeadingContent object, if the heading has no
	 * subheading.
	 */
	public void getEmptyHeadingIdFromHomePageContent(Elements headTags, IModule selectedDocument)
			throws IllegalArgumentException, NullPointerException {
		for (Element headTag : headTags) {
			Elements subheadTags = new Elements();
			Node sibling = headTag.nextSibling();
			while (sibling != null && sibling instanceof Element && !((Element) sibling).tagName().equals(headingTag)) {
				if (((Element) sibling).tagName().equals(subHeadingTag)) {
					subheadTags.add((Element) sibling);
				}
				sibling = sibling.nextSibling();
			}
			if (subheadTags.isEmpty()) {
				String idValue = headTag.attr("id");
				String idParamValue = idValue.replaceAll("^.*id=", "");
				emptyHeadingContent.put(selectedDocument.getWorkItem(idParamValue), selectedDocument);
			}
		}
	}

	/**
	 * The addHeadingIdSubHeadingIdModuleIdInH2H3Map method stores the selected
	 * Module, heading ID, and subheading ID in the h2h3 object, if the heading has
	 * a subheading in the selected document.
	 */
	public void addHeadingIdSubHeadingIdModuleIdInH2H3Map(Elements h3Elements, IModule selectedDocument)
			throws IllegalArgumentException, NullPointerException {
		for (Element h3Element : h3Elements) {
			Element h2Element = h3Element.previousElementSibling();
			while (h2Element != null && !h2Element.tagName().equals(headingTag)) {
				h2Element = h2Element.previousElementSibling();
			}
			if (h2Element != null) {
				String[] headingh2ID = h2Element.id().split("params=");
				String[] headingh2IDPart = headingh2ID[1].split("=");
				String headingh2IDPartvalue = headingh2IDPart[1].split("\"></" + headingTag + ">")[0];
				String[] headingh3ID = h3Element.id().split("params=");
				String[] headingh3IDPart = headingh3ID[1].split("=");
				String headingh3IDPartvalue = headingh3IDPart[1].split("\"></" + subHeadingTag + ">")[0];
				h2h3Map.put(
						new WorkItemModulePojo(selectedDocument.getWorkItem(headingh3IDPartvalue),
								selectedDocument.getWorkItem(headingh2IDPartvalue), selectedDocument),
						new WorkItemModulePojo(selectedDocument.getWorkItem(headingh3IDPartvalue),
								selectedDocument.getWorkItem(headingh2IDPartvalue), selectedDocument));
			}
		}
	}

	/**
	 * The mapHeadingsMapSubheading method calls two methods: -
	 * getEmptyHeadingIdFromHomePageContent -
	 * addHeadingIdSubHeadingIdModuleIdInH2H3Map
	 */
	public void mapHeadingsMapSubheading(List<IModule> module) throws IllegalArgumentException, NullPointerException {
		for (IModule selectedDocument : module) {
			String extractedH1Headings = selectedDocument.getHomePageContent().toString();
			Document documents = Jsoup.parse(extractedH1Headings);
			Elements h2Tags = documents.select(headingTag);
			getEmptyHeadingIdFromHomePageContent(h2Tags, selectedDocument);
			Elements h3Elements = documents.select(subHeadingTag);
			addHeadingIdSubHeadingIdModuleIdInH2H3Map(h3Elements, selectedDocument);
		}
	}

	/**
	 * The mapHeadings method gets the heading ID in the selected document and
	 * stores mappingDocumentHeadingsObject
	 */
	public void mapHeadings(List<IModule> module) throws IllegalArgumentException, NullPointerException {
		AtomicInteger primaryKey = new AtomicInteger(0);
		module.forEach(selectedDocument -> {
			String extractedH1Headings = selectedDocument.getHomePageContent().toString();
			Document soup = Jsoup.parse(extractedH1Headings);
			List<Element> h2Tags = soup.getElementsByTag(headingTag);
			List<IWorkItem> workItems = h2Tags.stream().map(h2 -> h2.toString()).map(id -> {
				String[] parts = id.split("params=");
				String[] parts2 = parts[1].split("=");
				String paramsValue = parts2[1].split("\"></" + headingTag + ">")[0];
				return selectedDocument.getWorkItem(paramsValue);
			}).collect(Collectors.toList());
			workItems.forEach(wi -> {
				int key = primaryKey.incrementAndGet();
				mappingDocumentHeadings.put(key, new LinkedHashMap<>());
				mappingDocumentHeadings.get(key).put(wi, selectedDocument);
			});
		});
	}

	// ProjectList action get all project from the sever when jsp page
	// being refreshed
	public void getProjectList(HttpServletRequest req, HttpServletResponse resp) throws UnresolvableObjectException {
		try {
			PrintWriter out = resp.getWriter();
			HttpSession session = req.getSession();
			IPObjectList<IProject> getProjectList = trackerService.getProjectsService().searchProjects("", "id");
			for (IProject pro : getProjectList) {
				try {
					projectList.put(pro.getId(), pro.getName());
				} catch (UnresolvableObjectException e) {
					log.error("Skipping entry due to UnresolvableObjectException: " + e.getMessage());
				} catch (Exception e) {
					log.error("Exception is" + e.getMessage());
					continue;
				}
			}

			session.setAttribute("projectList", projectList);
			responseData.put("projectId", projectList);
			String jsonProjectResponse = gson.toJson(responseData);
			out.println(jsonProjectResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	

	// DocumentList action get all document from the selected project when user
	// select Project
	public void getDocumentList(HttpServletRequest req, HttpServletResponse resp) {
		try {
			PrintWriter out = resp.getWriter();
			Map<String, String> documentList = new HashMap<String, String>();
			if (!(documentList.isEmpty())) {
				documentList.clear();
			}
			String selectedProject = req.getParameter("selectedProjectId");
			ITrackerProject trackerProject = trackerService.getTrackerProject(selectedProject);
			List<IFolder> folders = trackerService.getFolderManager().getFolders(selectedProject);
			for (IFolder folder : folders) {
				String folderName = folder.getName();
				ILocation location = Location.getLocation(folderName);
				List<IModule> modules = trackerService.getModuleManager().getModules(trackerProject, location);
				for (IModule module : modules) {
					try {
						documentList.put(module.getId(), module.getModuleName());
					} catch (UnresolvableObjectException e) {
						log.error("Skipping entry due to UnresolvableObjectException: " + e.getMessage());
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
			responseData.put("documentList", documentList);
			String jsonDocumentResponse = gson.toJson(responseData);
			out.println(jsonDocumentResponse);
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	// DocumentDetails action send mappingHeadingObject, loadedModules ,h2h3Map
	// to the frontend in json Format when user click Show Button
	public void getSelectedDocumentDetails(HttpServletRequest req, HttpServletResponse resp)
			throws UnresolvableObjectException {
		try {
			HttpSession session = req.getSession();
			String selectedDocuments = req.getParameter("selectedDocument");
			ObjectMapper objectMapper = new ObjectMapper();
			String selectedPro = req.getParameter("projectId");
			if (!(documentList.isEmpty())) {
				documentList.clear();
			}
			List<String> documentList = objectMapper.readValue(selectedDocuments, new TypeReference<List<String>>() {
			});
			if (!(loadedModules.isEmpty())) {
				loadedModules.clear();
			}

			if (!(mappingDocumentHeadings.isEmpty())) {
				mappingDocumentHeadings.clear();
			}

			if (!(h2h3Map.isEmpty())) {
				h2h3Map.clear();
			}

			if (!(emptyHeadingContent.isEmpty())) {
				emptyHeadingContent.clear();
			}
			loadedModules = trackerService.getFolderManager().getFolders(selectedPro).stream().flatMap(folder -> {
				ILocation location = Location.getLocation(folder.getName());
				ITrackerProject project = trackerService.getTrackerProject(selectedPro);
				List<IModule> documentModule = trackerService.getModuleManager().getModules(project, location);
				return documentModule.stream();
			}).filter(module -> documentList.contains(module.getModuleName()))
					.sorted(Comparator.comparingInt(module -> documentList.indexOf(module.getModuleName())))
					.collect(Collectors.toList());
			if (!(loadedModules.isEmpty())) {
				mapHeadings(loadedModules);
				mapHeadingsMapSubheading(loadedModules);
				session.setAttribute("document", loadedModules);
				session.setAttribute("emptyHeadingContent", emptyHeadingContent);
				session.setAttribute("mappingH2andH3Heading", h2h3Map);
			}
			;
			if (!(mappingHeadings.isEmpty())) {
				mappingHeadings.clear();
			}
			mappingHeadings = getMappingHeadings(mappingDocumentHeadings);
			if (!(subHeadingInfo.isEmpty())) {
				subHeadingInfo.clear();
			}
			subHeadingInfo = getHeadingAndSubheading(h2h3Map);
			String mappingHeadingsJson = objectMapper.writeValueAsString(mappingHeadings);
			String subHeadingInfoJson = objectMapper.writeValueAsString(subHeadingInfo);
			String documents = objectMapper.writeValueAsString(documentList);
			resp.setContentType("application/json");
			resp.getWriter().write("{\"mappingHeadings\":" + mappingHeadingsJson + ",\"myResponse\":"
					+ subHeadingInfoJson + ",\"Document\":" + documents + "}");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public List<MappingHeadingPojo> getMappingHeadings(Map<Integer, Map<IWorkItem, IModule>> mappingDocumentHeadings)
			throws UnresolvableObjectException {
		for (Map.Entry<Integer, Map<IWorkItem, IModule>> entry : mappingDocumentHeadings.entrySet()) {
			Map<IWorkItem, IModule> innerMap = entry.getValue();
			for (Map.Entry<IWorkItem, IModule> innerEntry : innerMap.entrySet()) {
				String title;
				try {
					title = innerEntry.getKey().getTitle();
				} catch (UnresolvableObjectException e) {
					log.error("Skipping entry due to UnresolvableObjectException: " + e.getMessage());
					continue;
				} catch (Exception e) {
					log.error("Exception is " + e.getMessage());
					continue;
				}
				mappingHeadings.add(new MappingHeadingPojo(innerEntry.getKey(), innerEntry.getValue(), title));
			}
		}
		return mappingHeadings;
	}

	public List<SubHeadingInfoPojo> getHeadingAndSubheading(
			LinkedHashMap<WorkItemModulePojo, WorkItemModulePojo> h2h3Map) throws UnresolvableObjectException {
		for (Map.Entry<WorkItemModulePojo, WorkItemModulePojo> subheading : h2h3Map.entrySet()) {
			try {
				String subHeadingId = subheading.getKey().getKey().getId();
				String subHeadingTitle;
				try {
					subHeadingTitle = subheading.getKey().getKey().getTitle();
				} catch (UnresolvableObjectException e) {
					log.error("Skipping subheading due to UnresolvableObjectException: " + e.getMessage());
					continue;
				}
				String headingId = subheading.getValue().getValue().getId();
				String moduleName = subheading.getValue().getModule().getId();
				subHeadingInfo.add(new SubHeadingInfoPojo(subHeadingId, subHeadingTitle, headingId, moduleName));
			} catch (Exception e) {
				log.error("Exception is " + e.getMessage());
				continue;
			}
		}
		return subHeadingInfo;
	}

	// DefaultProjectDetails action get currentProject document types and folders
	// when user
	// click create document button
	@SuppressWarnings("unchecked")
	public void getCurrentProjectDetails(HttpServletRequest req, HttpServletResponse resp)
			throws UnresolvableObjectException {
		try {
			HttpSession session = req.getSession();
			PrintWriter out = resp.getWriter();
			Map<String, String> projectList = (Map<String, String>) session.getAttribute("projectList");
			String defaultLoadedProject = req.getParameter("projectId");
			ITrackerProject pro = trackerService.getTrackerProject(defaultLoadedProject);
			Map<String, String> mapFolder = new LinkedHashMap<String, String>();
			Map<String, String> mapCurrentModuleType = new LinkedHashMap<String, String>();
			String projectName = pro.getName();

			ITrackerProject currentTrackerProject = trackerService.getTrackerProject(defaultLoadedProject);
			List<ITypeOpt> getModuleType = currentTrackerProject.getModuleTypeEnum().getAllOptions();
			mapCurrentModuleType = getModuleType.stream().collect(Collectors.toMap(ITypeOpt::getId, ITypeOpt::getName,
					(originalModuleObject, duplicatedModuleObject) -> originalModuleObject, LinkedHashMap::new));
			List<IFolder> getFolder = currentTrackerProject.getFolders();
			mapFolder = getFolder.stream().collect(Collectors.toMap(IFolder::getName, IFolder::getTitle,
					(originalModuleObject, duplicatedModuleObject) -> originalModuleObject, LinkedHashMap::new));
			responseData.put("projectList", projectList);
			responseData.put("mapCurrentModuleType", mapCurrentModuleType);
			responseData.put("mapFolder", mapFolder);
			responseData.put("projectName", projectName);
			responseData.put("projectId", defaultLoadedProject);
			String jsonCreateDocumentResponse = gson.toJson(responseData);
			out.println(jsonCreateDocumentResponse);
		} catch (Exception e) {
			log.error("Exception is" + e.getMessage());
		}

	}

	// ProjectListUpdate action get selected project documentTypes and folders when
	// user select project in Create Document Page
	public void getFolderListAndDocumentListForSelectedProject(HttpServletRequest req, HttpServletResponse resp) {
		try {
			String selectedProjectId = req.getParameter("projectId");
			PrintWriter out = resp.getWriter();
			ITrackerProject currentTrackerProject = trackerService.getTrackerProject(selectedProjectId);
			Map<String, String> selectedProjectModuleType = new LinkedHashMap<String, String>();
			Map<String, String> selectedProjecFolderObject = new LinkedHashMap<String, String>();
			List<ITypeOpt> getModuleType = currentTrackerProject.getModuleTypeEnum().getAllOptions();
			selectedProjectModuleType = getModuleType.stream()
					.collect(Collectors.toMap(ITypeOpt::getId, ITypeOpt::getName,
							(originalModuleObject, duplicatedModuleObject) -> originalModuleObject,
							LinkedHashMap::new));
			List<IFolder> getFolder = currentTrackerProject.getFolders();
			selectedProjecFolderObject = getFolder.stream()
					.collect(Collectors.toMap(IFolder::getName, IFolder::getTitle,
							(originalModuleObject, duplicatedModuleObject) -> originalModuleObject,
							LinkedHashMap::new));

			responseData.put("selectedProjectModuleType", selectedProjectModuleType);
			responseData.put("selectedProjecFolderObject", selectedProjecFolderObject);
			String jsonProjectListUpdateResponse = gson.toJson(responseData);
			out.println(jsonProjectListUpdateResponse);

		} catch (Exception e) {
			log.error("Exception is" + e.getMessage());
		}
	}

}