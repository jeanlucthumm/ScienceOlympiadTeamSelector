package gohs.scyoly.io;

import gohs.scyoly.core.Crew;
import gohs.scyoly.core.Event;
import gohs.scyoly.core.TimeRange;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.read.biff.BiffException;
import jxl.write.Blank;
import jxl.write.Label;
import jxl.write.Number;
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
	private int lastRow; // used to know where to append things

	public DataWriter(String path) {
		output = new File(path);
		lastRow = 0;
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

		// check if need appending
		if (lastRow != 0)
			lastRow++;

		// set up formats
		// bold
		WritableFont boldFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.BOLD);
		WritableCellFormat bold = new WritableCellFormat(boldFont);

		// italic
		WritableFont italicFont = new WritableFont(WritableFont.ARIAL, 10,
				WritableFont.NO_BOLD, true);
		WritableCellFormat italic = new WritableCellFormat(italicFont);
		
		// center
		WritableCellFormat centre = new WritableCellFormat();
		centre.setAlignment(Alignment.CENTRE);

		// FIXME make banded rows in terms of color
		// block cell
		WritableCellFormat blockCell = new WritableCellFormat();
		blockCell.setBackground(Colour.LIGHT_BLUE);
		blockCell.setBorder(Border.ALL, BorderLineStyle.THIN);

		// set up the time row
		List<TimeRange> timeRanges = new ArrayList<>();
		List<TimeRange> temp = crew.getSortedTimeRanges();
		for (int i = 0; i < temp.size(); i++) {
			if (timeRanges.size() > 0
					&& temp.get(i)
							.equals(timeRanges.get(timeRanges.size() - 1)))
				continue;

			timeRanges.add(temp.get(i));
		}

		String[] timeRangesAsString = new String[timeRanges.size()];
		// convert to string
		int count = 0;
		for (TimeRange timeRange : timeRanges) {
			timeRangesAsString[count++] = timeRange.toString();
		}

		// max columns should be number of time ranges plus one
		int columnNum = timeRangesAsString.length + 1;

		// FIXME Events should be in the same order as they were originally put
		// in
		String crewName = "Team " + crew.getRank();
		sheet.addCell(new Label(0, lastRow, crewName, bold));

		// print time range row
		for (int col = 1; col < timeRangesAsString.length + 1; col++) {
			sheet.addCell(new Label(col, lastRow, timeRangesAsString[col - 1], centre));
		}

		int teamColumn;
		lastRow++;
		for (Map.Entry<Event, gohs.scyoly.core.Entry> rosterEntry : crew
				.getRoster().entrySet()) {
			// print event name
			sheet.addCell(new Label(0, lastRow, rosterEntry.getKey().toString()));

			// find correct column
			teamColumn = timeRanges
					.indexOf(rosterEntry.getKey().getTimeRange()) + 1;

			if (teamColumn == 0)
				throw new IndexOutOfBoundsException();

			// print team
			sheet.addCell(new Label(teamColumn, lastRow, rosterEntry.getValue()
					.getTeam().toString(), italic));

			// block out cells
			for (int i = 1; i < columnNum; i++) {
				if (i != teamColumn)
					sheet.addCell(new Blank(i, lastRow, blockCell));
			}

			// move on to next row
			lastRow++;
		}

		// write team statistics
		lastRow++;
		sheet.addCell(new Label(0, lastRow, "Average Rank:", bold));
		sheet.addCell(new Number(1, lastRow, crew.getAverageRank()));
		lastRow++;

		mod.write();
		mod.close();
	}
}
