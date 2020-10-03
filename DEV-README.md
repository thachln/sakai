This file records historial changes of the project.
- 2019-8-15: Add a new feature "Export media metadata" in Resources tool.
  Main changed codes:
  + Class: org.sakaiproject.content.types.FolderType, constructor method FolderType():
		...
		actions.put(COMPRESS_ZIP_FOLDER, new FolderCompressAction(COMPRESS_ZIP_FOLDER, ActionType.COMPRESS_ZIP_FOLDER, typeId, false, localizer("action.compresszipfolder")));
		// ThachLN++
        actions.put(EXPORT_METADATA, new FolderExportMetadataAction(EXPORT_METADATA, ActionType.EXPORT_METADATA, typeId, false, localizer("action.exportmetadata")));
		actions.put(MAKE_SITE_PAGE, new MakeSitePageAction(MAKE_SITE_PAGE, ActionType.MAKE_SITE_PAGE, typeId));
		...
 