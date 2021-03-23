package terminals;

import java.util.Scanner;

public abstract class Terminal {

    public int launchUI(String title, String[] options) {
        int counter = 0, maxLen = 0;
        for (String s : options) if (s.length() > maxLen)  maxLen = s.length();
        String repeat = "─".repeat(2 * maxLen + title.length());
        System.out.println("┌" + repeat + "┐");
        System.out.println("│" + " ".repeat(maxLen) + title + " ".repeat(maxLen) + "│");
        System.out.println("├" + repeat + "┤");
        for (String opt : options){
            System.out.println("│  " + (++counter) + "- " + opt + " ".repeat(2*maxLen + title.length() - opt.length() - 4 - ((int) Math.log10(counter) + 1) ) + "│");
        }
        System.out.println("└" + repeat + "┘");
        return getOption(options.length);
    }

    private int getOption(int n) {
        int num = 0;
        Scanner sc = new Scanner(System.in);
        while(true){
            try {
                System.out.print("Option: ");
                num = sc.nextInt();
                if (0 < num && num <= n){
                    sc.close();
                    return num - 1;
                }
                System.out.println("Invalid Option!");
            } catch (Exception e){
                System.out.println("Invalid Option!");
                sc.next();
            }

        }
    }

}
