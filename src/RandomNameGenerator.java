import org.jsoup.Jsoup;
import java.io.IOException;
import java.util.*;

public class RandomNameGenerator {
    private static HashSet<String> names = new HashSet<>();

    private static void namesParser() throws IOException {
        for (int letter = 97; letter < 123; letter++) {
            String html = "http://www.vornamen.ch/namensliste/" + letter + ".html";
            String lastPage = (Jsoup.connect(html).get()).
                    select("a[title*=Zur letzten Seite]").attr("href");

            int end;
            if (Objects.equals(lastPage, "")) {
                end = Integer.parseInt((String) lastPage.subSequence(2, lastPage.indexOf('.')));
            } else {
                end = 1;
            }
            for (int j = 1; j <= end; j++) {
                String newHtml = "http://www.vornamen.ch/namensliste/" + letter + "," + j + ".html";
                strtok((Jsoup.
                        connect(newHtml).get()).
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
        return namen.get(new Random().nextInt(names.size()));
    }

    public static void main(String[] args) throws IOException {
        System.out.println(randomNameGenerator());
    }
}
