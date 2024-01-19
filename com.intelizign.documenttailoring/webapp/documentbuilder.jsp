
<%@page
	import="com.polarion.platform.internal.service.repository.listeners.job.SystemOverloadedException"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>

<head>
<meta charset="ISO-8859-1" />
<style>
.table {
	border-collapse: collapse;
	border-spacing: 0;
	width: 100%;
	margin-top: 8px;
	margin-bottom: 25px;
	border: 1px solid #ddd;
}

.table tr:nth-child(even) {
	background-color: #f2f2f2;
}

.table td {
	font-size: 14px;
	overflow: hidden;
	padding: 10px 5px;
	word-break: normal;
	vertical-align: top;
}

.table th {
	font-size: 14px;
	font-weight: normal;
	overflow: hidden;
	padding: 10px 5px;
	word-break: normal;
	color: white;
	width: 50%;
}

.table .th {
	background-color: steelBlue;
	font-weight: bold;
	text-align: left;
	vertical-align: bottom;
}

.main-container {
	width: 500px;
	border: 2px solid #ededed;
	padding: 5px;
	box-shadow: rgba(0, 0, 0, 0.24) 0px 3px 8px;
}

.project-container {
	background-color: #ededed;
	padding: 2px 8px;
	margin-bottom: 5px;
}

.input-check-box {
	background-color: #ededed;
	padding-left: 5px;
	padding-right: 5px;
	padding-top: 2px;
	margin-bottom: 8px;
	overflow: hidden;
}

label {
	color: grey;
}

.input-btn, .input-btn-cancel {
	white-space: nowrap;
	cursor: pointer;
	background-color: #005f87;
	border: 1px solid #003750;
	transition: background-color 0.3s #025f87;
	color: white;
	text-align: center;
	font-size: 18px;
	font-weight: bold;
	padding: 12px 8px;
	min-width: 100px;
}

.input-btn-cancel {
	background-color: #ffffff;
	transition: background-color 0.3s #fafafa;
	color: #005f87;
}

.input-btn-div {
	padding: 5px;
}

div.createDialogBox {
	display: block;
	margin-left: auto;
	margin-right: auto;
	background-color: white;
	opacity: 1;
	min-height: 150px;
	width: 50%;
	margin-top: 90px;
	border: 2px solid #ededed;
	border-radius: 5px;
	padding: 10px;
}

A, TD, LI, UL, INPUT, BUTTON, OPTION, P {
	font-family: "Segoe UI", "Selawik", "Open Sans", Arial, sans-serif;
	font-size: 14px;
}

DIV {
	font-family: "Segoe UI", "Selawik", "Open Sans", Arial, sans-serif;
	font-size: 18px;
}

.successPopupDiv {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	display: none;
}

.errorPopupDiv {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background-color: rgba(0, 0, 0, 0.5);
	display: none;
}

.dialogBoxPopupDiv {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100vh;
	background-color: rgba(0, 0, 0, 0.5);
	display: none;
}

.title {
	font-size: 13px;
	font-weight: bold;
	color: gray;
}

.form-control {
	padding: 5px 4px !important;
	font-weight: bold;
	width: 100%;
	border: 1px solid gray;
	font-size: 14px;
	margin-bottom: 10px;
	margin-top: 3px;
}

.show-subHeading-div {
	text-align: left;
	padding-bottom: 3px;
}

.show-subHeading-div label {
	font-size: 14px;
}

.text-theame {
	color: #005f87 !important;
}

.p-t-20 {
	padding-top: 20px;
}

.headingTitle {
	color: black;
	font-weight: bold;
}

.pull-right {
	text-align: right;
}

.loader {
	position: absolute;
	top: 30%;
	right: 50%;
	transform: translate(-50%, -50%);
	border: 16px solid #f3f3f3;
	border-radius: 50%;
	border-top: 16px solid #3498db;
	width: 50px;
	height: 50px;
	animation: spin 2s linear infinite;
}

