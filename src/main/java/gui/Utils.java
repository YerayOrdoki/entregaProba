package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class Utils {
	public static ArrayList<String> getStatus() {
		String lang=Locale.getDefault().toString();
		if (lang.compareTo("en")==0) 
			return new ArrayList<String>(Arrays.asList("New","Very Good","Acceptable","Very Used"));
		if (lang.compareTo("es")==0) 
			return new ArrayList<String>(Arrays.asList("Nuevo","Muy Bueno","Aceptable","Lo ha dado todo"));
		if (lang.compareTo("eus")==0) 
			return new ArrayList<String>(Arrays.asList("Berria","Oso Ona","Egokia","Oso zaharra"));
		return null;
	}
	public static String getStatus(int t) {
		ArrayList<String> status=getStatus();
		return status.get(t);
	}
}
