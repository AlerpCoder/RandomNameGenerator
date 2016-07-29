import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.*;

public class RandomNameGenerator {
    private static HashSet<String> names = new HashSet<>();
    private static boolean isBoy=false;

    private static void namesParser() throws IOException {
        userInteraction();

        String maleFemale;
        if(isBoy){
            maleFemale="jungennamen";
        }else{
            maleFemale="maedchennamen";
        }
        for (int letter = 97; letter < 123; letter++) {
            String html = "http://www.vornamen.ch/"+maleFemale+"/" + (char)letter + ".html";
            String lastPage = (Jsoup.connect(html).get()).
                    select("a[title*=Zur letzten Seite]").attr("href");

            int end;
            if (lastPage.compareTo("")!=0) {
                end = Integer.parseInt((String) lastPage.subSequence(2, lastPage.indexOf('.')));
            } else {
                end = 1;
            }
            for (int j = 1; j <= end; j++) {
                String newHtml = "http://www.vornamen.ch/"+maleFemale+"/" +(char) letter + "," + j + ".html";
                strtok((Jsoup.connect(newHtml).get()).
                        removeClass("trend").
                        removeClass("jungencharts").
                        removeClass("maedchencharts").
                        select("a[href*=../name/]").text());
            }
        }
    }

    private static void strtok(String text) throws IOException {
        StringTokenizer stok = new StringTokenizer(text);
        for (int i = 0; i < stok.countTokens(); i++) {
            names.add(stok.nextToken());
        }
    }


    private static String randomNameGenerator() throws IOException {
        namesParser();
        List<String> namen = new ArrayList<>(names.size());
        namen.addAll(names);
        System.out.println(namen.size());
        return namen.get(new Random().nextInt(names.size()));
    }

    private static void userInteraction() {
        Scanner input= new Scanner(System.in);
        System.out.println("Schould the name for a girl (girl, g, m,) or a boy (boy, b, j)? (Please insert one word/character in the free space)");
        String gender= input.next();
        genderDecider(gender);
    }

    private static void genderDecider(String gender) {
        switch (gender) {
            case "m":
            case "g":
            case "girl":
                isBoy = false;
                break;
            case "j":
            case "b":
            case "boy":
                isBoy = true;
                break;
            default:
                System.out.println("Please insert a valid word or letter in the input box");
                userInteraction();
                break;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(randomNameGenerator());
    }


}