/* Safari */
@
-webkit-keyframes spin { 0% {
	-webkit-transform: rotate(0deg);
}

100
%
{
-webkit-transform
:
rotate(
360deg
);
}
}
@
keyframes spin { 0% {
	transform: rotate(0deg);
}
100
%
{
transform
:
rotate(
360deg
);
}
}
</style>
</head>

<body>
	<div class="main-container" style="margin-bottom: 20px">
		<div class="project-container">
			<label class="title">Project</label> <select class="form-control"
				id="projectDropDown" name="projectDiv" onChange="projectInfo()">
				<option>Select</option>
			</select>
		</div>

		<div class="project-container">
			<label class="title"> Document</label> <select class="form-control"
				id="documentSelection" name="Document" multiple="multiple"
				style="height: 30px" onClick="multielectingFunction()">
				<option value="">Select</option>
			</select>
		</div>

		<div class="show-subHeading-div">
			<input type="checkbox" id="showSecondColumnCheckbox" /> <label>Include
				Sub Heading</label>
		</div>

		<div class="input-btn-div">
			<button class="input-btn" id="submit" type="submit"
				onClick="applyFunction()">Show</button>
			<button class="input-btn" id="createDocumentButton"
				style="display: none" onclick="createDialogboxFunction()">
				Create Document</button>
		</div>
	</div>

	<div class="dialogBoxPopupDiv">
		<div class="createDialogBox" id="createDialogBox"
			style="display: none">
			<div style="padding-bottom: 15px">
				<label class="headingTitle text-theame">Create Document</label>
			</div>
			<div class="project-container">
				<label for="documentTitle" class="title"> <span
					style="color: red">*</span>Title:
				</label> <input class="form-control" type="text" id="documentTitle"
					style="width: 98%" onkeydown="documentNameFunction()" />
			</div>

			<div class="project-container">
				<label for="documentName" class="title"> <span
					style="color: red">*</span>Name(ID):
				</label> <input class="form-control" type="text" id="documentName"
					style="width: 98%" />
			</div>

			<div class="project-container">
				<label for="projectList" class="title"> <span
					style="color: red">*</span>Project:
				</label> <select class="form-control" id="projectList"
					onchange="selectProjectFunction()"></select>
			</div>

			<div class="project-container">
				<label for="documentFolder" class="title"><span
					style="color: red">*</span>Space:</label> <select class="form-control"
					id="documentFolder">
					<option>Select</option>
				</select>
			</div>

			<div class="project-container">
				<label for="documentType" class="title"><span
					style="color: red">*</span>Document Type:</label> <select
					class="form-control" id="documentType">
					<option>Select</option>
				</select>
			</div>

			<div class="input-btn-div">
				<input type="button" class="input-btn" id="saveId"
					onclick="saveFunction()" value="Save" /> <input type="button"
					class="input-btn-cancel" id="cancelId" onclick="closeFunction()"
					value="Cancel" />
			</div>
		</div>
	</div>

	<div class="loader" id="myloader" style="display: none;"></div>

	<div class="successPopupDiv" id="successPopupDiv">
		<div id="successMessageDiv" class="createDialogBox">
			<div style="text-align: center">
				<label class="headingTitle" style="color: green; font-size: 18px">Success</label>
			</div>

			<p class="title text-theame">Document Created Successfully</p>

			<div>
				<table class="table">
					<tr>
						<td class="title" style="width: 30%">Project Id</td>
						<td><span id="projectIdSpan"></span></td>
					</tr>
					<tr>
						<td class="title" style="width: 30%">Module Id</td>
						<td><span id="moduleIdSpan"></span></td>
					</tr>
					<tr>
						<td class="title" style="width: 30%">Title</td>
						<td><span id="titleIdSpan"></span></td>
					</tr>
					<tr>
						<td class="title" style="width: 30%">Document Space</td>
						<td><span id="spaceSpan"></span></td>
					</tr>
				</table>
			</div>
			<div>
				<input type="button" class="input-btn" id="closeMessage"
					onclick="closeMessageFunction()" value="Close" />
			</div>
		</div>
	</div>

	<div class="successPopupDiv" id="errorPopupDiv">
		<div id="errorMessageDiv" class="createDialogBox">
			<div style="text-align: center">
				<label class="headingTitle" style="color: red; font-size: 18px">Error</label>
			</div>

			<p class="title text-theame">Document exists with the same name
				in this space,kindly choose a different name or select different
				space.</p>

			<div>
				<div>
					<table class="table">
						<tr>
							<td class="title" style="width: 30%">Module Id</td>
							<td><span id="showTitleErrorSpanTag"></span></td>
						</tr>
						<tr>
							<td class="title" style="width: 30%">space</td>
							<td><span id="showSpaceErrorTag"></span></td>
						</tr>
					</table>
				</div>

				<div>
					<input type="button" class="input-btn" id="closeErrorMessage"
						onclick="errorMessageFunction()" value="Close" />
				</div>
			</div>
		</div>
	</div>
