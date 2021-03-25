package terminals;

import java.util.Scanner;

public abstract class Terminal {
    protected Scanner sc;

    public Terminal(){
        this.sc = new Scanner(System.in);
    }

    public Terminal launchUI(String title, String[] options) {
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
        }
        System.out.println("└" + repeat + "┘");
        return this;
    }

    public int getOption(int n) {
        int num = 0;
        while(true){
            try {
                System.out.print("Option: ");
                num = sc.nextInt();
                if (0 < num && num <= n){
                    return num;
                }
                System.out.println("Invalid Option!");
            } catch (Exception e){
                System.out.println("Invalid Option!");
                sc.next();
            }

        }
    }

    public Terminal clear(){
        System.out.print("\033[H\033[2J");
        System.out.flush();
        return this;
    }

}
