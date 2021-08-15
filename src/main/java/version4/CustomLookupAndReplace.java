package version4;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.lookup.StrLookup;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Plugin(name="customReplace", category="Lookup")
public class CustomLookupAndReplace implements StrLookup {

	private static Date currentDate = new Date();
	private static Calendar cal = Calendar.getInstance();
	private static DateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");
	private static int currentTime = 0;
	private static int currentHour = 0;
	private static int currentMinute = 0;
	private final static String appName = System.getProperty("appName");
	private boolean hourChanged = false;
	private boolean timeChanged = false;
	private boolean newApplication = false;
	private int month = 0;
	private int dateOfMonth = 0;
	private int year = 0;

	private static Logger LOGGER = null;
	static {
		LOGGER = LogManager.getLogger(CustomLookupAndReplace.class);
		cal.setTime(currentDate);
		setTheCurrentHour(cal.get(Calendar.HOUR) == 0 ? 12 : cal.get(Calendar.HOUR));
		setTheCurrentMinute(cal.get(Calendar.MINUTE));
		currentTime = cal.get(Calendar.AM_PM);
	}

	@Override
	public String lookup(LogEvent event, String key) {
		// TODO Auto-generated method stub
		return lookup(key);
	}

	/**
	 * This method determines how the instance file will be saved
	 * Acceptable values for the param: key, are:
	 * $(date) this represents the date
	 * %i this represents an interval count is used
	 *
	 * @param key
	 * @return
	 */
	@Override
	public String lookup(String key) {
		LOGGER.info("key: " + key);;
		if (key == null) return "";
		if (!key.contains(";")) return key;
		String retStr = "";
		String[] params = key.split(";");
		File newFile = null;
		Date instanceLogDate = null;
		String path = "logs" + File.separator;
		String value1 = params[0];


		if (value1.equalsIgnoreCase(sdf.format(currentDate))) {
			path += sdf.format(currentDate);
			// rolloverdaily
			try {
				newFile = new File(path);
				newFile.mkdir();
			} catch (NullPointerException e) {
				LOGGER.error("There was an exception. Path was " + path);
				printException(e);
			}
			return path;
		}
		// FIXME: This needs to be evaluated.
		else if (value1.equals("")) {
			File instanceFile = null;
			BufferedReader fileReader = null;
			PrintWriter pw = null;
			try {
				instanceFile = new File(path + appName + "~Instance~Logs.log");
				fileReader = new BufferedReader(new FileReader(instanceFile));
			} catch (FileNotFoundException e) {
				return sdf.format(currentDate);
			}
			String line = "";
			try {
				line = fileReader.readLine();
			} catch (IOException e) {
				LOGGER.error("There was an issue reading the file");
				printException(e);
			}
			if (line == null || line.equals("") || line.equals(StringUtils.LF)) {
				try { fileReader.close(); } catch (IOException e) { printException(e); }
				return sdf.format(currentDate);
			}
			String[] parts = line.split(" ");
			Calendar instanceLogCalendar = Calendar.getInstance();
			String asDate = null;
			String theTimeDatePart = "";
			try {
				if (parts[0].substring(1).equalsIgnoreCase("info") || parts[0].substring(1).equalsIgnoreCase("warn")) {
					theTimeDatePart = parts[2];
					instanceLogDate = sdf.parse(theTimeDatePart.substring(0, 11));
				} else {
					theTimeDatePart = parts[1];
					instanceLogDate = sdf.parse(theTimeDatePart.substring(0, 11));
				}
				instanceLogCalendar.setTime(instanceLogDate);
				asDate = sdf.format(instanceLogDate).toString();
			} catch (ParseException e) {
				printException(e);
			}
			if (instanceLogDate.before(currentDate) && instanceLogCalendar.get(Calendar.DATE) != cal.get(Calendar.DATE) ) {
				// get count of like files
				int instanceHour =
						Integer.parseInt(theTimeDatePart.substring(11, 13)) > 12 ?
								Integer.parseInt(theTimeDatePart.substring(11,13)) - 12
								:
								Integer.parseInt(theTimeDatePart.substring(11, 13));
				String instanceHourStr = instanceHour <= 9 ? "0" + instanceHour : Integer.toString(instanceHour);
				String[] allApplicationFiles = getAllApplicationFiles(path + sdf.format(instanceLogDate), instanceHour);
				int instanceCount = allApplicationFiles.length;
				String instanceCountStr = "";
				if (instanceCount > 0 && instanceCount <= 8)
					instanceCountStr = "00" + (instanceCount+1);
				else if (instanceCount >= 9 && instanceCount <= 98)
					instanceCountStr = "0" + (instanceCount+1);
				else
					instanceCountStr = Integer.toString(instanceCount+1);
				newFile = new File(path + sdf.format(instanceLogDate) + File.separator + appName + "~Instance~Logs~" + sdf.format(instanceLogDate) + instanceCountStr);
				LOGGER.info("newFile name: " + newFile.getName());
				LOGGER.info("newFile path: " + newFile.getPath());
				try {
					pw = new PrintWriter(newFile);
				} catch (FileNotFoundException e) {
					printException(e);
				}
				try {
					do {
						pw.print(line + "\n");
					} while ((line = fileReader.readLine()) != null);
				} catch (IOException e) {
					printException(e);
				} finally {
					pw.close();
				}
				retStr = sdf.format(instanceLogDate);
			} else {
				retStr = sdf.format(currentDate);
			}
			return retStr;
		}
		// value2 ${%i}
		int numOfLogs = 0;
		path += sdf.format(currentDate);
		String[] allApplicationFiles = getAllApplicationFiles(path, currentHour);
		if (StringUtils.isNumeric(value1)) numOfLogs = Integer.valueOf(value1);
		else numOfLogs = allApplicationFiles.length;

		hourChanged = updateTime(allApplicationFiles);
		newApplication = isApplicationNew(numOfLogs);
		int officialCount = numOfLogs + 1;
		if (numOfLogs == 0 || hourChanged || newApplication) {
			retStr += "00" + Integer.toString(officialCount);
		} else if (numOfLogs > 0 && numOfLogs <= 8) {
			retStr += "00" + Integer.toString(officialCount);
		} else if (numOfLogs >= 9 && numOfLogs <= 98) {
			retStr += "0" + Integer.toString(officialCount);
		} else {
			retStr += Integer.toString(officialCount);
		}
		cal.add(Calendar.DAY_OF_MONTH, -1);
		newFile = new File("logs" + File.separator + sdf.format(cal.getTime()) + File.separator + appName + "~Instance~Logs" + Integer.toString(officialCount));
		try {
			newFile.delete();
		} catch (Exception e) {
			printException(e);
		}
		return retStr;
	}

