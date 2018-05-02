import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserInterface {
	
	static BufferedReader con = new BufferedReader(new InputStreamReader(System.in));
	
	public void show_menu() {
		System.out.println("--------------------------------------");
		System.out.println("- (1) Visa alla föreställningar för alla salonger   -");
		System.out.println("- (2) Visa alla föreställningar för specifik salong -");
		System.out.println("- (3) Skapa föreställning                           -");
		System.out.println("- (4) Boka biljett                           -");
		System.out.println("--------------------------------------");
	}
	
	// Get Int from console
	public static int inputInt() {
		int myInt = 0;
		try {
			myInt = Integer.parseInt(con.readLine());
		} catch (NumberFormatException | IOException e) {
			System.out.println("Bad input");
			myInt = inputInt();
		}

		return myInt;
	}

}
