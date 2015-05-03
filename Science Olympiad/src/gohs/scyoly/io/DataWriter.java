package gohs.scyoly.io;

import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DataWriter {

	private File output;

	public static void generateTemplate(String path, int eventNum)
			throws IOException, RowsExceededException, WriteException {

		// prompt user for columnNum

		int columnNum = eventNum * 2;
		
		// open and set up files
		File file = new File(path);
		WritableWorkbook template = Workbook.createWorkbook(file);
		WritableSheet sheet = template.createSheet("Hello", 0);

		WritableFont font;
		WritableCellFormat format;

		// create header row
		for (int column = 0; column < columnNum; column += 2) {
			font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.BOLD);
			format = new WritableCellFormat(font);
			sheet.addCell(new Label(column, 0, "Event Name", format));
		}

		// create start/stop time template
		font = new WritableFont(WritableFont.ARIAL, 10, WritableFont.NO_BOLD,
				true); // italics
		format = new WritableCellFormat(font);
		for (int column = 0; column < columnNum; column++) {
			if (column % 2 == 0)
				sheet.addCell(new Label(column, 1, "start time:", format));
			else
				sheet.addCell(new Label(column, 1, "hh:mm"));
		}

		for (int column = 0; column < columnNum; column++) {
			if (column % 2 == 0)
				sheet.addCell(new Label(column, 2, "stop time:", format));
			else
				sheet.addCell(new Label(column, 2, "hh:mm"));
		}

		// create name/score template
		for (int row = 3; row < 5; row++) {
			for (int column = 0; column < columnNum; column++) {
				if (column % 2 != 0)
					sheet.addCell(new Label(column, row, "score"));
				else
					sheet.addCell(new Label(column, row, "name(s)"));
			}
		}

		template.write();
		template.close();
	}
	
	
}