	private void setCurrentDate(Date currentDate) {
		if (currentDate == null) {
			this.currentDate = new Date();
			getCalendar().setTime(getCurrentDate());
		} else {
			getCalendar().setTime(currentDate);
		}
		setCurrentHour(getCalendar().get(Calendar.HOUR));
		setCurrentMinute(getCalendar().get(Calendar.MINUTE));
	}
	private void setCalendar(Calendar cal) {
		this.cal = cal;
	}
	private void setCurrentHour(int currentHour) {
		this.currentHour = currentHour;
	}
	private static void setTheCurrentHour(int theHour) {
		currentHour = theHour;
	}
	private void setCurrentMinute(int currentMinute) {
		this.currentMinute = currentMinute;
	}
	private static void setTheCurrentMinute(int theMinute) {
		currentMinute = theMinute;
	}
	public Date getCurrentDate() {
		return this.currentDate;
	}
	public Calendar getCalendar() { return this.cal; }
	public int getCurrnetHour() { return this.currentHour; }
	public static int getTheCurrentHour() { return currentHour; }
	public int getCurrentMinute() { return this.currentMinute; }
	public static int getTheCurrentMinute() { return currentMinute; }

	public boolean updateTime(String[] allFiles) {
		getCalendar().setTime(new Date());
		int hour = getCalendar().get(Calendar.HOUR) == 0 ? 12 : getCalendar().get(Calendar.HOUR);
		int time = getCalendar().get(Calendar.AM_PM);
		boolean hasChanged = false;
		if (getTheCurrentHour() < hour || time != currentTime) {
			hasChanged = true;
			setCurrentHour(hour);
			setTheCurrentHour(hour);
			currentTime = getCalendar().get(Calendar.AM_PM);
		}
		String finalLogsFile = "";
		if (allFiles.length != 0) {
			finalLogsFile = allFiles[allFiles.length - 1];
			for (int i=0; i<4; i++) {
				finalLogsFile = finalLogsFile.substring(finalLogsFile.indexOf("~"));
			}
			int theLastHour = Integer.parseInt(finalLogsFile.substring(0, 2));
			if (theLastHour < hour) {
				hasChanged = true;
				setCurrentHour(hour);
				setTheCurrentHour(hour);
				currentTime = getCalendar().get(Calendar.AM_PM);
			}
		}
		return hasChanged;
	}

