/*
copyright bla bla bla whatever yeah i didnt make this
worst client did check em out at the link in the description guyz!!!
 */
package net.shadow.plugin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {
    public String translate(String text, String langFrom, String langTo) {
        String html = getHTML(text, langFrom, langTo);
        String translated = parseHTML(html);

        if (text.equalsIgnoreCase(translated))
            return null;

        return translated;
    }

    private String getHTML(String text, String langFrom, String langTo) {
        URL url = createURL(text, langFrom, langTo);

        try {
            URLConnection connection = setupConnection(url);

            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder html = new StringBuilder();

                String line;
                while ((line = br.readLine()) != null)
                    html.append(line).append("\n");

                return html.toString();
            }

        } catch (IOException e) {
            return null;
        }
    }

    private URL createURL(String text, String langFrom, String langTo) {
        try {
            String encodedText = URLEncoder.encode(text.trim(), StandardCharsets.UTF_8);

            String urlString = String.format("https://translate.google.com/m?hl=en&sl=%s&tl=%s&ie=UTF-8&prev=_m&q=%s", langFrom, langTo, encodedText);

            return new URL(urlString);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private URLConnection setupConnection(URL url) throws IOException {
        URLConnection connection = url.openConnection();

        connection.setConnectTimeout(5000);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        return connection;
    }

    @SuppressWarnings("deprecation")
    private String parseHTML(String html) {
        String regex = "class=\"result-container\">([^<]*)</div>";
        Pattern pattern = Pattern.compile(regex, Pattern.MULTILINE);

        Matcher matcher = pattern.matcher(html);
        matcher.find();
        String match = matcher.group(1);

        if (match == null || match.isEmpty())
            return null;

        // deprecated in favor of org.apache.commons.text.StringEscapeUtils,
        // which isn't bundled with Minecraft
        return org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(match);
    }
}