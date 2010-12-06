package comment.parser;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import comment.model.persist.DisqusComment;
import comment.model.persist.DisqusThread;

public class DisqusAnalytics {

	public static final String DATE = "11/13/2010";
	
	public static void main(String[] args) throws IOException {		
		String d1 = "2010-11-07";
		String d2 = "2010-11-13";
		generateReport(d1,d2,"disquscomments.csv");		

		
//		yearlyReport();
		
	}
	
	
	public static void generateReport(String d1, String d2, String fn) throws IOException {
				
		Collection<DisqusThread> c = PMF.threadByDate(d1,null);

		List<DisqusThread> dt = ObjectHelper.postsByDate(c, d1, d2);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(fn)));
		
		bw.write("date,url,comments");
		bw.newLine();
		
		int count = 0;
		
		for(DisqusThread d:dt) {
			if(count >= 5)
				break;
			
			bw.write(DATE + "," + d.getUrl() + "," + d.getSize());
			bw.newLine();
			
			
			count++;
		}
		
		
		bw.close();
	}
	
	
	public static void yearlyReport() {
		
		String[] years = {"2009", "2010"};
		String[] months = {"01","02","03","04","05","06","07","08","09","10","11","12"};
		String[] days =   {"31", "28", "31", "30", "31", "30", "31", "31", "30", "31", "30", "31"};
		Date d = new Date();
		
		for(int i = 0; i < years.length; i++) {
			
			for(int j = 0; j < months.length; j++) {
				
				for(int k = 1; k <= new Integer(days[j]); k++) {
					String nextYear = years[i];
					String nextMonth = months[j];
					String nextDay = Integer.toString(k);
					
					if(k == new Integer(days[j])) {
						nextDay = "01";
						if(j == (months.length-1)) {
							nextMonth = "01";
							
							if(i == (years.length-1)) {
								nextYear = "2011";
								
							}
							else {
								nextYear = years[i+1];
								
							}
							
						}
						else {
							nextMonth = months[j+1];
							
						}
					}
					else {
						nextDay = Integer.toString(k+1);
						
					}
					
					
					
					String rawPresentDate = years[i] + "-" + months[j] + "-" + k;
					Date compiledPresentDate = ObjectHelper.searchDate(rawPresentDate);
					
					String rawNextDate = nextYear + "-" + nextMonth + "-" + nextDay;
					Date compiledNextDate = ObjectHelper.searchDate(rawNextDate);
					
					
					
					Collection<DisqusThread> c = PMF.threadByDate(rawPresentDate,null);

					List<DisqusThread> lst = ObjectHelper.postsByDate(c, rawPresentDate, rawNextDate);
					
					int count = 0;
					
					for(DisqusThread dt:lst) {
						count += dt.getComments().size();
					}
					
					System.out.println(rawPresentDate.replaceAll("-", "/") + "," + count);

				}
				
			}
			
		}
	}
}
