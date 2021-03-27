import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Scanner;


public class Main {

    public static void main(String[] args) throws ParseException {
        System.out.println("Hello SD-Project");

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
        Scanner sc = new Scanner(System.in);
        String s = sc.nextLine();
        sdf.setLenient(false);
        sdf.parse(s);
        System.out.println(sdf.getCalendar().getTime());
        GregorianCalendar g = (GregorianCalendar) sdf.getCalendar();
        Date d = new Date();
    }
}