</body>

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
<script>
/**
 * on loading the page, the below function triggers and call the documentBuiderContentPage
 * there we get all project and add to the object then send the object in json
 * format to the frontend.After we bind the values to the Project selectElement
 */
 window.addEventListener("load", function () {
	  fetch("Tailoring?action=getProjectList", {
	    method: "GET",
	    headers: {
	      "Content-Type": "application/json",
	    },
	  })
	    .then(function (response) {
	      if (!response.ok) {
	        throw new Error(`Response status: ${response.status}`);
	      }
	      return response.json();
	    })
	    .then(function (data) {
	      const projectId = data.projectId;
	      const selectElement = document.getElementById("projectDropDown");
	      for (const [id, name] of Object.entries(projectId)) {
	        const option = document.createElement("option");
	        option.value = id;
	        option.text = name;
	        selectElement.appendChild(option);
	      }
	    })
	    .catch(function (error) {
	      console.error("Error message is", error.message);
	    });
	});
/**
 * When user change the Project in front end that time trigger below function
 *There we send request using Fetch Api to the backend.In backend we add all
 *document inside the documentList object .Then send Frontend in json Format
 *there we bind the values to the Document select Element
 */
	    function projectInfo() {
	        const projectId = document.getElementById("projectDropDown").value
	        const selectElement = document.getElementById("documentSelection")
	
	        fetch("Tailoring?action=getDocumentList&selectedProjectId="+ projectId, {
	            method: "GET",
	            headers: {
	                "Content-Type": "application/json",
	            },
	          
	        })
	           .then(function (response) {
	            if (!response.ok) {
	            throw new Error(`Response status: ${response.status}`);
	            }
	             return response.json();
	            })
	            .then(function (data) {
	                const DocumentList = data.documentList
	                const selectOption = document.createElement("option")
	                document.getElementById("documentSelection").innerHTML = ""
	                selectOption.value = ""
	                selectOption.text = "Select"
	                document
	                    .getElementById("documentSelection")
	                    .appendChild(selectOption)
	                for (const [id, name] of Object.entries(DocumentList)) {
	                    const option = document.createElement("option")
	                    option.value = id
	                    option.text = name
	                    selectElement.appendChild(option)
	                }
	                $("#documentSelection").css("height", "100px")
	            })
	            .catch((error) => {
	                console.error(error.message)
	            })
	    }
	
