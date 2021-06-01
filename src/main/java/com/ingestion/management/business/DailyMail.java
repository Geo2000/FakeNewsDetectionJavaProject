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

public class DailyMail {
    public ArrayList<String> urlList = new ArrayList<>();

    public JSONObject scrapPageContent(String link, String lastDate) throws IOException, ParseException {
        StringBuilder newsBody = new StringBuilder();
        JSONObject newsDetails = new JSONObject();
        Document doc = Jsoup.connect("https://www.dailymail.co.uk" + link).userAgent("Mozilla").get();
        String newsTitle = doc.title();
        String newsAuthor = "Unknown";
        String newsDate = "Unknown";
        String newsThumbnail = "Unknown";
        String newsUrl = "https://www.dailymail.co.uk" + link;

        Elements newsDateDiv = doc.getElementsByClass("article-timestamp article-timestamp-published");
        if (newsDateDiv.size() > 0) {
            String date = newsDateDiv.get(0).getElementsByTag("time").text();
            newsDate = date;
        }

        Elements newsAuthorDiv = doc.getElementsByClass("author-section byline-plain");
        if (newsAuthorDiv.size() > 0) {
            String authName = newsAuthorDiv.get(0).getElementsByTag("a").text();
            newsAuthor = authName;
        }

        Elements newsThumbnailImg = doc.getElementsByClass("mol-img");
        if (newsThumbnailImg.size() > 0) {
            if (newsThumbnailImg.get(0).getElementsByTag("img").size() > 0) {
                String thumbnail = newsThumbnailImg.get(0).getElementsByTag("img").get(0)
                        .getElementsByAttribute("data-src").attr("data-src");
                newsThumbnail = thumbnail;
            }
        }

        String newsArticleBody = doc.getElementsByClass("mol-para-with-font").text();
        if (newsArticleBody.length() > 0) {
            newsBody.append(newsArticleBody);
        }

        if (newsBody.length() != 0) {
            newsDetails.put("title", newsTitle);
            newsDetails.put("author", newsAuthor);
            newsDetails.put("url", newsUrl);
            newsDetails.put("description", JSONValue.escape(newsBody.toString()));
//            newsDetails.put("description",JSONValue.escape(newsBody.substring(0, 150)));

            SimpleDateFormat format = new SimpleDateFormat("hh:mm 'BST', dd MMM yyyy");
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

            if(convertedDate2.equals("Unknown")) {
                return null;
            }
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            if (sdf.parse(lastDate).before(sdf.parse(convertedDate2)))
                return newsDetails;
            else return null;
        } else
            return null;
    }

    public String scrapMainPage(String lastDate) throws IOException, ParseException {
        JSONArray newsList = new JSONArray();
        JSONObject tempNews;
        String[] newsCategories = {"home/index.html", "news/index.html", "ushome/index.html", "sport/index.html",
                "tvshowbiz/index.html", "health/index.html", "sciencetech/index.html", "money/index.html"};

        for (String category : newsCategories) {
            Document doc = Jsoup.connect("https://www.dailymail.co.uk/" + category).userAgent("Mozilla").get();

            Elements links = doc.select("a");
            for (Element link : links) {
                String newsURL = link.attr("href");
                if (newsURL.matches("(\\/.*)(\\/article-)([(0-9)]+)(\\/.*\\.html)")) {
                    if (!urlList.contains(newsURL)) {
                        tempNews = scrapPageContent(newsURL, lastDate);
                        if (tempNews != null) {
                            newsList.add(tempNews);
                        }
                        urlList.add(newsURL);
                    }
                }
            }
        }
        return newsList.toJSONString();
    }
}