	public boolean isApplicationNew(int numOfLogs) {
		if (numOfLogs == 0) return true;
		String newPath = "logs" + File.separator + getDateAsStr(null);
		String[] files = new File(newPath).list();
		for (String file : files) {
			int squigglyIndex = file.indexOf("~");
			String nameOfApp = file.substring(0, squigglyIndex);
			if(nameOfApp.equals(appName)) {
				return false;
			}
		}
		return true;
	}

	public String[] getAllFiles(String path) {
		String[] allFiles = null;
		if (!StringUtils.isEmpty(path)) {
			allFiles = new File(path).list();
		}
		for (String file : allFiles) {
			//LOGGER.info(file);
		}
		return allFiles;
	}

	public String[] getAllApplicationFiles(String path, int theHour) {
		String[] allFiles = null;
		if (!StringUtils.isEmpty(path)) {
			allFiles = new File(path).list();
			if (allFiles == null) {
				return new String[0];
			}
		}
		String[] tempArr = new String[allFiles.length];
		int count = 0;
		for (String file : allFiles) {
			try {
				String thisFilesName = file.substring(0, file.indexOf("~"));
				if (StringUtils.equals(thisFilesName, appName)) {
					String fileCopy = file;
					for (int i=0; i<4; i++) {
						fileCopy = fileCopy.substring(fileCopy.indexOf("~")+1);
					}
					String hour = fileCopy.substring(0, 1);
					if (Integer.parseInt(hour) == theHour) {
						tempArr[count] = file;
						count++;
					}
				}
			} catch (StringIndexOutOfBoundsException e) {
				printException(e);
			}
		}
		String[] specificFilesArr = new String[count];
		if (count != 0) {
			for (int i=0; i<count; i++) {
				specificFilesArr[i] = tempArr[i];
			}
			//for (String file : specificFilesArr) {
			//	LOGGER.info(file);
			//}
		}
		return specificFilesArr;
	}

	public int getApplicationCount(String appName, int theHour, String path) {
		String[] files = getAllFiles(path);
		int count = 0;
		for (String file : files) {
			String nameOfApp = file.substring(0, file.indexOf("~"));
			for (int i=0; i<4; i++) {
				file = file.substring(file.indexOf("~")+1);
			}
			if (nameOfApp.equals(appName) && theHour == getTheCurrentHour() && theHour == Integer.parseInt(file.substring(0, 2))) {
				count++;
			}
		}
		return count;
	}

	public String getDateAsStr(Date dt) {
		if (dt != null) setCurrentDate(dt);
		else 			setCurrentDate(new Date());
		String monthStr = "", dateOfMonthStr = "";
		try {
			month = cal.get(Calendar.MONTH) + 1;
			dateOfMonth = cal.get(Calendar.DAY_OF_MONTH);
			year = cal.get(Calendar.YEAR);
			if (month <= 9) monthStr = "0" + month;
			if (dateOfMonth <= 9) dateOfMonthStr = "0" + dateOfMonth;
		} catch (Exception e) {
			printException(e);
		}
		if (month <= 9 && dateOfMonth > 9) {
			return monthStr + "-"+Integer.toString(dateOfMonth)+"-"+Integer.toString(year);
		} else if (dateOfMonth <= 9 && month > 9) {
			return Integer.toString(month)+"-"+ dateOfMonthStr+"-"+Integer.toString(year);
		} else if (month <= 9 && dateOfMonth <= 9 ) {
			return monthStr + "-" + dateOfMonthStr + "-" + Integer.toString(year);
		} else {
			return Integer.toString(month) + "-" + Integer.toString(dateOfMonth) + "-" + Integer.toString(year);
		}
	}

	public void printException(Exception e) {
		StackTraceElement[] steArr = e.getStackTrace();
		StringBuilder sb = new StringBuilder();
		for (StackTraceElement ste : steArr) {
			sb.append(ste + "\n");
		}
		LOGGER.error(sb);
	}
}
