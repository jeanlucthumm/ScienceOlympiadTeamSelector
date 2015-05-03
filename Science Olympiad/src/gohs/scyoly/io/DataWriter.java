package gohs.scyoly.io;

import gohs.scyoly.core.Crew;
import gohs.scyoly.core.Event;
import gohs.scyoly.core.TimeRange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.AllPermission;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class DataWriter {

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

	private File output;

	public DataWriter(String path) {
		output = new File(path);
	}

	public void writeCrew(Crew crew) throws IOException, BiffException,
			WriteException {

		WritableWorkbook mod = null;
		WritableSheet sheet = null;

		try { // file is already made => modify it
			Workbook original = Workbook.getWorkbook(output);
			mod = Workbook.createWorkbook(output, original);
			sheet = mod.getSheet("Team Listing");
		} catch (FileNotFoundException e) { // make new file
			mod = Workbook.createWorkbook(output);
			sheet = mod.createSheet("Team Listing", 0);
		}

		// set up formats
		// bold
		WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		WritableCellFormat bold = new WritableCellFormat(boldFont);

		// italic
		WritableFont italicFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.NO_BOLD, true);
		WritableCellFormat italic = new WritableCellFormat(italicFont);

		// block cell
		WritableCellFormat blockCell = new WritableCellFormat();
		blockCell.setBackground(Colour.LIGHT_BLUE);

		// set up the time row
		List<TimeRange> timeRanges = new ArrayList<>(
				crew.getSortedTimeRanges());
		String[] timeRangesAsString = new String[timeRanges.size()];
		
		int count = 0;
		for (TimeRange timeRange : timeRanges) {
			timeRangesAsString[count++] = timeRange.toString();
		}
		
		// max columns should be number of time ranges plus one
		int columnNum = timeRangesAsString.length + 1;

		// FIXME Events should be in the same order as they were originally put
		// in
		sheet.addCell(new Label(0, 0, "Event", bold));

		// print time range row
		for (int col = 1; col < timeRangesAsString.length + 1; col++) {
			sheet.addCell(new Label(col, 0, timeRangesAsString[col - 1], italic));
		}
		
		// print events and teams
		// loop through events
		List<Event> events = new ArrayList<>(Event.getAllEvents())
		for (int row = 1; row)

	}

}
