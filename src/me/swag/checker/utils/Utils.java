package me.swag.checker.utils;

import me.swag.checker.controllers.Controller;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Utils {

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");

    public static String getDate()
    {
        return simpleDateFormat.format(new Date());
    }


    public static String getDiscordStatus() {
        try {
            URL url = new URL("https://srhpyqt94yxb.statuspage.io/api/v2/summary.json");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) discord/0.0.306 Chrome/78.0.3904.130 Electron/7.1.11 Safari/537.36");
            connection.setRequestProperty("Content-Type", "application/json");
            if (connection.getResponseCode() >= 200) {
                InputStream inputStream = connection.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                connection.disconnect();
                if (sb.toString().substring(0, 301).contains("operational")) {
                    return "Operational";
                } else {
                    return "API Down";
                }
            } else {
                System.out.println(connection.getResponseMessage());
                connection.disconnect();
            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            return "Malformed URL";
        } catch (IOException ex) {
            ex.printStackTrace();
            return "IOException";
        }
        return null;
    }
    public static void saveTextToFile(String content, File file) {
        try {
            PrintWriter writer;
            writer = new PrintWriter(file);
            writer.println(content);
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