/**
 *Below function trigger After user click the Apply Button it send request to backend From backend
 *we get mappingHeadings object  and mappingHeadingAndSubheadings object .with the help of this 
 *object we create a table inside the function.
 */
	    function applyFunction() {
	        const projectId = document.getElementById("projectDropDown").value
	        const selectMultiDocument = $("select#documentSelection").val()
	        if (projectId !== "Select" && selectMultiDocument !== null) {
	            $("#createDocumentButton").css("display", "inline-block")
	        } else {
	            if (projectId === "Select") {
	                alert("Please Select Project")
	                return
	            }
	            if (selectMultiDocument === null) {
	                alert("Please Select Document")
	                return
	            }
	        }
	        var selectedDocument = $("#documentSelection").find(":selected")
	        var documents = [];
	        for (var i = 0; i < selectedDocument.length; i++) {
	            documents.push(selectedDocument[i].innerText)
	        }
	        const encodedDocuments = encodeURIComponent(JSON.stringify(documents));
	        console.log("Encoded Documents is" +encodedDocuments );	
	        fetch("Tailoring?action=getDocumentDetails&selectedDocument="+encodedDocuments+"&projectId="+projectId, {
	            method: "GET",
	            headers: {
	                "Content-Type": "application/json",
	            },
	        })
	            .then(function(response)  {
	                if (!response.ok) {
	                    throw new Error("Network response was not ok")
	                }
	                return response.json();
	            })
	
	            .then(function(data) {
	                if (data.length === 0) {
	                    throw new Error("data is undefined or has a length of 0.")
	                }
	                const mappingHeadings = data.mappingHeadings
	                const myResponse = data.myResponse
	                const Document = data.Document
	                const container = document.createElement("div")
	                container.id = "tableContainer"
	                container.classList.add("tableContainerClass")
	                const uniqueModuleNames = [];
	                const uniqueMappingHeadings = mappingHeadings.filter((item) => {
	                    if (!uniqueModuleNames.includes(item.moduleName)) {
	                        uniqueModuleNames.push(item.moduleName);
	                        return true;
	                    }
	                    return false;
	                })
	
	                const showSecondColumnContainer = document.createElement("div")
	                showSecondColumnContainer.classList.add("p-t-20")

	                //Check if a table with the tableContainer ID already exists
	                const existingTable = document.getElementById("tableContainer")
	                if (existingTable) {
	                    // Remove the existing table
	                    existingTable.remove();
	                }
	                try {
	                    for (let doc = 0; doc < Document.length; doc++) {
	                        const documents = Document[doc];
	                        const moduleName = uniqueModuleNames.find(name => name === documents);
	                        if (moduleName === documents) {
	                            const h2 = document.createElement("label");
	                            h2.classList.add("headingTitle");
	                            h2.classList.add("text-theame");
	                            h2.textContent = moduleName
	                            container.appendChild(h2);
	                            const table = document.createElement("table");
	                            table.classList.add("table");
	                            const tableHead = document.createElement("thead");
	                            const headRow = document.createElement("tr");
	                            const headingTh = document.createElement("th");
	                            headingTh.classList.add("th");
	                            headingTh.textContent = "Heading";
	                            headRow.appendChild(headingTh);
	                            const subHeadingTh = document.createElement("th");
	                            subHeadingTh.classList.add("th");
	                            subHeadingTh.textContent = "Sub Heading";
	                            headRow.appendChild(subHeadingTh);
	                            tableHead.appendChild(headRow);
	                            table.appendChild(tableHead);
	                            const tableBody = document.createElement("tbody");
	                            for (let j = 0; j < mappingHeadings.length; j++) {
	                                if (moduleName === mappingHeadings[j].moduleName) {
	                                    const row = document.createElement("tr");
	                                    const headingTd = document.createElement("td");
	                                    const headingCheckbox = document.createElement("input");
	                                    headingCheckbox.setAttribute("type", "checkbox");
	                                    headingCheckbox.value = mappingHeadings[j].id;
	                                    headingCheckbox.addEventListener("change", function () {
	                                        const subHeadingCheckboxes = subHeadingTd.querySelectorAll(
	                                            'input[type="checkbox"]'
	                                        );
	                                        for (let i = 0; i < subHeadingCheckboxes.length; i++) {
	                                            subHeadingCheckboxes[i].checked = this.checked;
	                                        }
	                                    });
	                                    headingTd.appendChild(headingCheckbox);
	                                    headingTd.appendChild(
	                                        document.createTextNode(mappingHeadings[j].wiTitle)
	                                    );
	                                    row.appendChild(headingTd);
	                                    const subHeadingTd = document.createElement("td");
	                                    for (let k = 0; k < myResponse.length; k++) {
	                                        if (
	                                            mappingHeadings[j].id === myResponse[k].headingId &&
	                                            moduleName === myResponse[k].moduleName
	                                        ) {
	                                            // select heading checkbox respective child heading checkbox enable
	                                            const subHeadingCheckbox = document.createElement("input");
	                                            subHeadingCheckbox.setAttribute("type", "checkbox");
	                                            subHeadingCheckbox.value = myResponse[k].subHeadingId;
	                                            subHeadingTd.appendChild(subHeadingCheckbox);
	                                            subHeadingTd.appendChild(
	                                                document.createTextNode(myResponse[k].subHeadingTitle)
	                                            );
	                                            subHeadingTd.appendChild(document.createElement("br"));
	                                        }
	                                    }
	                                    row.appendChild(subHeadingTd);
	                                    tableBody.appendChild(row);
	
	                                }
	                            }
	                            table.appendChild(tableBody)
	                            container.appendChild(table)
	                            document.body.appendChild(container)
	                        } else {
	                            alert(`The selected Document '${documents}' contains no heading and subheading`)
	                        }
	                    }
	                } catch (error) {
	                    console.error("An error occurred:", error)
	                }
	                const showSecondColumnCheckboxId = document.querySelector(
	                    "#showSecondColumnCheckbox"
	                );
	
	                //index of the subheading column
	                const subHeadingColumnIndex = 1
	
	                const containerId = document.querySelector(".tableContainerClass")
	
	                // hide subheading column by default
	                const tables = containerId.querySelectorAll("table")
	                for (let j = 0; j < tables.length; j++) {
	                    const subHeadingTds = tables[j].querySelectorAll(
	                        "tbody tr td:nth-child(" + (subHeadingColumnIndex + 1) + ")"
	                    );
	                    const subHeadingTh = tables[j].querySelector(
	                        "thead tr th:nth-child(" + (subHeadingColumnIndex + 1) + ")"
	                    );
	                    for (let i = 0; i < subHeadingTds.length; i++) {
	                        if (showSecondColumnCheckboxId.checked) {
	                            subHeadingTds[i].style.display = "table-cell"
	                            subHeadingTh.style.display = "table-cell"
	                        } else {
	                            subHeadingTds[i].style.display = "none"
	                            subHeadingTh.style.display = "none"
	                        }
	                    }
	                }
	                // add event listener to show/hide subheading column
	                showSecondColumnCheckboxId.addEventListener("change", function () {
	                    const tables = containerId.querySelectorAll("table");
	                    for (let j = 0; j < tables.length; j++) {
	                        const subHeadingTds = tables[j].querySelectorAll(
	                            "tbody tr td:nth-child(" + (subHeadingColumnIndex + 1) + ")"
	                        );
	                        const subHeadingTh = tables[j].querySelector(
	                            "thead tr th:nth-child(" + (subHeadingColumnIndex + 1) + ")"
	                        );
	                        for (let i = 0; i < subHeadingTds.length; i++) {
	                            if (this.checked) {
	                                subHeadingTds[i].style.display = "table-cell"
	                                subHeadingTh.style.display = "table-cell"
	                            } else {
	                                subHeadingTds[i].style.display = "none"
	                                subHeadingTh.style.display = "none"
	                            }
	                        }
	                    }
	                });
	            })
	            .catch((error) => {
	                console.log("error message is", error.message)
	            })
    }	
