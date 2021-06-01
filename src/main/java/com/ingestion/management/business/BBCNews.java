package com.ingestion.management.business;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BBCNews {
    public ArrayList<String> urlList = new ArrayList<>();

    public JSONObject scrapPageContent(String link, String lastDate) throws IOException, ParseException {

        JSONObject newsDetails = new JSONObject();
        String url = "https://www.bbc.com" + link;
        String newsTitle;
        String newsAuthor = "Unknown";
        String newsDate = "Unknown";
        String newsThumbnail = "Unknown";
        StringBuilder newsBody = new StringBuilder();

        Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
        newsTitle = doc.title();

        Elements content = doc.getElementsByTag("article");

        for (Element divs : content) {
            Elements author = divs.getElementsByTag("strong");
            if (!author.isEmpty()) {
                newsAuthor = author.text();
            }

            newsDate = divs.select("time").attr("datetime");

            Elements div = divs.getElementsByTag("div");
            for (Element elem : div) {

                if (elem.attr("data-component").equals("image-block")) {
                    if (newsThumbnail.equals("Unknown")) {
                        newsThumbnail = elem.select("img").attr("src");
                    }
                }

                if (elem.attr("data-component").equals("crosshead-block")) {
                    Elements paragraphs = elem.getElementsByTag("h2");
                    newsBody.append(paragraphs.text());
                }

                if (elem.attr("data-component").equals("text-block")) {
                    Elements paragraphs = elem.getElementsByTag("p");
                    newsBody.append(paragraphs.text());
                }
            }
        }
        return getJsonObject(url, newsDetails, newsTitle, newsAuthor, newsBody, newsDate, newsThumbnail, lastDate);
    }

    private JSONObject getJsonObject(String url, JSONObject newsDetails, String newsTitle, String newsAuthor,
                                     StringBuilder newsBody, String newsDate, String newsThumbnail, String lastDate) throws ParseException {
        if (newsBody.length() != 0 && !urlList.contains(url)) {
            newsDetails.put("title", newsTitle);
            newsDetails.put("author", newsAuthor);
            newsDetails.put("url", url);
            newsDetails.put("description", JSONValue.escape(newsBody.toString()));
//            newsDetails.put("description",JSONValue.escape(newsBody.substring(0, 150)));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            Date convertedDate;
            String convertedDate2;
            try {
                convertedDate = format.parse(newsDate);
                convertedDate2 = formatted.format(convertedDate);
            } catch (ParseException e) {
                convertedDate2 = "Unknown";
            }

            newsDetails.put("postDate", convertedDate2);
            newsDetails.put("thumbnail", newsThumbnail);

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            urlList.add(url);
            if (sdf.parse(lastDate).before(sdf.parse(convertedDate2)))
                return newsDetails;
            else return null;
        } else
            return null;
    }

    public String scrapMainPage(String lastDate) throws IOException, ParseException {
        JSONArray newsList = new JSONArray();
        JSONObject tempNews;

        String[] newsCategories = {"", "coronavirus", "world", "uk", "business", "technology",
                "science_and_environment", "entertainment_and_arts"};

        for (String category : newsCategories) {

            Document doc = Jsoup.connect("https://www.bbc.com/news/" + category).userAgent("Mozilla").get();

            Element content = doc.getElementById("orb-modules");
            Elements links = content.getElementsByTag("a");

            for (Element link : links) {
                String linkHref = link.attr("href");

                if (linkHref.matches("(.*)/(.*)[(0-9)]+")) {
                    if (!linkHref.contains("http")) {
                        tempNews = scrapPageContent(linkHref, lastDate);
                        if (tempNews != null)
                            newsList.add(tempNews);
                    }
                }
            }
        }
        return newsList.toJSONString();
    }
}