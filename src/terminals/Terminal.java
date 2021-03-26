package terminals;

import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.Scanner;

/**
 *  An abstract class for an Terminal
 */
public abstract class Terminal {
    public final static String abortCode = "QUIT";
    public final static String dateFormat = "dd/MM/yyyy";
    protected Scanner sc;
    protected SimpleDateFormat sdf;

    /**
     *  Builder
     */
    public Terminal(){
        this.sc = new Scanner(System.in);
        this.sdf = new SimpleDateFormat(dateFormat);
        sdf.setLenient(false);
    }

    /**
     * A generic terminal UI generator
     * @param title String with the menu Title (header)
     * @param options [Optional] Options for the user to choose
     * @return This Terminal
     */
    private Terminal launchUI(String title, String[] options) {
        int counter = 0, maxLen = title.length();
        for (String s : options) if (s.length() > maxLen)  maxLen = s.length();
        String repeat = "─".repeat(2 * maxLen + title.length());
        System.out.println("┌" + repeat + "┐");
        System.out.println("│" + " ".repeat(maxLen) + title + " ".repeat(maxLen) + "│");
        if (options.length != 0){
            System.out.println("├" + repeat + "┤");
            for (String opt : options){
                System.out.println("│  " + (++counter) + "- " + opt + " ".repeat(2*maxLen + title.length() - opt.length() - 4 - ((int) Math.log10(counter) + 1) ) + "│");
            }
            System.out.println("├" + repeat + "┤");
            System.out.println("|  0- Back" + " ".repeat(2*maxLen + title.length() - "Back".length() - 5) + "│");

        }
        System.out.println("└" + repeat + "┘");
        return this;
    }

    /**
     * A launchUI wrapper for a Header UI
     * @param title String with the menu Title (header)
     * @return This Terminal
     */
    public Terminal header(String title){
        return this.launchUI(title, new String[]{});
    }

    /**
     * A launchUI wrapper for getting options
     * @param title String with the menu Title (header)
     * @param options Options for the user to choose
     * @return An int of the option selected
     */
    public int choose(String title, String[] options){
        return this.launchUI(title, options).getOption(options.length);
    }

    /**
     * A function to return an option
     * @param n Range of the options [0..n]
     * @return An int of the option selected
     */
    public int getOption(int n) {
        int num;
        while(true){
            try {
                System.out.print("Option: ");
                num = sc.nextInt();
                if (0 < num && num <= n){
                    return num;
                }
            } catch (Exception e){
                sc.next();
            }
            System.out.println("Invalid Option!");

        }
    }

    /**
     * Ask user for a String that does not contain "|", ";" and is different of "\n"
     * @param label Asking label
     * @param abortFlag boolean that indicates if this procedure should be aborted
     * @return The User inputted String or null if the abortCode is entered
     */
    public String parseString(String label, boolean abortFlag) {
        String input;
        if (!abortFlag){
            while (true) {
                /* Ask for input */
                System.out.print(label + ": ");
                input = sc.nextLine();

                /* Check abort code */
                if (input.equals(abortCode)) {
                    System.out.println("Aborting...");
                    return null;
                }

                /* Check if input is valid */
                if (input.length() != 0 && !input.contains(";") && !input.contains("|"))
                    return input;

                System.out.println("Invalid " + label + "!");
            }
        }
        return null;
    }

    /**
     * Ask user for a positive (>= 0) int
     * @param label Asking label
     * @param abortFlag boolean that indicates if this procedure should be aborted
     * @return The User inputted number or -1 if the abortCode is entered
     */
    public int parsePositiveInt(String label, boolean abortFlag) {
        String input;
        int value;
        if (!abortFlag) {
            while (true) {
                try {
                    /* Ask for input */
                    System.out.print(label + ": ");
                    input = sc.nextLine();

                    /* Check abort code */
                    if (input.equals(abortCode)) {
                        System.out.println("Aborting...");
                        return -1;
                    }

                    /* Check if input is valid */
                    value = Integer.parseInt(input);
                    if (value >= 0)
                        return value;
                } catch (Exception ignore) {}
                System.out.println("Invalid " + label + "!");
            }
        }
        return -1;
    }

    /**
     * Ask user for a date in the format specified in Terminal.dateFormat
     * @param label Asking label
     * @param abortFlag boolean that indicates if this procedure should be aborted
     * @returnThe User inputted GreogorianDate or null if the abortCode is entered
     */
    public GregorianCalendar parseDate(String label, boolean abortFlag) {
        String input;
        if(!abortFlag) {
            while (true) {
                try {
                    System.out.print(label + " [dd/mm/yyyy]: ");
                    input = sc.nextLine();

                    if (input.equals(abortCode)) {
                        System.out.println("Aborting...");
                        return null;
                    }
                    sdf.parse(input);
                    return (GregorianCalendar) sdf.getCalendar();
                } catch (Exception e) {
                    System.out.println("Invalid " + label + "!");
                }
            }
        }
        return null;
    }

    /**
     * A script to clear the Terminal 
     * #FIXME might be system dependent!
     * @return This Terminal
     */
    public Terminal clear(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        return this;
    }

}
