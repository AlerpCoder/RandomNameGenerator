/*Copyright AlerpCoder*/

import org.jsoup.Jsoup;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RandomNameGenerator {
    private static HashSet<String> names = new HashSet<>();

    private static boolean isBoy = false;

    public static void namesParser() throws IOException, URISyntaxException {
        //TODO Parallel mit Threads schreiben damit
        Thread[] threads = new Thread[3];
        File file = new File(RandomNameGenerator.class.getClassLoader().getResource("RandomNameGenerator.class").toURI());
        String txtName;
        if (isBoy) {
            txtName = "\\namesBoy.txt";
        } else {
            txtName = "\\namesGirl.txt";
        }

        File newfile = new File(file.getParentFile().toString() + txtName);
        FileReader reader;

        if (newfile.exists()) {
            reader = new FileReader(newfile);
            BufferedReader buffer = new BufferedReader(reader);
            strtok(buffer.readLine());
        } else {
            String maleFemale;
            if (isBoy) {
                maleFemale = "jungennamen";
            } else {
                maleFemale = "maedchennamen";
            }

            int start = 97;
            int mid = (97 - 123) / 3;
            int end;
            int rest = (97 - 123) % 3;
            for (int i = 0; i < threads.length; i++) {
                end = start + mid;

                threads[i] = new WebsiteDL(start, end, maleFemale);
                start = start + mid;
                end= (i==0)?end=end+rest;
                if (i == 0) {
                    end += rest;

                }
            }
            saveNames(file);
        }
    }


    public static void saveNames(File file) throws IOException, URISyntaxException {
        String txtName;
        if (isBoy) {
            txtName = "\\namesBoy.txt";
        } else {
            txtName = "\\namesGirl.txt";
        }

        File newfile = new File(file.getParentFile().toString() + txtName);
        PrintWriter writer;
        writer = new PrintWriter(newfile, "UTF-8");
        writer.println(names);
        writer.close();
    }

    public static void strtok(String text) throws IOException {
        StringTokenizer stok = new StringTokenizer(text);
        for (int i = 0; i < stok.countTokens(); i++) {
            names.add(stok.nextToken());
        }
    }


    public static String randomNameGenerator() throws IOException, URISyntaxException {
        namesParser();
        List<String> namen = new ArrayList<>(names.size());
        /*names.parallelStream().forEach(namen::add);*/
        namen.addAll(names);
        return namen.get(new Random().nextInt(names.size()));
    }

    public static void userInteraction() {
        Scanner input = new Scanner(System.in);
        System.out.println("Schould the name for a girl (girl, g, m,) or a boy (boy, b, j)? (Please insert one word/character in the free space)");
        String gender = input.next();
        genderDecider(gender);
    }

    public static void genderDecider(String gender) {
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


    static class WebsiteDL extends Thread {
        /*websiteDL(int start, int end){*/
        int start;
        int end;
        String maleFemale;
        RandomNameGenerator test;

        public WebsiteDL(int start, int end, String maleFemale) {
            this.start = start;
            this.end = end;
            this.maleFemale = maleFemale;
            this.test = test;
        }

        public void run() {
            for (int letter = start; letter < end; letter++) {
                String html = "http://www.vornamen.ch/" + maleFemale + "/" + (char) letter + ".html";
                String lastPage = null;
                try {
                    lastPage = (Jsoup.connect(html).get()
                            .select("a[title*=Zur letzten Seite]").attr("href"));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                int end;
                if (lastPage.compareTo("") != 0) {
                    end = Integer.parseInt((String) lastPage.subSequence(2, lastPage.indexOf('.')));
                } else {
                    end = 1;
                }
                for (int j = 1; j <= end; j++) {
                    String newHtml = "http://www.vornamen.ch/" + maleFemale + "/" + (char) letter + "," + j + ".html";
                    try {
                        strtok((Jsoup.connect(newHtml).get()).
                                removeClass("trend").
                                removeClass("jungencharts").
                                removeClass("maedchencharts").
                                select("a[href*=../name/]").text());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void main(String[] args) throws IOException, URISyntaxException {
        userInteraction();
        System.out.println(randomNameGenerator());
    }
}
