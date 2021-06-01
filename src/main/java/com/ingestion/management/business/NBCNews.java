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

public class NBCNews {
    public ArrayList<String> urlList = new ArrayList<>();

    public JSONObject scrapPageContent(String link, String lastDate) throws IOException, ParseException {
        StringBuilder newsBody = new StringBuilder();
        JSONObject newsDetails = new JSONObject();
        Document doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
        String newsTitle = doc.title();
        String newsAuthor = "Unknown";
        String newsDate = "Unknown";
        String newsThumbnail = "Unknown";

        Elements mainBlock = doc.getElementsByTag("article").attr("class", "article-body");

        if (doc.getElementsByClass("mb7").size() > 0) {
            if (doc.getElementsByClass("mb7").get(0).getElementsByAttribute("data-test").text().split("By").length > 0) {
                if (doc.getElementsByClass("mb7").get(0).getElementsByAttribute("data-test").text().contains("By")) {
                    String newsAuthorBlock = doc.getElementsByClass("mb7").get(0).getElementsByAttribute("data-test").text().split("By")[1];
                    newsAuthor = newsAuthorBlock;
                }
            }
        }

        if (doc.getElementsByClass("article-hero__main-image").size() > 0) {
            if (doc.getElementsByClass("article-hero__main-image").get(0).getElementsByTag("img").size() > 0) {
                String newsThumbnailblock = doc.getElementsByClass("article-hero__main-image").get(0).getElementsByTag("img").get(0).attr("src");
                newsThumbnail = newsThumbnailblock;
            }
        }

        String newsDateBlock = doc.getElementsByTag("time").attr("content");
        if (newsDateBlock.length() > 0) {
            newsDate = newsDateBlock;
        }

        for (Element e : mainBlock) {
            Elements contents = e.getElementsByTag("div").attr("class", "article-body__content");

            if (contents.size() > 0) {
                Elements paragraphs = contents.get(0).getElementsByTag("p");
                for (Element p : paragraphs) {
                    newsBody.append(p.text());
                }
            }

        }

        if (newsBody.length() != 0 && !urlList.contains(link)) {
            newsDetails.put("title", newsTitle);
            newsDetails.put("author", newsAuthor);
            newsDetails.put("url", link);
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
            urlList.add(link);
            if(convertedDate2.equals("Unknown")) {
                return null;
            }
            if (sdf.parse(lastDate).before(sdf.parse(convertedDate2)))
                return newsDetails;
            else return null;
        } else
            return null;
    }

    public String scrapMainPage(String lastDate) throws IOException, InterruptedException, ParseException {
        String[] newsCategories = {"", "world", "politics", "business", "health", "entertainment"};
        JSONArray newsList = new JSONArray();
        JSONObject tempNews;

        for (String category : newsCategories) {
            String url = "https://www.nbcnews.com/" + category;

            Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2228.0 Safari/537.36").get();
            Elements mainLinks = doc.select("a[href]");
            for (Element e : mainLinks) {
                String tempUrl = e.attr("href");
                if (tempUrl.matches("(.*)/(.*)[(0-9)]+")) {
                    if (!tempUrl.contains("/video/")) {
                        Thread.sleep(50);
                        tempNews = scrapPageContent(tempUrl, lastDate);
                        if (tempNews != null)
                            newsList.add(tempNews);
                    }
                }
            }
        }
        return newsList.toJSONString();
    }
}
