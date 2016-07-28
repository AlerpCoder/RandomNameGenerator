import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;

public class RandomNameGenerator {
    private static HashSet<String> names = new HashSet<>();

    public HashSet<String> getNames() {
        return names;
    }

    public void setNames(HashSet<String> names) {
        this.names = names;
    }

    private static void namensParser(/*Path path*/) throws IOException {
        for (int i = 97; i < 123; i++) {
            char letter = (char) i;
            String html = "http://www.vornamen.ch/namensliste/" + letter + ".html";
            Document doc = Jsoup.connect(html).get();
            String lastPage = doc.select("a[title*=Zur letzten Seite]").attr("href");

            int end = 0;
            if (lastPage != "") {
                end = Integer.parseInt((String) lastPage.subSequence(2, lastPage.indexOf('.')));
            } else {
                end = 1;
            }
            for (int j = 1; j <= end; j++) {
                String newhtml = "http://www.vornamen.ch/namensliste/" + letter + "," + j + ".html";
                Document newdoc = Jsoup.connect(newhtml).get();
                strtok(newdoc.select("a[href*=../name/]").text(), (char) i);
            }

        }
    }

    private static String[] strtok(String text, char letter) throws IOException {
        StringTokenizer stok = new StringTokenizer(text);
        String tokens[] = new String[stok.countTokens()];
        for (int i = 0; i < tokens.length; i++) {
            names.add(stok.nextToken());
        }
        return tokens;
    }

    private static String randomNameGenerator() throws IOException {
        namensParser();
        List<String> namen = new ArrayList<>(names.size());
        int i = 0;
        for (String name : names) {
            namen.add(name);
        }
        Random random = new Random();
        int randomNumber = random.nextInt(names.size());
        return namen.get(randomNumber);

    }

    public static void main(String[] args) throws IOException {
        System.out.println(randomNameGenerator());
    }
}