/**
 * Below Function its helps to show createDialog Box page there was add project
 *values , document types and ,Document space to that creationDialogBoxPage
 */
	    function createDialogboxFunction() {
	    	   const urlParams = new URLSearchParams(window.location.search);
	    	   const projectId = urlParams.get('projectId');
	    	if(projectId === null ){
	    		alert("Set Project Id in extension url Parameter")
	    	}else{
	        fetch("Tailoring?action=getDefaultProjectDetails&projectId="+projectId, {
	            method: "GET",
	            headers: {
	                "Content-Type": "application/json",
	            },
	           
	        })
	            .then(function(response) {
	                if (!response.ok) {
	                    throw new Error("Network response was not ok")
	                }
	                return response.json()
	            })
	            .then(function(data)  {
	                $(".dialogBoxPopupDiv").css("display", "block")
	                $("#createDialogBox").css("display", "block")
	
	                const selectElement = document.getElementById("projectList")
	                const selectOption = document.createElement("option")
	                const projectList = data.projectList
	                const mapCurrentModuleType = data.mapCurrentModuleType
	                const mapFolder = data.mapFolder
	                const projectId = data.projectId
	                const projectName = data.projectName
	
	                selectOption.value = projectId
	                selectOption.text = projectName
	
	                selectElement.appendChild(selectOption);
	
	                Object.entries(projectList).forEach(([id, name]) => {
	                    const option = document.createElement("option");
	                    option.value = id
	                    option.textContent = name
	                    document.getElementById("projectList").appendChild(option)
	                });
	
	                Object.entries(mapCurrentModuleType).forEach(([id, name]) => {
	                    const option = document.createElement("option")
	                    option.value = id
	                    option.textContent = name
	                    document.getElementById("documentType").appendChild(option)
	                });
	
	                Object.entries(mapFolder).forEach(([id, name]) => {
	                    const option = document.createElement("option")
	                    option.value = id
	                    option.textContent = name
	                    document.getElementById("documentFolder").appendChild(option)
	                });
	            })
	            .catch((error) => { })
	    	}
	    }
	
