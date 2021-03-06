package fmlMetro;

import java.util.List;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 *
 * @author termaat
 */
public class FreakyDate {

        static final int CALENDAR_WIDTH = 7;
	static final int CALENDAR_HEIGHT = 6;
	int[][] calDates = new int[CALENDAR_HEIGHT][CALENDAR_WIDTH];
	int year, month, day, lastDate, week;
	final int[] lastDateOfMonth = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 }; // JFMAMJJASOND
        final String[] MonthText = {"January", "February", "March", "April", "May",
                "June", "July", "August", "September", "October", 
                "November", "December"};
        
	Calendar today = Calendar.getInstance(), cal;

	public void setCurrentDate() { // Sets current calendar data based on
									// getInstance of today
		this.year = this.today.get(Calendar.YEAR);
		this.month = this.today.get(Calendar.MONTH);
		this.day = this.today.get(Calendar.DAY_OF_MONTH);
                this.week = this.today.get(Calendar.WEEK_OF_YEAR);
		//initializeCalendarData(this.today);
	}
        
        public boolean IsSunday(java.util.Date isSunDate){
            
            Calendar sun_cal = Calendar.getInstance();
            sun_cal.setTime(isSunDate);
             if(sun_cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                 return true;   
                }           
             return false;
        }
        
        public String getMonthDesc(int modifier){
            Calendar m_desc = Calendar.getInstance();
            m_desc.add(Calendar.MONTH, + modifier);
            String m_text = m_desc.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
            return m_text;
        }
        
        
        public Integer[] getWeeksInMonth(int month, int year) {
            List<Integer> list = new ArrayList<Integer>();
            SimpleDateFormat format = new SimpleDateFormat("w");

            Calendar startDate = Calendar.getInstance();
            Calendar endDate = Calendar.getInstance();

            startDate.set(year, month - 1, 1);
            startDate.setMinimalDaysInFirstWeek(4);

            endDate.set(year, month, 0);

            // Iterate between the start and stop days
            while (startDate.getTimeInMillis() <= endDate.getTimeInMillis()) {

            Calendar cal = Calendar.getInstance();
            cal.setTime(startDate.getTime());
            cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            // System.out.println("Here here " +
            // cal.get(Calendar.DAY_OF_MONTH));

            // Start of the week is Monday - check if dates 1, 2, 3 fall after
            // Wednesday (specified 4 day rule)
            if ((startDate.get(Calendar.DAY_OF_WEEK) > Calendar.THURSDAY)
                && (startDate.get(Calendar.DAY_OF_MONTH) < 4)) {

                // If they do, move the dates to next immediate start of the
                // week - making it to 4th
                startDate.add(Calendar.DAY_OF_MONTH,
                    (4 - startDate.get(Calendar.DAY_OF_MONTH)));

                // Similarly - check if last dates of the month make less than 4
                // for that month
            } else if ((startDate.get(Calendar.DAY_OF_MONTH) > 25)
                && ((startDate.getActualMaximum(Calendar.DAY_OF_MONTH) - (cal
                        .get(Calendar.DAY_OF_MONTH))) + 1) < 4) {
                // If they do, move the dates to next immediate start of the
                // week - making it to next month

                startDate
                    .add(Calendar.DAY_OF_MONTH,
                            (startDate
                            .getActualMaximum(Calendar.DAY_OF_MONTH) - startDate
                            .get(Calendar.DAY_OF_MONTH)) + 1);

            } else {
                // Get the number of the week
                // System.out.println(startDate.getTime());
                int week = Integer.parseInt(format.format(startDate.getTime()));

                // If December, check if the next year's 1st week falls under
                // the 4 day rule, if it does then ignore
                if (!(startDate.get(Calendar.MONTH) == Calendar.DECEMBER && week == 1))
                    list.add(week);

                    startDate.add(Calendar.WEEK_OF_MONTH, 1);
            }
        }

            return list.toArray(new Integer[list.size()]);
        }

        //old depricated date routine
        public Date getActualFOM(int modifier){
            Calendar fom = Calendar.getInstance();
            fom.set(Calendar.DATE, fom.getActualMinimum(Calendar.DATE));
            fom.add(Calendar.MONTH, + modifier);
            java.util.Date firstDayOfMonth = fom.getTime();
            java.sql.Date sqlDate1 = new java.sql.Date(firstDayOfMonth.getTime());
            
            return sqlDate1;
        }
        
        // returns the end of the month date given a date. also adds or 
        // subtracts based on modifier.  0 retunrs eom current month
        public Date getEOM(int modifier){
            Calendar eom = Calendar.getInstance();
            eom.set(Calendar.DATE, eom.getActualMaximum(Calendar.DATE));
            eom.add(Calendar.MONTH, + modifier);
            java.util.Date lastDayOfMonth = eom.getTime();
            java.sql.Date sqlDate30 = new java.sql.Date(lastDayOfMonth.getTime());
            
            return sqlDate30;
        }
        public int getToday(){
            Calendar today = Calendar.getInstance();
            String t_mon = leftPad((today.get(Calendar.MONTH) + 1),2);
            String t_day = leftPad(today.get(Calendar.DAY_OF_MONTH),2);
            String t_yr = String.valueOf(today.get(Calendar.YEAR));
            String t_Date8 = t_yr + t_mon + t_day;
            int i_Date8 = Integer.parseInt(t_Date8);
            
            return i_Date8;
        }
        public int getIntEOM(int modifier){
            Calendar eom = Calendar.getInstance();
            eom.add(Calendar.MONTH, + modifier);
            eom.set(Calendar.DATE, eom.getActualMaximum(Calendar.DATE));
            String t_mon = leftPad((eom.get(Calendar.MONTH) + 1),2);
            String t_day = leftPad(eom.get(Calendar.DAY_OF_MONTH),2);
            String t_yr = String.valueOf(eom.get(Calendar.YEAR));
            String t_Date8 = t_yr + t_mon + t_day;
            int i_Date8 = Integer.parseInt(t_Date8);
            
            return i_Date8;
        }
        //returns fom date in integer format 20180301
        public int getIntFOM(int modifier){
            Calendar fom = Calendar.getInstance();
            fom.add(Calendar.MONTH, + modifier);
            fom.set(Calendar.DATE, fom.getActualMinimum(Calendar.DATE));
            String t_mon = leftPad((fom.get(Calendar.MONTH) + 1),2);
            String t_day = leftPad(fom.get(Calendar.DAY_OF_MONTH),2);
            String t_yr = String.valueOf(fom.get(Calendar.YEAR));
            String t_Date8 = t_yr + t_mon + t_day;
            int i_Date8 = Integer.parseInt(t_Date8);
            
            return i_Date8;
        }
        
        public Date getFOM(int modifier){
            Calendar fom = Calendar.getInstance();
            fom.set(Calendar.DATE, fom.getActualMaximum(Calendar.DATE));
            fom.add(Calendar.MONTH, + modifier);
            java.util.Date firstDayOfMonth = fom.getTime();
            java.sql.Date sqlDate1 = new java.sql.Date(firstDayOfMonth.getTime());
            
            return sqlDate1;
            
        }
        
        public int getIntEOW (int modifier) {
            int dayofweek = 0;
            Calendar eow = Calendar.getInstance();
            dayofweek = eow.get(Calendar.DAY_OF_WEEK);
            int lastdaymodifier = modifier - dayofweek;
            eow.add(Calendar.DAY_OF_WEEK,  + lastdaymodifier +1); //+1 for Sun
            String t_mon = leftPad((eow.get(Calendar.MONTH) + 1),2);
            String t_day = leftPad(eow.get(Calendar.DAY_OF_MONTH),2);
            String t_yr = String.valueOf(eow.get(Calendar.YEAR));
            String t_Date8 = t_yr + t_mon + t_day;
            int i_Date8 = Integer.parseInt(t_Date8);
            
            return i_Date8;
        }
        
        // 8 needs to be the modifier and 8, 16, 24, 32...
        public Date getEOW(int modifier) {
            int dayofweek = 0;
        
            //get current day
            Calendar eow = Calendar.getInstance();
            dayofweek = eow.get(Calendar.DAY_OF_WEEK);
            // System.out.println("eow - day of week today is " + dayofweek);
            //we want the last day to be monday of the next week so we can
            //return sunday in the list
            int lastdaymodifier = modifier - dayofweek;
            //System.out.println("eow - lastdaymodifier = " + modifier + " - " + dayofweek);
            //add this number to calender to get the last date of the week
            eow.add(Calendar.DAY_OF_WEEK,  + lastdaymodifier +1);
            //System.out.println("eow - adding " + lastdaymodifier + " + 1 to get to Sunday");
            java.util.Date lastDateOfWeek = eow.getTime();
            java.sql.Date sqlDate7 = new java.sql.Date(lastDateOfWeek.getTime());
            //System.out.println("eow - returning " + sqlDate7);
            return sqlDate7;
        
        }
        
        // get fow integer date
        public int getIntFOW(int modifier){
            int dayofweek = 0;
            Calendar fow = Calendar.getInstance();
            dayofweek = fow.get(Calendar.DAY_OF_WEEK);
            int lastdaymodifier = modifier - dayofweek;
            fow.add(Calendar.DAY_OF_WEEK,  + lastdaymodifier +1);
            fow.add(Calendar.DAY_OF_WEEK,  - 6);
            String t_mon = leftPad((fow.get(Calendar.MONTH) + 1),2);
            String t_day = leftPad(fow.get(Calendar.DAY_OF_MONTH),2);
            String t_yr = String.valueOf(fow.get(Calendar.YEAR));
            String t_Date8 = t_yr + t_mon + t_day;
            int i_Date8 = Integer.parseInt(t_Date8);
            
            return i_Date8;
        }
        
        public Date getFOW(int modifier){
            int dayofweek = 0;
        
            Calendar fow = Calendar.getInstance();
            dayofweek = fow.get(Calendar.DAY_OF_WEEK);
            //System.out.println("fow - day of week today is " + dayofweek);
            int lastdaymodifier = modifier - dayofweek;
            //System.out.println("fow - lastdaymodifier = " + modifier + " - " + dayofweek);
            fow.add(Calendar.DAY_OF_WEEK,  + lastdaymodifier +1);
            //System.out.println("fow - adding " + lastdaymodifier + " + 1 to get to Sunday");
            // now that we know the last date of the week we can minus 8 to 
            // get the first date of the week
            fow.add(Calendar.DAY_OF_WEEK,  - 7);
            //System.out.println("fow - now subtracting 7 to get day before Monday");
            java.util.Date firstDateOfWeek = fow.getTime();
            java.sql.Date sqlDate1 = new java.sql.Date(firstDateOfWeek.getTime());
            //System.out.println("fow SQL - returning " + sqlDate1);
            return sqlDate1;
        }
        
        public Date getActualFOW(int modifier){
            int dayofweek = 0;
        
            Calendar fow = Calendar.getInstance();
            dayofweek = fow.get(Calendar.DAY_OF_WEEK);
            //System.out.println("fow actual - day of week today is " + dayofweek);
            int lastdaymodifier = modifier - dayofweek;
            //System.out.println("fow actual - lastdaymodifier = " + modifier + " - " + dayofweek);
            fow.add(Calendar.DAY_OF_WEEK,  + lastdaymodifier +1);
            //System.out.println("fow actual - adding " + lastdaymodifier + " + 1 to get to Sunday");
            // now that we know the last date of the week we can minus 8 to 
            // get the first date of the week
            fow.add(Calendar.DAY_OF_WEEK,  - 6);
            //System.out.println("fow actual - now subtracting 6 to get Monday");
            java.util.Date firstDateOfWeek = fow.getTime();
            java.sql.Date sqlDate1 = new java.sql.Date(firstDateOfWeek.getTime());
            //System.out.println("fow actual  - returning " + sqlDate1);
            return sqlDate1;
        }
        
        public static String leftPad(int n, int padding) {
            return String.format("%0" + padding + "d", n);
        }
        
        public void moveMonth(int mon) {
		month += mon;
		if (month > 11)
			while (month > 11) {
				year++;
				month -= 12;
			}
		else if (month < 0)
			while (month < 0) {
				year--;
				month += 12;
			}
		cal = new GregorianCalendar(year, month, day);
		//initializeCalendarData(cal);
	}
}
