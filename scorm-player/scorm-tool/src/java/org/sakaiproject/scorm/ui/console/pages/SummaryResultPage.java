/**
 * 
 */
package org.sakaiproject.scorm.ui.console.pages;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.CellRangeAddress;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.wicket.Page;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.navigation.paging.PagingNavigator;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.sakaiproject.authz.api.Member;
import org.sakaiproject.scorm.api.ScormConstants;
import org.sakaiproject.scorm.model.api.ActivityReport;
import org.sakaiproject.scorm.model.api.ActivitySummary;
import org.sakaiproject.scorm.model.api.ContentPackage;
import org.sakaiproject.scorm.model.api.Interaction;
import org.sakaiproject.scorm.model.api.LearnerExperience;
import org.sakaiproject.scorm.service.api.LearningManagementSystem;
import org.sakaiproject.scorm.service.api.ScormContentService;
import org.sakaiproject.scorm.service.api.ScormResultService;
import org.sakaiproject.scorm.ui.BasicResultPerContentP;
import org.sakaiproject.scorm.ui.NameValuePair;
import org.sakaiproject.scorm.ui.SummaryRecord;
import org.sakaiproject.scorm.ui.console.components.IndicatingSearchButton;
import org.sakaiproject.scorm.ui.player.util.Utils;
import org.sakaiproject.scorm.ui.reporting.pages.AttemptDataProvider;
import org.sakaiproject.site.api.Group;

/**
 * @author Dell 2017-05-07: - Fix some bugs in Coding convention - Change var
 *         selectedValue => selectedGroupValue - arg0 => request
 */
public class SummaryResultPage extends ConsoleBasePage implements ScormConstants, IGetSelectGroupName {

	/** For logging. */
	private static Log log = LogFactory.getLog(SummaryResultPage.class);
	private final static int RECORD_PER_PAGE = 2;

	@SpringBean(name = "org.sakaiproject.scorm.service.api.ScormResultService")
	ScormResultService resultService;

	@SpringBean(name = "org.sakaiproject.scorm.service.api.ScormContentService")
	ScormContentService contentService;

	@SpringBean
	LearningManagementSystem lms;

	private static final Log LOG = LogFactory.getLog(SummaryResultPage.class);

	/**
	 * Value of this var should be used via method getSelectedGroupId(). Make
	 * sure there is not statement "getSelectedGroupName.getValue()" except
	 * method getSelectedGroupId().
	 */
	private NameValuePair selectedGroupValue = null;
	private NameValuePair selectedNumberRecord = null;

	private List<NameValuePair> selectedList = new ArrayList<>();

	// create list provider
	private List<AttemptDataProvider> dataProvider = new ArrayList<>();

	private int currentPage = 1;
	
	private Integer currentNumberOfRecord = 0;
	private IndicatingSearchButton prevBtn;
	private IndicatingSearchButton nextBtn;