/**
 * Below function its help to when enter the value in  Title text box
 *that value automatically populated to the Name text box in creation DialogBoxPage
 */
	
	    function documentNameFunction() {
	        document
	            .getElementById("documentTitle")
	            .addEventListener("input", function (event) {
	                document.getElementById("documentName").value = event.target.value
	            })
	    }
/**
 * In this selectProjectFunction trigger when user change the project in create DialogBoxPage
 * Based on the project what are the documentType and Space there in the project its bind to that
 *documentType and Space element in frontend
 */
	    function selectProjectFunction() {
	        var projectId = document.getElementById("projectList").value
	
	        fetch("Tailoring?action=getFolderListAndDocumentListForSelectedProject&projectId="+projectId, {
	            method: "GET",
	            headers: {
	                "Content-Type": "application/json",
	            },
	  
	        })
	            .then(function(response)  {
	                if (!response.ok) {
	                    throw new Error("Network response was not ok")
	                }
	                return response.json()
	            })
	            .then(function (data)  {
	                const selectElement = document.getElementById("documentFolder")
	                const selectOption = document.createElement("option")
	                const select = document.getElementById("documentFolder")
	                const selectedmapCurrentModuleType =
	                    data.selectedProjectModuleType
	                const selectedmapFolder = data.selectedProjecFolderObject
	
	                document.getElementById("documentFolder").innerHTML = ""
	                selectOption.value = ""
	                selectOption.text = "select"
	
	                document.getElementById("documentFolder").appendChild(selectOption)
	
	                // Add the new option
	                Object.entries(selectedmapFolder).forEach(([id, name]) => {
	                    const option = document.createElement("option")
	                    option.value = id
	                    option.textContent = name
	                    document.getElementById("documentFolder").appendChild(option)
	                })
	
	                const selectOptions = document.createElement("option")
	
	                document.getElementById("documentType").innerHTML = ""
	                selectOptions.value = ""
	                selectOptions.text = "select"
	                document.getElementById("documentType").appendChild(selectOptions)
	                Object.entries(selectedmapCurrentModuleType).forEach(([id, name]) => {
	                    const option = document.createElement("option")
	                    option.value = id
	                    option.textContent = name
	                    document.getElementById("documentType").appendChild(option)
	                });
	            })
	            .catch((error) => {
	                // Log the error message
	                console.error(error.message)
	            })
	    }
	
