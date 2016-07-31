import org.jsoup.Jsoup;

import java.io.*;
import java.net.URISyntaxException;
import java.util.*;

public class RandomNameGenerator {
    private static HashSet<String> names = new HashSet<>();

    private static boolean isBoy = false;

    private static void namesParser() throws IOException, URISyntaxException, InterruptedException {
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
            Thread[] threads = new Thread[3];
            String maleFemale;
            if (isBoy) {
                maleFemale = "jungennamen";
            } else {
                maleFemale = "maedchennamen";
            }

            int start = 97;
            int mid = (123 - 97) / 3;
            int end;
            int rest = (123 - 97) % 3;
            for (int i = 0; i < threads.length; i++) {


                if (i == 2) {
                    end = start + mid + rest;
                } else {
                    end = start + mid;
                }
                threads[i] = new WebsiteDL(start, end, maleFemale);
                start = end;
                threads[i].start();
            }
            for (Thread thread : threads) {
                thread.join();
            }
            saveNames(file);
        }
    }


    private static void saveNames(File file) throws IOException, URISyntaxException {
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

    private static void strtok(String text) throws IOException {
        StringTokenizer stok = new StringTokenizer(text);
        for (int i = 0; i < stok.countTokens(); i++) {
            names.add(stok.nextToken());
        }
    }


    private static String randomNameGenerator() throws IOException, URISyntaxException, InterruptedException {
        namesParser();
        List<String> namen = new ArrayList<>(names.size());
        namen.addAll(names);
        return namen.get(new Random().nextInt(names.size()));
    }

    private static void userInteraction() {
        Scanner input = new Scanner(System.in);
        System.out.println("Schould the name for a girl (girl, g, m,) or a boy (boy, b, j)? (Please insert one word/character in the free space)");
        String gender = input.next();
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


   private static class WebsiteDL extends Thread {
        int start;
        int end;
        String maleFemale;

        private WebsiteDL(int start, int end, String maleFemale) {
            this.start = start;
            this.end = end;
            this.maleFemale = maleFemale;
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


    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {
        userInteraction();
        System.out.println(randomNameGenerator());
    }
}
