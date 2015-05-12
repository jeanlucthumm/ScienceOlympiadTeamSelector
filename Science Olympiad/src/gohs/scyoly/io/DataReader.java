package gohs.scyoly.io;

import gohs.scyoly.core.Event;
import gohs.scyoly.core.Team;

import java.io.File;
import java.io.IOException;

import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class DataReader {

	private Workbook workbook;
	private Sheet sheet;

	public DataReader(String path) throws BiffException, IOException {
		File file = new File(path);
		this.workbook = Workbook.getWorkbook(file);
		this.sheet = workbook.getSheet(0);
	}

	// FIXME NumberFormatException needs to be handled when numbers are not put
	// in the right way

	// FIXME add a column that contains seniors. If it is left empty, generate a
	// JPanel with check boxes, and then modify the input file as well.
	public void populateEvents() {

		String eventName;
		String start;
		String stop;
		Event currentEvent;

		String studentName;
		int rank;

		for (int i = 0; i < sheet.getColumns(); i += 2) {

			// skip columns that are empty
			if (sheet.getCell(i, 0).getType() == CellType.EMPTY)
				continue;

			// get data and create event
			eventName = sheet.getCell(i, 0).getContents();
			start = sheet.getCell(i + 1, 1).getContents();
			stop = sheet.getCell(i + 1, 2).getContents();

			currentEvent = new Event(eventName, start, stop);

			for (int j = 3; j < sheet.getRows(); j++) {

				if (sheet.getCell(i, j).getType() == CellType.EMPTY)
					continue;

				// get the scores for every student by traversing the column
				studentName = sheet.getCell(i, j).getContents();
				rank = Integer.parseInt(sheet.getCell(i + 1, j).getContents());

				currentEvent.addEntry(new Team(studentName), rank);
			}
		}
	}

}
