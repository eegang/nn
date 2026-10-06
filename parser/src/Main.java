import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    static HttpClient client = HttpClient.newHttpClient();

    static Stack<String> history = new Stack<>();

    public static void main(String[] args) throws Exception {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите ссылку: ");
        String url = scanner.nextLine();

        while (true) {

            history.push(url);

            ArrayList<String> links = getLinks(url);

            System.out.println("\nТекущая страница:");
            System.out.println(url);

            System.out.println("\nСсылки:");

            if (links.isEmpty()) {
                System.out.println("Ссылок нет.");
            }

            for (int i = 0; i < links.size(); i++) {
                System.out.println((i + 1) + ". " + links.get(i));
            }

            System.out.println("\n0. Назад");

            System.out.print("\nВведите номер ссылки: ");
            int number = scanner.nextInt();

            if (number == 0) {

                if (history.size() <= 1) {
                    break;
                }

                history.pop();

                url = history.pop();

                continue;
            }


            if (number < 1 || number > links.size()) {
                System.out.println("Такой ссылки нет.");
                history.pop();
                continue;
            }

            url = links.get(number - 1);
        }
    }


    static ArrayList<String> getLinks(String url) throws Exception {

        ArrayList<String> links = new ArrayList<>();


        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();


        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        String html = response.body();

        Pattern pattern = Pattern.compile(
                "<a//s+href=[\"']([^\"']+)[\"']",
                Pattern.CASE_INSENSITIVE
        );

        Matcher matcher = pattern.matcher(html);

        while (matcher.find()) {

            String link = matcher.group(1);


            if (link.startsWith("/")) {
                URI currentUri = URI.create(url);

                link = currentUri.getScheme() + "://"
                        + currentUri.getHost()
                        + link;
            }

            if (link.startsWith("http://") || link.startsWith("https://")) {
                links.add(link);
            }
        }

        return links;
    }
}