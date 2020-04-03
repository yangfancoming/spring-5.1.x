

package org.springframework.scheduling.quartz;


public class QuartzTestBean {

	private int importCount;

	private int exportCount;


	public void doImport() {
		++importCount;
	}

	public void doExport() {
		++exportCount;
	}

	public int getImportCount() {
		return importCount;
	}

	public int getExportCount() {
		return exportCount;
	}

}