	public SummaryResultPage() {
		String context = lms.currentContext();

		List<Group> allGroup = resultService.getAllGroupInSite(context);
		List<NameValuePair> groupsPairValue = new LinkedList<NameValuePair>();
		List<NameValuePair> numberRecordOfPageList = new LinkedList<NameValuePair>();

		numberRecordOfPageList.add(new NameValuePair("5", "5"));
		numberRecordOfPageList.add(new NameValuePair("10", "10"));
		numberRecordOfPageList.add(new NameValuePair("20", "20"));
		numberRecordOfPageList.add(new NameValuePair("50", "50"));
		numberRecordOfPageList.add(new NameValuePair("100", "100"));

		selectedNumberRecord = numberRecordOfPageList.get(0);
		currentNumberOfRecord = Integer.valueOf(selectedNumberRecord.getValue());

		// ThachLN.Fix
		// bug.20170507.https://gst.fsoft.com.vn/redmine/issues/10902.START
		if ((allGroup != null) && (allGroup.size() > 0)) {
			for (Group tmp : allGroup) {
				groupsPairValue.add(new NameValuePair(tmp.getId(), tmp.getTitle()));
			}

			// set default value
			selectedGroupValue = groupsPairValue.get(0);
		}
		// ThachLN.Fix
		// bug.20170507https://gst.fsoft.com.vn/redmine/issues/10902.END

		DropDownChoice groupsChoice = new DropDownChoice("groupsInSite", new PropertyModel(this, "selectedValue"),
				(List) groupsPairValue);
		groupsChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				nextBtn.setVisible(false);
				prevBtn.setVisible(false);
				selectedList.clear();
				request.getPage().remove("listContentPackages");
				ListView lstview = createListViewComponent();
				selectedList = lstview.getModelObject();
				request.getPage().add(lstview);
				
				setResponsePage(request.getPage());
			}
		});
		add(groupsChoice);

		DropDownChoice numberOfRecordChoice = new DropDownChoice("recordOfPage",
				new PropertyModel(this, "selectedNumberRecord"), (List) numberRecordOfPageList);
		numberOfRecordChoice.add(new AjaxFormComponentUpdatingBehavior("onchange") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				currentNumberOfRecord = Integer.valueOf(selectedNumberRecord.getValue());
			}
		});
		add(numberOfRecordChoice);

		// list content package
		ListView lstview = createListViewComponent();
		selectedList = lstview.getModelObject();
		add(lstview);

		// add button search
		// Button searchBtn = new Button("btnSearch") ;
		IndicatingSearchButton searchBtn = new IndicatingSearchButton("btnSearch");
		searchBtn.add(new AjaxFormComponentUpdatingBehavior("onclick") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				// Thach extract request.getPage() to a variable page.
				// Call get("rows") to check before remove
				currentPage = 1;
				Page page = request.getPage();
				page.remove("headers");

				if (get("rows") != null) {
					page.remove("rows");
				}
				/*
				 * if(get("navigator")!=null) { page.remove("navigator"); }
				 */
				// HeaderData
				List<Object[]> header = new ArrayList<>();
				header.add(buildHeaderExportExcel(selectedList));
				DataView<Object[]> dataViewHeader = createheaderdata(header);
				page.add(dataViewHeader);

				// list data
				String selectedGroupId = getSelectedGroupId();

				if (log.isDebugEnabled()) {
					log.debug("Thach:Processing searchBtn:selectedGroupId=" + selectedGroupId);
				}
				

				Map<NameValuePair, SummaryRecord> lstSummary = createMapSummaryRecord(selectedGroupId, context,
						currentNumberOfRecord, currentPage, selectedList);
				List<SummaryRecord> summaryLst = convertMapToSummaryList(lstSummary);

				DataView<SummaryRecord> dataView = createDataRowScreen(summaryLst);

				nextBtn.setVisible(true);
				prevBtn.setVisible(true);
				// dataView.setItemsPerPage(RECORD_PER_PAGE);
				page.add(dataView);
				// page.add(new MyPagingNavigator("navigator",dataView));
				setResponsePage(page);
			}
		});
		searchBtn.setOutputMarkupId(true);
		add(searchBtn);

		nextBtn = new IndicatingSearchButton("btnNextPage");
		nextBtn.add(new AjaxFormComponentUpdatingBehavior("onclick") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				currentPage++;
				Page page = request.getPage();
				page.remove("headers");

				if (get("rows") != null) {
					page.remove("rows");
				}
				/*
				 * if(get("navigator")!=null) { page.remove("navigator"); }
				 */
				// HeaderData
				List<Object[]> header = new ArrayList<>();
				header.add(buildHeaderExportExcel(selectedList));
				DataView<Object[]> dataViewHeader = createheaderdata(header);
				page.add(dataViewHeader);

				// list data
				String selectedGroupId = getSelectedGroupId();

				if (log.isDebugEnabled()) {
					log.debug("Thach:Processing searchBtn:selectedGroupId=" + selectedGroupId);
				}

				Map<NameValuePair, SummaryRecord> lstSummary = createMapSummaryRecord(selectedGroupId, context,
						currentNumberOfRecord, currentPage, selectedList);
				List<SummaryRecord> summaryLst = convertMapToSummaryList(lstSummary);

				DataView<SummaryRecord> dataView = createDataRowScreen(summaryLst);
				// dataView.setItemsPerPage(RECORD_PER_PAGE);
				page.add(dataView);
				// page.add(new MyPagingNavigator("navigator",dataView));
				setResponsePage(page);
			}
		});
		nextBtn.setOutputMarkupId(true);
		nextBtn.setVisible(false);
		add(nextBtn);

		prevBtn = new IndicatingSearchButton("btnPrevPage");
		prevBtn.add(new AjaxFormComponentUpdatingBehavior("onclick") {
			@Override
			protected void onUpdate(AjaxRequestTarget request) {
				currentPage--;
				if (currentPage < 1) {
					currentPage = 1;
				}
				Page page = request.getPage();
				page.remove("headers");

				if (get("rows") != null) {
					page.remove("rows");
				}
				/*
				 * if(get("navigator")!=null) { page.remove("navigator"); }
				 */
				// HeaderData
				List<Object[]> header = new ArrayList<>();
				header.add(buildHeaderExportExcel(selectedList));
				DataView<Object[]> dataViewHeader = createheaderdata(header);
				page.add(dataViewHeader);

				// list data
				String selectedGroupId = getSelectedGroupId();

				if (log.isDebugEnabled()) {
					log.debug("Thach:Processing searchBtn:selectedGroupId=" + selectedGroupId);
				}

				Map<NameValuePair, SummaryRecord> lstSummary = createMapSummaryRecord(selectedGroupId, context,
						currentNumberOfRecord, currentPage, selectedList);
				List<SummaryRecord> summaryLst = convertMapToSummaryList(lstSummary);

				DataView<SummaryRecord> dataView = createDataRowScreen(summaryLst);
				// dataView.setItemsPerPage(RECORD_PER_PAGE);
				page.add(dataView);
				// page.add(new MyPagingNavigator("navigator",dataView));
				setResponsePage(page);
			}
		});
		prevBtn.setOutputMarkupId(true);
		prevBtn.setVisible(false);
		add(prevBtn);

		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String strNow = sdf.format(now);

		AbstractReadOnlyModel<File> fileModelExcel = createExcelDataFile(context);
		ResultDownloadLink btnExportExcel = new ResultDownloadLink("btnExportExcel", fileModelExcel,
				"_results_" + strNow + ".xlsx", this);
		btnExportExcel.setDeleteAfterDownload(true);
		btnExportExcel.setOutputMarkupId(true);
		add(btnExportExcel);

		AbstractReadOnlyModel<File> fileModelDetailExcel = createExcelDetailDataFile(context);
		ResultDownloadLink btnExportDetailExcel = new ResultDownloadLink("btnExportDetailExcel", fileModelDetailExcel,
				"_detailed_results" + strNow + ".xlsx", this);
		btnExportDetailExcel.setDeleteAfterDownload(true);
		btnExportDetailExcel.setOutputMarkupId(true);
		add(btnExportDetailExcel);

		// HeaderData
		List<Object[]> header = new ArrayList<>();
		header.add(buildHeaderExportExcel(selectedList));
		DataView<Object[]> dataViewHeader = createheaderdata(header);
		add(dataViewHeader);

		// list data

		ListDataProvider<SummaryRecord> listDataProvider = new ListDataProvider<SummaryRecord>();

		DataView<SummaryRecord> dataView = new DataView<SummaryRecord>("rows", listDataProvider) {
			@Override
			protected void populateItem(Item<SummaryRecord> item) {
				// do nothing here
			}
		};
		add(dataView);
		// add(new MyPagingNavigator("navigator", dataView));

		// String buildVersion =
		// ServerConfigurationService.getString("scormplayer.version","?");
		// Label buildVersionLable = new Label("buildVersion",buildVersion);
		// add(buildVersionLable);
	}

	private AbstractReadOnlyModel<File> createExcelDetailDataFile(String context) {
		AbstractReadOnlyModel<File> fileModelExcel = new AbstractReadOnlyModel<File>() {
			@Override
			public File getObject() {
				File tempFile = null;
				try {
					tempFile = File.createTempFile("detail_results", ".xlsx");
					List<AttemptDataProvider> dataProvider = createAttemptDataProviders(getSelectedGroupId(), context,
							currentNumberOfRecord, currentPage, selectedList);
					generateDetailExportExcel(dataProvider, tempFile);
				} catch (IOException ex) {
					LOG.error("Could not generate results export: ", ex);
				}

				return tempFile;
			}
		};
		return fileModelExcel;
	}

	/**
	 * Create header Data View Screen
	 * 
	 * @param header
	 * @return
	 */
	private DataView<Object[]> createheaderdata(List<Object[]> header) {
		ListDataProvider<Object[]> listHeaderProvider = new ListDataProvider<Object[]>(header);

		DataView<Object[]> dataViewHeader = new DataView<Object[]>("headers", listHeaderProvider) {

			@Override
			protected void populateItem(Item<Object[]> item) {
				Object[] person = item.getModelObject();
				RepeatingView repeatingView = new RepeatingView("header");

				int size = person.length;
				for (int i = 1; i < size; i++) {
					if (null != person[i] && !"".equals(person[i].toString())) {
						repeatingView.add(new Label(repeatingView.newChildId(), person[i].toString()));
					}
				}
				item.add(repeatingView);
			}
		};
		return dataViewHeader;
	}

	/**
	 * Create data row for button search
	 * 
	 * @param summaryLst
	 * @return
	 */
	private DataView<SummaryRecord> createDataRowScreen(List<SummaryRecord> summaryLst) {
		SummaryRecord firstItem = null;
		if (null != summaryLst && !summaryLst.isEmpty()) {
			firstItem = summaryLst.get(0);
		}
		SummaryRecord itemHeader = createSecondHeader(firstItem);
		if (null != itemHeader) {
			summaryLst.add(0, itemHeader);
		}
		ListDataProvider<SummaryRecord> listDataProvider = new ListDataProvider<SummaryRecord>(summaryLst);

		DataView<SummaryRecord> dataView = new DataView<SummaryRecord>("rows", listDataProvider) {

			@Override
			protected void populateItem(Item<SummaryRecord> item) {
				SummaryRecord person = item.getModelObject();
				RepeatingView repeatingView = new RepeatingView("dataRow");

				repeatingView.add(new Label(repeatingView.newChildId(), person.getUserName()));

				for (BasicResultPerContentP tmp : person.getLstResult()) {
					repeatingView.add(new Label(repeatingView.newChildId(), tmp.getStartDateStr()));
					repeatingView.add(new Label(repeatingView.newChildId(), tmp.getDuration()));
					repeatingView.add(new Label(repeatingView.newChildId(), tmp.getScoreStr()));
					repeatingView.add(new Label(repeatingView.newChildId(), tmp.getCompletedStatus()));
					// repeatingView.add(new Label(repeatingView.newChildId(),
					// tmp.getLastAttempt()));
					// repeatingView.add(new Label(repeatingView.newChildId(),
					// tmp.getSubmitTime()));
				}
				item.add(repeatingView);
			}
		};
		return dataView;
	}

	private SummaryRecord createSecondHeader(SummaryRecord firstRec) {
		if (null != firstRec) {
			int numberOfContent = firstRec.getLstResult().size();
			List<BasicResultPerContentP> lstResult = new ArrayList<>();
			for (int i = 0; i < numberOfContent; i++) {
				BasicResultPerContentP item = new BasicResultPerContentP();
				item.setCompletedStatus("Status");
				item.setDuration("Duration");
				item.setScoreStr("Score");
				item.setStartDateStr("Start Date");
				lstResult.add(item);
			}

			SummaryRecord result = new SummaryRecord("", lstResult);
			return result;
		} else {
			return null;
		}

	}

	/**
	 * convert Map to List
	 * 
	 * @param lstSummary
	 * @return
	 */
	private List<SummaryRecord> convertMapToSummaryList(Map<NameValuePair, SummaryRecord> lstSummary) {
		List<SummaryRecord> result = new ArrayList<>();
		for (Map.Entry<NameValuePair, SummaryRecord> entry : lstSummary.entrySet()) {
			SummaryRecord tmpRecord = entry.getValue();

			result.add(tmpRecord);
		}
		return result;
	}

	/**
	 * Create data Content Package List for list View Component
	 * 
	 * @return
	 */
	private List<NameValuePair> createContentPackageData() {
		List<ContentPackage> lst = contentService.getContentPackages();
		List<NameValuePair> lstListViewData = new ArrayList<>();
		if (null != lst) {
			for (ContentPackage tmp : lst) {
				NameValuePair value = new NameValuePair(tmp.getContentPackageId().toString().toString(),
						tmp.getTitle());
				lstListViewData.add(value);
			}
		}
		return lstListViewData;
	}

	/**
	 * Create List content Package List View Component
	 * 
	 * @return
	 */
	private ListView createListViewComponent() {
		List<NameValuePair> lstListViewData = createContentPackageData();
		@SuppressWarnings("unchecked")
		ListView listView = new ListView("listContentPackages", lstListViewData) {
			protected void populateItem(ListItem item) {
				NameValuePair wrapper = (NameValuePair) item.getModelObject();
				item.add(new CheckBox("check", new PropertyModel(wrapper, "selected")) {
					protected boolean wantOnSelectionChangedNotifications() {
						return true;
					}
				});
				item.add(new Label("name", wrapper.getName()));
			}

		};

		listView.setReuseItems(true);
		return listView;

	}

	private List<NameValuePair> getLstSelectedItem(List<NameValuePair> selectedList) {
		List<NameValuePair> result = new ArrayList<>();
		int sizeContent = selectedList.size();
		for (int i = 0; i < sizeContent; i++) {
			if (((NameValuePair) selectedList.get(i)).getSelected()) {
				result.add(selectedList.get(i));
			}
		}
		return result;
	}

	/**
	 * Export Excel
	 * 
	 * @param tempFile
	 * @param context
	 * @throws IOException
	 */
	private void generateExportExcel(File tempFile, String context) throws IOException {
		FileOutputStream os = new FileOutputStream(tempFile);

		XSSFWorkbook workbook = new XSSFWorkbook();
		// Create a blank spreadsheet
		XSSFSheet spreadsheet = workbook.createSheet("Results");
		// Create row object
		XSSFRow row;
		List<Object[]> lstData = new ArrayList<Object[]>();
		// This Header needs to be written (Object[])
		Object[] header = buildHeaderExportExcel(selectedList);
		lstData.add(header);
		lstData.addAll(createData(getSelectedGroupId(), context, currentNumberOfRecord, currentPage, selectedList));

		// export
		int rowid = 0;
		for (Object[] objLst : lstData) {
			row = spreadsheet.createRow(rowid++);
			int cellid = 0;
			for (Object obj : objLst) {
				Cell cell = row.createCell(cellid++);
				cell.setCellValue((String) obj);
			}
		}

		int size = header.length;
		spreadsheet.autoSizeColumn(0);
		int sizeContent = selectedList.size();
		int indx = 1;
		for (int i = 0; i < sizeContent; i++) {
			if (((NameValuePair) selectedList.get(i)).getSelected()) {
				spreadsheet.autoSizeColumn(indx++);
				indx++;
			}
		}

		// merge header
		List<CellRangeAddress> cellRangeLst = createMergeCell(selectedList);
		for (CellRangeAddress tmp : cellRangeLst) {
			spreadsheet.addMergedRegion(tmp);
		}

		workbook.write(os);

		os.close();
	}

	/**
	 * create list cell merge for excel export
	 * 
	 * @param lstContentPackage
	 * @return
	 */
	private List<CellRangeAddress> createMergeCell(List<NameValuePair> lstContentPackage) {
		List<CellRangeAddress> result = new ArrayList<>();
		int sizeContent = lstContentPackage.size();
		int indx = 0;
		for (int i = 0; i < sizeContent; i++) {
			if (((NameValuePair) lstContentPackage.get(i)).getSelected()) {
				int startCol = i * 4 + 1;
				int lastCol = startCol + 3;
				CellRangeAddress cellRange = new CellRangeAddress(0, 0, startCol, lastCol);
				result.add(cellRange);
			}
		}
		return result;
	}

	/**
	 * buid header excel
	 * 
	 * @param lstContentPackage
	 * @return
	 */
	private Object[] buildHeaderExportExcel(List<NameValuePair> lstContentPackage) {
		int sizeContent = lstContentPackage.size();
		int size = sizeContent * 4 + 1;
		Object[] tmpObj = new Object[size];
		tmpObj[0] = "Learner Name";
		int indx = 1;
		for (int i = 0; i < sizeContent; i++) {
			if (((NameValuePair) lstContentPackage.get(i)).getSelected()) {
				tmpObj[indx++] = ((NameValuePair) lstContentPackage.get(i)).getName();
				tmpObj[indx++] = "";
				tmpObj[indx++] = "";
				tmpObj[indx++] = "";
			}
		}
		return tmpObj;
	}

	/**
	 * create export data
	 * 
	 * @param context
	 * @return
	 */
	private AbstractReadOnlyModel<File> createExcelDataFile(String context) {
		AbstractReadOnlyModel<File> fileModelExcel = new AbstractReadOnlyModel<File>() {
			@Override
			public File getObject() {
				File tempFile = null;
				try {
					tempFile = File.createTempFile("Test_results", ".xlsx");
					generateExportExcel(tempFile, context);
				} catch (IOException ex) {
					LOG.error("Could not generate results export: ", ex);
				}

				return tempFile;
			}
		};
		return fileModelExcel;
	}

	/**
	 * create data for export excel
	 * 
	 * @param lstMember
	 * @param lstContentPackage
	 * @return
	 */
	private List<Object[]> createData(String groupId, String context, int numberOfUser, int currentPage,
			List<NameValuePair> lstContentPackage) {
		Map<NameValuePair, SummaryRecord> data = createMapSummaryRecord(groupId, context, numberOfUser, currentPage,
				lstContentPackage);
		SummaryRecord firstRec = null;
		for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()) {
			firstRec = entry.getValue();
			break;
		}
		List<Object[]> result = new ArrayList<>();
		SummaryRecord itemHeader = createSecondHeader(firstRec);
		NameValuePair firstValue = new NameValuePair("", "");
		Map<NameValuePair, SummaryRecord> fisrtItemMap = new HashMap<>();
		fisrtItemMap.put(firstValue, itemHeader);
		createObjectsFromMap(fisrtItemMap, result);
		createObjectsFromMap(data, result);

		return result;
	}

	private void createObjectsFromMap(Map<NameValuePair, SummaryRecord> data, List<Object[]> result) {
		for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()) {
			SummaryRecord tmpRecord = entry.getValue();

			if (tmpRecord == null) {
				return;
			}

			List<BasicResultPerContentP> lstResult = tmpRecord.getLstResult();

			if (lstResult == null) {
				return;
			}

			int sizeContent = lstResult.size();
			int size = sizeContent * 4 + 1;
			Object[] tmpObj = new Object[size];
			tmpObj[0] = tmpRecord.getUserName();
			int indx = 1;
			BasicResultPerContentP rowResult;
			for (int i = 0; i < sizeContent; i++) {
				rowResult = lstResult.get(i);
				tmpObj[indx++] = rowResult.getStartDateStr();
				tmpObj[indx++] = rowResult.getDuration();
				tmpObj[indx++] = rowResult.getScoreStr();
				tmpObj[indx++] = rowResult.getCompletedStatus();
			}

			result.add(tmpObj);
		}
	}

	/**
	 * Create Map data
	 * 
	 * @param groupId
	 * @param context
	 * @param lstContentPackage
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<NameValuePair, SummaryRecord> createMapSummaryRecord(String groupId, String context, int numberOfUser,
			int currentPage, List<NameValuePair> lstContentPackage) {
		
		// 1. create list ID
		List<String> lstMemberId = new ArrayList<>();
		List<Member> lstMember = resultService.getAllMemberInGroup(groupId, context);
		if (lstMember != null) {
			int size = lstMember.size();
			BigDecimal page =  (new BigDecimal(size)).divide((new BigDecimal(numberOfUser)), RoundingMode.HALF_UP);
			int numberOfPage =  page.intValue();
			
			if(currentPage > numberOfPage){
				this.currentPage = numberOfPage;
				currentPage = this.currentPage;
			}
			
			lstMember.sort(new MemberComparator());
			int start = (currentPage * numberOfUser - numberOfUser);
			int end = currentPage * numberOfUser;
			//
			int i = 0;
			for (Member tmp : lstMember) {
				if (i < end && i >= start) {
					lstMemberId.add(tmp.getUserDisplayId());
				}
				i++;
			}
		}
		// 2. create list ContentPackage
		List<Long> lstContentPackageId = new ArrayList<>();
		if (lstContentPackage != null) {
			for (NameValuePair p : lstContentPackage) {
				if (p.getSelected()) {
					lstContentPackageId.add(new Long(p.getValue()));
				}
			}
		}

		Map<NameValuePair, SummaryRecord> data = new HashMap<>();

		for (Long contentId : lstContentPackageId) {
			ContentPackage contentPackage = contentService.getContentPackage(contentId.longValue());
			AttemptDataProvider dataProvider = new AttemptDataProvider(contentId.longValue(), null, contentPackage,
					null, null, lstMemberId, true, resultService);
			Map<NameValuePair, BasicResultPerContentP> oneContent = createResultPerContentPackage(dataProvider,
					contentPackage);
			appendData(data, oneContent);
		}
		return data;
	}

	/**
	 * For export Detail
	 * 
	 * @param groupId
	 * @param context
	 * @param lstContentPackage
	 * @return
	 */
	private List<AttemptDataProvider> createAttemptDataProviders(String groupId, String context, int numberOfUser,
			int currentPage, List<NameValuePair> lstContentPackage) {
		List<AttemptDataProvider> result = new ArrayList<>();
		
		// 1. create list ID
		List<String> lstMemberId = new ArrayList<>();
		List<Member> lstMember = resultService.getAllMemberInGroup(groupId, context);
		if (lstMember != null) {
			int size = lstMember.size();
			BigDecimal page =  (new BigDecimal(size)).divide((new BigDecimal(numberOfUser)), RoundingMode.HALF_UP);
			int numberOfPage =  page.intValue();
			
			if(currentPage > numberOfPage){
				this.currentPage = numberOfPage;
				currentPage = this.currentPage;
			}
			int start = (currentPage * numberOfUser - numberOfUser);
			int end = currentPage * numberOfUser;
			
			lstMember.sort(new MemberComparator());
			int i = 0;
			for (Member tmp : lstMember) {
				if (i < end && i >= start) {
					lstMemberId.add(tmp.getUserDisplayId());
				}
				i++;
			}
		}		

		// 2. create list ContentPackage
		List<Long> lstContentPackageId = new ArrayList<>();
		if (lstContentPackage != null) {
			for (NameValuePair p : lstContentPackage) {
				if (p.getSelected()) {
					lstContentPackageId.add(new Long(p.getValue()));
				}
			}
		}

		Map<NameValuePair, SummaryRecord> data = new HashMap<>();

		for (Long contentId : lstContentPackageId) {
			ContentPackage contentPackage = contentService.getContentPackage(contentId.longValue());
			AttemptDataProvider dataProvider = new AttemptDataProvider(contentId.longValue(), null, contentPackage,
					null, null, lstMemberId, true, resultService);
			result.add(dataProvider);
		}
		return result;
	}

	/**
	 * Append data on lesson (content package) to list
	 * 
	 * @param data
	 * @param oneContent
	 */
	private void appendData(Map<NameValuePair, SummaryRecord> data,
			Map<NameValuePair, BasicResultPerContentP> oneContent) {
		if (!data.isEmpty()) {
			for (Map.Entry<NameValuePair, SummaryRecord> entry : data.entrySet()) {
				if (oneContent.containsKey(entry.getKey())) {
					BasicResultPerContentP tmp = oneContent.get(entry.getKey());
					data.get(entry.getKey()).setUserName(entry.getKey().getName());
					data.get(entry.getKey()).getLstResult().add(tmp);
				}
			}
		} else {
			for (Map.Entry<NameValuePair, BasicResultPerContentP> entry : oneContent.entrySet()) {
				List<BasicResultPerContentP> lsBasic = new ArrayList<>();
				lsBasic.add(entry.getValue());
				SummaryRecord sRecord = new SummaryRecord(entry.getKey().getName(), lsBasic);
				data.put(entry.getKey(), sRecord);
			}
		}

	}

	/**
	 * create Map User and result of One ContentPackage
	 * 
	 * @param attemptProvider
	 * @return
	 */
	@SuppressWarnings("unused")
	private Map<NameValuePair, BasicResultPerContentP> createResultPerContentPackage(
			AttemptDataProvider attemptProvider, ContentPackage contentPackage) {
		// Create the column headers
		Map<NameValuePair, BasicResultPerContentP> resultBasic = new HashMap<>();

		Iterator<LearnerExperience> itr = attemptProvider.iterator(0, attemptProvider.size());
		while (itr.hasNext()) {
			// Learner info
			LearnerExperience learner = itr.next();
			BasicResultPerContentP basic = null;

			// Get the summaries for all attempts for the current user
			List<ActivitySummary> summaries = new ArrayList<>();
			for (int i = 1; i <= learner.getNumberOfAttempts(); i++) {
				summaries.addAll(
						resultService.getActivitySummaries(learner.getContentPackageId(), learner.getLearnerId(), i));
			}
			NameValuePair learnerInfo = new NameValuePair(learner.getLearnerId(), learner.getLearnerName());
			if (summaries.isEmpty()) {
				if (!resultBasic.containsKey(learner.getLearnerId())) {
					basic = new BasicResultPerContentP(learner.getContentPackageId(), "", "", "", "");
					resultBasic.put(learnerInfo, basic);
				}
			} else {
				// Summary info
				ActivitySummary summary = null;
				ActivityReport report = null;

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				if (null != contentPackage.getGetHighestResult() && contentPackage.getGetHighestResult()) {
					summary = getMaxHighResultInListAttempt(summaries);
					report = resultService.getActivityReport(summary.getContentPackageId(), summary.getLearnerId(),
							summary.getAttemptNumber(), summary.getScoId());
				} else {
					summary = summaries.get(summaries.size() - 1);
					report = resultService.getActivityReport(summary.getContentPackageId(), summary.getLearnerId(),
							summary.getAttemptNumber(), summary.getScoId());
				}

				List<Interaction> interactions = report.getInteractions();
				String submitTime = "";

				submitTime = getLastSubmitTimeFromInteractions(interactions);

				if (!resultBasic.containsKey(learner.getLearnerId())) {
					basic = new BasicResultPerContentP(summary.getContentPackageId(), summary.getCompletionStatus(),
							Utils.getNumberPercententString(summary.getScaled()),
							sdf.format(learner.getLastAttemptDate()), submitTime);
					basic.setDuration(summary.getTotalSessionSecondsDisplay());
					basic.setStartDate(summary.getStartDate());
					basic.setStartDateStr(sdf.format(summary.getStartDate()));
					resultBasic.put(learnerInfo, basic);
				}
			}

		}
		return resultBasic;
	}

	private String getLastSubmitTimeFromInteractions(List<Interaction> interactions) {
		String formattedMaxSubmitTime = "";
		if (null != interactions && !interactions.isEmpty()) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			int size = interactions.size();

			String maxSubmitTime = interactions.get(0).getTimestamp();

			// Thach.Start
			if ((maxSubmitTime == null) || (maxSubmitTime.length() == 0)) {
				// return default value ""
				return formattedMaxSubmitTime;
			}
			// Thach.End
			Date d1 = null;

			try {
				d1 = sdf.parse(maxSubmitTime);
			} catch (Exception ex) {
				LOG.warn("Could not get last submit time: maxSubmitTime=" + maxSubmitTime);
			}
			if (null != d1) {
				formattedMaxSubmitTime = output.format(d1);
			}
			for (int i = 0; i < size; i++) {
				if (null != interactions.get(i) && null != interactions.get(i).getTimestamp()
						&& "" != interactions.get(i).getTimestamp()) {
					String curSubmitTime = interactions.get(i).getTimestamp();
					Date d2 = null;

					try {
						d2 = sdf.parse(curSubmitTime);
					} catch (Exception ex) {
						LOG.warn("Could not get last submit time: curSubmitTime=" + curSubmitTime);
					}
					if (null != d2) {
						String formattedCurSubmitTime = output.format(d2);
						if (formattedMaxSubmitTime.compareTo(formattedCurSubmitTime) < 0) {
							formattedMaxSubmitTime = formattedCurSubmitTime;
						}
					}
				}
			}
		}

		return formattedMaxSubmitTime;
	}

	/**
	 * get In of Object has Max Result In List Attemp
	 * 
	 * @param summaries
	 * @return
	 */
	private ActivitySummary getMaxHighResultInListAttempt(List<ActivitySummary> summaries) {
		int size = summaries.size();
		ActivitySummary maxActivity = summaries.get(0);
		for (int i = 1; i < size; i++) {
			ActivitySummary curActivity = summaries.get(i);
			if (maxActivity.compareActivitySummary(curActivity) < 0) {
				maxActivity = curActivity;
			}
		}
		return maxActivity;
	}

	private void generateDetailExportExcel(List<AttemptDataProvider> attemptProviders, File tempFile)
			throws IOException {
		FileOutputStream os = new FileOutputStream(tempFile);

		XSSFWorkbook workbook = new XSSFWorkbook();
		for (int i = 0; i < attemptProviders.size(); i++) {
			// // Create a blank spreadsheet
			XSSFSheet spreadsheet = workbook.createSheet(attemptProviders.get(i).getContentPackageName());
			// // Create row object
			XSSFRow row;
			List<Object[]> lstData = new ArrayList<Object[]>();
			// This Header needs to be written (Object[])
			Object[] header = buildHeaderExportExcel();
			lstData.add(header);
			lstData.addAll(generateExportExcel(resultService, attemptProviders.get(i)));

			// export
			int rowid = 0;
			for (Object[] objLst : lstData) {
				row = spreadsheet.createRow(rowid++);
				int cellid = 0;
				for (Object obj : objLst) {
					Cell cell = row.createCell(cellid++);
					cell.setCellValue((String) obj);
				}
			}
			int size = header.length;
			for (int j = 0; j < size; j++) {
				spreadsheet.autoSizeColumn(j);
			}
		}

		workbook.write(os);

		os.close();
	}

	/**
	 * Get selected group value. In case there is no group, the blank value is
	 * returned.
	 * 
	 * @return selected group value or blank
	 */
	private String getSelectedGroupId() {
		return (selectedGroupValue != null) ? selectedGroupValue.getValue() : "";
	}

	private String getSelectedGroupName() {
		return (selectedGroupValue != null) ? selectedGroupValue.getName() : "";
	}

	public NameValuePair getSelectedValue() {
		return selectedGroupValue;
	}

	public void setSelectedValue(NameValuePair selectedValue) {
		this.selectedGroupValue = selectedValue;
	}

	@Override
	public String getGroupName() {
		return getSelectedGroupName();
	}

	public NameValuePair getSelectedNumberRecord() {
		return selectedNumberRecord;
	}

	public void setSelectedNumberRecord(NameValuePair selectedNumberRecord) {
		this.selectedNumberRecord = selectedNumberRecord;
	}

	@SuppressWarnings("rawtypes")
	class MemberComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			Member s1 = (Member) o1;
			Member s2 = (Member) o2;

			if (s1.getUserDisplayId().equals(s2.getUserDisplayId()))
				return 0;
			else if (s1.getUserDisplayId().compareTo(s2.getUserDisplayId()) > 0)
				return 1;
			else
				return -1;
		}
	}
}
