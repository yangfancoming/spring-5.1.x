

package org.springframework.web.servlet.view.document;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.web.servlet.View;

import static org.junit.Assert.*;

/**
 * Tests for AbstractXlsView and its subclasses.
 *

 * @since 4.2
 */
public class XlsViewTests {

	private final MockHttpServletRequest request = new MockHttpServletRequest();

	private final MockHttpServletResponse response = new MockHttpServletResponse();


	@Test
	@SuppressWarnings("resource")
	public void testXls() throws Exception {
		View excelView = new AbstractXlsView() {
			@Override
			protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
					HttpServletRequest request, HttpServletResponse response) throws Exception {
				Sheet sheet = workbook.createSheet("Test Sheet");
				Row row = sheet.createRow(0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Test Value");
			}
		};

		excelView.render(new HashMap<>(), request, response);

		Workbook wb = new HSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
		assertEquals("Test Sheet", wb.getSheetName(0));
		Sheet sheet = wb.getSheet("Test Sheet");
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		assertEquals("Test Value", cell.getStringCellValue());
	}

	@Test
	@SuppressWarnings("resource")
	public void testXlsxView() throws Exception {
		View excelView = new AbstractXlsxView() {
			@Override
			protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
					HttpServletRequest request, HttpServletResponse response) throws Exception {
				Sheet sheet = workbook.createSheet("Test Sheet");
				Row row = sheet.createRow(0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Test Value");
			}
		};

		excelView.render(new HashMap<>(), request, response);

		Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
		assertEquals("Test Sheet", wb.getSheetName(0));
		Sheet sheet = wb.getSheet("Test Sheet");
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		assertEquals("Test Value", cell.getStringCellValue());
	}

	@Test
	@SuppressWarnings("resource")
	public void testXlsxStreamingView() throws Exception {
		View excelView = new AbstractXlsxStreamingView() {
			@Override
			protected void buildExcelDocument(Map<String, Object> model, Workbook workbook,
					HttpServletRequest request, HttpServletResponse response) throws Exception {
				Sheet sheet = workbook.createSheet("Test Sheet");
				Row row = sheet.createRow(0);
				Cell cell = row.createCell(0);
				cell.setCellValue("Test Value");
			}
		};

		excelView.render(new HashMap<>(), request, response);

		Workbook wb = new XSSFWorkbook(new ByteArrayInputStream(response.getContentAsByteArray()));
		assertEquals("Test Sheet", wb.getSheetName(0));
		Sheet sheet = wb.getSheet("Test Sheet");
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		assertEquals("Test Value", cell.getStringCellValue());
	}

}
