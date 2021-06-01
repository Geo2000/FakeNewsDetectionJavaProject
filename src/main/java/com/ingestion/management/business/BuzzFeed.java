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

public class BuzzFeed {
    public ArrayList<String> urlList = new ArrayList<>();

    public JSONObject scrapPageContent(String link, String lastDate) throws IOException, ParseException {
        JSONObject newsDetails = new JSONObject();
        String newsTitle;
        String newsAuthor = "Unknown";
        String newsDate = "Unknown";
        String newsThumbnail = "Unknown";
        StringBuilder newsBody = new StringBuilder();

        Document doc = Jsoup.connect(link).userAgent("Mozilla").get();
        newsTitle = doc.title();

        if (doc.select("picture").select("img").attr("src").length() > 0) {
            newsThumbnail = doc.select("picture").select("img").attr("src");
        }

        Elements mainBlock = doc.getElementsByTag("main").attr("id", "news-content");

        for (Element e : mainBlock) {
            Elements author_elem = e.getElementsByTag("header");
            for (Element author : author_elem) {
                Elements authorLinks = author.getElementsByTag("span").attr("class",
                        "news-byline-full__name xs-block link-initial--text-black");
                if (authorLinks.size() > 0) {
                    Element a = authorLinks.get(0);
                    if (a.getElementsByTag("span").size() > 2) {
                        newsAuthor = a.getElementsByTag("span").get(1).text();
                    }
                }
            }

            for (Element date : author_elem) {
                Elements dates = date.getElementsByTag("p").attr("class", "news-article-header__timestamps-posted");
                if (dates.size() > 0) {
                    if (dates.text().contains("Posted on")) {
                        newsDate = dates.get(dates.size() - 1).text();
                    }
                }
            }

            Elements divs = e.getElementsByTag("div").attr("id", "mod-article-wrapper-1");
            for (Element elem : divs) {
                if (elem.attr("data-module").equals("subbuzz-text")) {
                    Elements paragraphs = elem.getElementsByTag("p");
                    newsBody.append(paragraphs.text());
                }
            }
        }

        return getJsonObject(link, newsDetails, newsTitle, newsAuthor, newsBody, newsDate, newsThumbnail, lastDate);
    }

    private JSONObject getJsonObject(String url, JSONObject newsDetails, String newsTitle, String newsAuthor,
                                     StringBuilder newsBody, String newsDate, String newsThumbnail, String lastDate) throws ParseException {
        if (newsBody.length() != 0 && !urlList.contains(url)) {
            newsDetails.put("title", newsTitle);
            newsDetails.put("author", newsAuthor);
            newsDetails.put("url", url);
            newsDetails.put("description", JSONValue.escape(newsBody.toString()));
//            newsDetails.put("description",JSONValue.escape(newsBody.substring(0, 150)));

            SimpleDateFormat format;
            if (newsDate.contains("a.m."))
                format = new SimpleDateFormat("'Posted on' MMM dd, yyyy, 'at' hh:mm 'a.m. ET'");
            else
                format = new SimpleDateFormat("'Posted on' MMM dd, yyyy, 'at' hh:mm 'p.m. ET'");
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
            if(convertedDate2.equals("Unknown")) {
                return null;
            }
            if (sdf.parse(lastDate).before(sdf.parse(convertedDate2)))
                return newsDetails;
            else return null;
        } else
            return null;
    }

    public String scrapMainPage(String lastDate) throws IOException, ParseException {
        JSONArray newsList = new JSONArray();
        JSONObject tempNews;

        String[] newsCategories = {"", "section/arts-entertainment", "section/books", "section/culture",
                "section/inequality", "section/jpg", "section/lgbtq", "collection/opinion", "section/politics",
                "section/reader", "section/science", "section/tech", "section/world"};
        for (String category : newsCategories) {
            Document doc = Jsoup.connect("https://www.buzzfeednews.com/" + category).userAgent("Mozilla").get();

            Elements mainBlock = (Elements) doc.getElementsByTag("main").attr("id", "news-content");

            for (Element e : mainBlock) {
                Elements articles = e.getElementsByTag("article");
                for (Element article : articles) {
                    Elements links = article.getElementsByTag("a");

                    for (Element link : links) {
                        String linkHref = link.attr("href");
                        if (linkHref.contains("https")) {
                            tempNews = scrapPageContent(linkHref, lastDate);
                            if (tempNews != null) {
                                newsList.add(tempNews);
                            }
                        }
                    }
                }
            }
        }
        return newsList.toJSONString();
    }
}
