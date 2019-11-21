/**
 * 
 */
package ca.gc.dfo.slims.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import com.itextpdf.text.*;
import org.apache.logging.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import ca.gc.dfo.slims.dto.audit.CommonAuditDto;

/**
 * @author FANGW
 *
 */

public class GeneratePdfReport {

	//private static final Logger logger = LoggerFactory.getLogger(GeneratePdfReport.class);

	public  static ByteArrayInputStream allUsersAuditReport(List<CommonAuditDto> auditReportDate,MessageSource messageSource,Locale locale) {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numColumns = 8;
		try {
			PdfWriter.getInstance(document, out);
			document.open();



			document.addTitle(messageSource.getMessage("i18n.audit.report.pdf.title.all", null, locale)+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale)+"   " + LocalDate.now());


			Paragraph paragraph = new Paragraph();
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.add(new Chunk(messageSource.getMessage("i18n.audit.report.pdf.title.all", null, locale)+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale) +"   " + LocalDate.now()));
			//	document.add(new Phrase().add(new Chunk(messageSource.getMessage("i18n.audit.report.pdf.title.single", null, locale)+"("+userID+") "+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale) +"   " + LocalDate.now(),SMALL_BOLD));

			document.add(paragraph);



			PdfPTable table = new PdfPTable(8);
						 