/**
 * This Function helps to create New Document .Here we send request to
 *backend.In backend using createModule Api to create NewDocument
 *If sucessfull message to show in successMessage Div and error message also displayed in frontend
 *based on what responses getting From backend
 */
	    function saveFunction() {
	        var project = document.getElementById("projectList").value
	        var documentTitle = document.getElementById("documentTitle").value
	        var documentName = document.getElementById("documentName").value
	        var documentType = document.getElementById("documentType").value
	        var folder = document.getElementById("documentFolder").value
	        var headings = []
	        if (documentName === "") {
	            alert("Please Enter the Document Id:")
	            return
	        }
	        if (documentTitle === "") {
	            alert("Please enter Document Title:")
	            return
	        }
	        if (folder === "Select") {
	            alert("Please Select Select Space:")
	            return
	        }
	
	        if (documentType === "Select") {
	            alert("Please Select Document Type:")
	            return
	        }
	
	        $.each($("input[type='checkbox']:checked"), function () {
	            headings.push($(this).val())
	        });
	
	
	
	        fetch("Tailoring", {
	            method: "POST",
	            headers: {
	                "Content-Type": "application/json",
	            },
	            body: JSON.stringify({
	                selectedProjectId: project,
	                ModuleTitle: documentTitle,
	                ModuleName: documentName,
	                ModuleType: documentType,
	                space: folder,
	                allheadings: headings,
	            }),
	        })
	            .then((response) => {
	                if (!response.ok) {
	                    throw new Error(`Response status: ${response.status}`)
	                }
	                return response.json()
	            })
	            .then((data) => {
	                const loaderElement = document.createElement("div")
	                $("#myloader").css("display", "block");
	                var myVar = setTimeout(function () {
	                    showPage();
	                    executeAfterShowPage(data)
	                }, 3000)
	
	            })
	            .catch((error) => {
	                // Log the error message
	                console.error(error.message)
	            });
	    }
	    /* This funtion is for loader class after 3 sec the loader will hide*/
	
	    function showPage() {
	        document.getElementById("myloader").style.display = "none"
	       
	
	    }
	
	    function executeAfterShowPage(data) {
	        const message = data.message
	        const warning =  data.warningMessage
	      /* If user hasn't permission to create Document means below else condition is executed
	         If user has permission below if condition going to be executed*/
	        
	        if(warning == null){
	        if (message.moduleName !== null) {
	            $("#createDialogBox").css("display", "none")
	            $("#successPopupDiv").css("display", "block")
	            $("#successMessageDiv").css("display", "block")
	            const projectIdPtag = document.getElementById("projectIdSpan");
	            projectIdPtag.textContent = message.projectId
	
	            const moduleIdPtag = document.getElementById("moduleIdSpan");
	            moduleIdPtag.textContent = message.moduleName
	
	            const moduleTitlePtag = document.getElementById("titleIdSpan");
	            moduleTitlePtag.textContent = message.title
	
	            const spacePtag = document.getElementById("spaceSpan");
	            spacePtag.textContent = message.space
	        } else {
	            $("#errorPopupDiv").css("display", "block")
	            $("#errorMessageDiv").css("display", "block")
	
	            const errorSpanTag = document.getElementById(
	                "showTitleErrorSpanTag"
	            )
	            errorSpanTag.textContent = data.ModuleName
	
	            const errorSpaceSpanTag =
	                document.getElementById("showSpaceErrorTag")
	            errorSpaceSpanTag.textContent = message.space
	        }
	        }else{
	           // if user hasn't permission to create Document means
	          // here we display alert popup to the user and hide the loader class
	        	setTimeout(function() {
	                alert(warning)
	                location.reload()
	                hideLoader()
	              }, 500)
	          }
	     }
	    
	    function multielectingFunction() {
	        const projectId = document.getElementById("projectDropDown")
	        if (projectId.value === 'Select') {
	            alert("please select project")
	        }
	
	    }
	    function closeFunction() {
	        $(".dialogBoxPopupDiv").css("display", "none")
	        $("#createDialogBox").css("display", "none")
	    }
	
	    function closeMessageFunction() {
	        $("#successMessageDiv").css("display", "none")
	        location.reload()
	    }
	    function errorMessageFunction() {
	        $("#errorMessageDiv").css("display", "none")
	        $("#errorPopupDiv").css("display", "none")
	    }
	</script>

</html>