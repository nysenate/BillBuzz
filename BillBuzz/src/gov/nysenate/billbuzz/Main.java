package gov.nysenate.billbuzz;


public class Main {
	private static String _dateInputRegExp = "[\\d]{4}+\\-[\\d]{2}+\\-[\\d]{2}+T[\\d]{2}+\\:[\\d]{2}+";
	public static void main(String[] args) throws Exception {
		if(args.length == 0) {
			new BillBuzz();
		}
		else if(args[0].compareTo("-m") == 0) {
			if(args[1].matches(_dateInputRegExp)) {
				new BillBuzz(args[1]);
			}
			else {
				System.out.println("Invalid paramters for -m flag, correct format is: BillBuzz -m \"YYYY-MM-DD\"");
			}
		}
		else {
			System.out.println("Invalid paramters, correct usage is: BillBuzz or BillBuzz -m \"YYYY-MM-DD\"");
		}
//		new BillBuzz("2010-07-01T00:00");	
	}
}