			//PdfPTable table = new PdfPTable(auditReportDate.stream().findAny().get().getColumnCount());
			table.setWidthPercentage(100.0f);
			//table.setWidths(new int[]{1, 3, 3,3,3,3,3,3});
			table.setSpacingBefore(5);
			Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);


			// define font for table header row
			Font font = FontFactory.getFont(FontFactory.TIMES);
			font.setColor(BaseColor.WHITE);

			// define table header cell
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(BaseColor.BLUE);
			cell.setPadding(10);

			// write table header
			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.rev", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		
			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.userId", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			
			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.auditDate", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.auditTime", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.userName", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.actionName", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.elementName_audit", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.newValue", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			table.completeRow();

			PdfPCell[] cells = null;

				for (CommonAuditDto audit : auditReportDate) {

					cells = new PdfPCell[numColumns];
					cells[0] = new PdfPCell(new Phrase(audit.getRev()));
								cells[0].setPaddingBottom(2);
					cells[0].setPaddingLeft(20);
					cells[0].setPaddingTop(4);
					table.addCell(cells[0]);


					cells[1] = new PdfPCell(new Phrase(audit.getUserID()));
								cells[1].setPaddingBottom(2);
					cells[1].setPaddingLeft(15);
					cells[1].setPaddingTop(2);
					table.addCell(cells[1]);

					cells[2] = new PdfPCell(new Phrase(audit.getAuditDate()));
					cells[2].setPaddingBottom(2);
					cells[2].setPaddingTop(2);
					cells[2].setPaddingLeft(2);
					table.addCell(cells[2]);



					cells[3] = new PdfPCell(new Phrase(audit.getAuditTime()));
					cells[3].setPaddingBottom(2);
					cells[3].setPaddingLeft(2);
					cells[3].setPaddingTop(2);
					table.addCell(cells[3]);

					cells[4] = new PdfPCell(new Phrase(audit.getUserName()));
					cells[4].setPaddingBottom(2);
//					cells[4].setPaddingLeft(2);
					cells[4].setPaddingTop(4);
					table.addCell(cells[4]);


					cells[5] = new PdfPCell(new Phrase(audit.getActionName()));
					cells[5].setPaddingBottom(2);
					cells[5].setPaddingLeft(15);
					cells[5].setPaddingTop(2);
					table.addCell(cells[5]);

					cells[6] = new PdfPCell(new Phrase(audit.getElementName_audit()));
					cells[6].setPaddingBottom(2);
					cells[6].setPaddingTop(2);
					table.addCell(cells[6]);

					cells[7] = new PdfPCell(new Phrase(audit.getNewValue()));
					cells[7].setPaddingBottom(2);
					cells[7].setPaddingLeft(2);
					cells[7].setPaddingTop(2);
					table.addCell(cells[7]);



				}
			table.completeRow();
			document.add(table);

			document.add(new Paragraph(messageSource.getMessage("i18n.audit.report.action.add", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.add.des", null, locale)+"                            "+messageSource.getMessage("i18n.audit.report.action.update", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.update.des", null, locale)
				));
			document.addKeywords(messageSource.getMessage("i18n.audit.report.action.add", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.add.des", null, locale)+"                            "+messageSource.getMessage("i18n.audit.report.action.update", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.update.des", null, locale));
			document.close();

		} catch (DocumentException ex) {
			ex.printStackTrace();
		}

		return new ByteArrayInputStream(out.toByteArray());
	}
	
	public  static ByteArrayInputStream userAuditReport(String userID,List<CommonAuditDto> auditReportDate,MessageSource messageSource,Locale locale) {

		Document document = new Document();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int numColumns = 7;
		try {
			PdfWriter.getInstance(document, out);
			document.open();


				document.addTitle(messageSource.getMessage("i18n.audit.report.pdf.title.single", null, locale)+"("+userID+") "+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale) +"   " + LocalDate.now());

			Paragraph paragraph = new Paragraph();
			paragraph.setAlignment(Element.ALIGN_CENTER);
			paragraph.add(new Chunk(messageSource.getMessage("i18n.audit.report.pdf.title.single", null, locale)+"("+userID+") "+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale) +"   " + LocalDate.now()));
			//	document.add(new Phrase().add(new Chunk(messageSource.getMessage("i18n.audit.report.pdf.title.single", null, locale)+"("+userID+") "+messageSource.getMessage("i18n.audit.report.pdf.title.common", null, locale) +"   " + LocalDate.now(),SMALL_BOLD));

			document.add(paragraph);



			PdfPTable table = new PdfPTable(numColumns);
			
			 
			//PdfPTable table = new PdfPTable(auditReportDate.stream().findAny().get().getColumnCount());
			table.setWidthPercentage(100.0f);
			//table.setWidths(new int[]{1, 3, 3,3,3,3,3,3});
			table.setSpacingBefore(5);
			Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);




			// define font for table header row
			Font font = FontFactory.getFont(FontFactory.TIMES);
			font.setColor(BaseColor.WHITE);

			// define table header cell
			PdfPCell cell = new PdfPCell();
			cell.setBackgroundColor(BaseColor.BLUE);
			cell.setPadding(10);

			// write table header
			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.rev", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
		
			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.auditDate", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.auditTime", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.userName", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.actionName", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.elementName_audit", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);

			cell.setPhrase(new Phrase(messageSource.getMessage("i18n.audit.report.newValue", null, locale), font));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			table.addCell(cell);
			table.completeRow();

			PdfPCell[] cells = null;

			for (CommonAuditDto audit : auditReportDate) {

				cells = new PdfPCell[numColumns];
				cells[0] = new PdfPCell(new Phrase(audit.getRev()));
				cells[0].setPaddingBottom(2);
				cells[0].setPaddingLeft(30);
				cells[0].setPaddingTop(2);
				table.addCell(cells[0]);


				cells[1] = new PdfPCell(new Phrase(audit.getAuditDate()));
				cells[1].setPaddingBottom(2);
				cells[1].setPaddingTop(2);
				cells[1].setPaddingLeft(2);
				table.addCell(cells[1]);



				cells[2] = new PdfPCell(new Phrase(audit.getAuditTime()));
				cells[2].setPaddingBottom(2);
				cells[2].setPaddingLeft(2);
				cells[2].setPaddingTop(2);
				table.addCell(cells[2]);

				cells[3] = new PdfPCell(new Phrase(audit.getUserName()));
				cells[3].setPaddingBottom(2);
//					cells[4].setPaddingLeft(2);
				cells[3].setPaddingTop(2);
				table.addCell(cells[3]);


				cells[4] = new PdfPCell(new Phrase(audit.getActionName()));
				cells[4].setPaddingBottom(2);
				cells[4].setPaddingLeft(15);
				cells[4].setPaddingTop(2);
				table.addCell(cells[4]);

				cells[5] = new PdfPCell(new Phrase(audit.getElementName_audit()));
				cells[5].setPaddingBottom(2);
				cells[5].setPaddingTop(2);
				table.addCell(cells[5]);

				cells[6] = new PdfPCell(new Phrase(audit.getNewValue()));
				cells[6].setPaddingBottom(2);
				cells[6].setPaddingLeft(2);
				cells[6].setPaddingTop(2);
				table.addCell(cells[6]);
			}
			table.completeRow();
			document.add(table);
			document.add(new Paragraph(messageSource.getMessage("i18n.audit.report.action.add", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.add.des", null, locale)+"                            "+messageSource.getMessage("i18n.audit.report.action.update", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.update.des", null, locale)
				));
			document.addKeywords(messageSource.getMessage("i18n.audit.report.action.add", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.add.des", null, locale)+"                            "+messageSource.getMessage("i18n.audit.report.action.update", null, locale)+"==="+messageSource.getMessage("i18n.audit.report.action.update.des", null, locale));
			document.close();

		} catch (DocumentException ex) {
			ex.printStackTrace();
		}

		return new ByteArrayInputStream(out.toByteArray());
	}
}
