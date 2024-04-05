/* Document Builder Plugin is developed by intelizign Lifecycle services pvt ltd
 * Developer : benish.bm@intelizign.com */

package com.intelizign.documenttailoring;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.polarion.core.util.logging.Logger;


public class DocumentTailoringServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger log = Logger.getLogger(DocumentTailoringServlet.class);
	private DocumentTailoringGetService tailoringGetService = new DocumentTailoringGetService();
	private DocumentTailoringPostService tailoringPostService = new DocumentTailoringPostService();

	
	/**
	 * Recieving Post request From the Frontend and send response to the Json Format From
	 * Backend
	 */	

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			//tailoringPostService.createDocument(req,resp);	case "getPartEvaluationData":
			tailoringPostService.getPartEvaluationData(req,resp);
		}catch(Exception e) {
			log.error("Exception is" + e.getMessage());
		}
	}
	
	/**
	 * Recieving Get request From the Frontend and send response to the Json Format From
	 * Backend
	 */	
	@Override
	protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("Its working");
		String action = req.getParameter("action");
		try {
			switch (action) {
			case "getProjectList":
				System.out.println("Its working projectList");
				tailoringGetService.getProjectList(req, resp);
				break;
			case "getDocumentList":
				tailoringGetService.getDocumentList(req, resp);
				break;
			case "getDocumentDetails":
				tailoringGetService.getSelectedDocumentDetails(req, resp);
				break;
			case "getDefaultProjectDetails":
				tailoringGetService.getCurrentProjectDetails(req, resp);
				break;
			case "getFolderListAndDocumentListForSelectedProject":
				tailoringGetService.getFolderListAndDocumentListForSelectedProject(req, resp);
				break;
			default:
				throw new IllegalArgumentException("Invalid action specified");
			}
		} catch (Exception e) {
			log.error("Exception is" + e.getMessage());
		}
	}
}
