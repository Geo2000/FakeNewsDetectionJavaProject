package com.ingestion.management.cron;

import com.google.gson.Gson;
import com.ingestion.management.business.*;
import com.ingestion.management.model.News;
import com.ingestion.management.repository.NewsRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Component
public class CronNews {

    // private static final Logger LOGGER = (Logger)
    // LoggerFactory.getLogger(CronNewsTask.class);

    private String bbcLastRunDate = "22-May-2021 00:01:00";
    private String buzzFeedLastRunDate = "22-May-2021 00:01:00";
    private String huffingtonPostLastRunDate = "22-May-2021 00:01:00";
    private String NYLastRunDate = "22-May-2021 00:01:00";
    private String NBCLastRunDate = "22-May-2021 00:01:00";
    private String dailyMailLastRunDate = "22-May-2021 00:01:00";

    private NewsRepository newsRepository;

    public CronNews(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callBBCNews() throws IOException, ParseException {
        // call BBC news
        BBCNews bbcNews = new BBCNews();
        String bbcnews = bbcNews.scrapMainPage(bbcLastRunDate);

        Gson gson = new Gson();
        News[] newsBBC = gson.fromJson(bbcnews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsBBC));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        bbcLastRunDate = formatted.format(lastRunDate);
//        System.out.println("BBCNews---:" + lastRunDate);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callHuffingtonPost() throws IOException, ParseException {
        HuffingtonPost huffingtonNews = new HuffingtonPost();
        String huffintonnews = huffingtonNews.scrapMainPage(huffingtonPostLastRunDate);

        Gson gson = new Gson();
        News[] newsHuffinton = gson.fromJson(huffintonnews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsHuffinton));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        huffingtonPostLastRunDate = formatted.format(lastRunDate);
//        System.out.println("Huffington:" + lastRunDate);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callNY() throws IOException, ParseException {
        NewYorkPost nyNews = new NewYorkPost();
        String nynews = nyNews.scrapMainPage(NYLastRunDate);

        Gson gson = new Gson();
        News[] newsNY = gson.fromJson(nynews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsNY));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        NYLastRunDate = formatted.format(lastRunDate);
//        System.out.println("NewYork---:" + lastRunDate);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callNBC() throws IOException, InterruptedException, ParseException {
        NBCNews nbcNews = new NBCNews();
        String nbcnews = nbcNews.scrapMainPage(NBCLastRunDate);

        Gson gson = new Gson();
        News[] newsNBC = gson.fromJson(nbcnews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsNBC));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        NBCLastRunDate = formatted.format(lastRunDate);
//        System.out.println("NBCNews---:" + lastRunDate);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callBuzzFeed() throws IOException, ParseException {
        BuzzFeed bfNews = new BuzzFeed();
        String bfnews = bfNews.scrapMainPage(buzzFeedLastRunDate);

        Gson gson = new Gson();
        News[] newsBF = gson.fromJson(bfnews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsBF));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        buzzFeedLastRunDate = formatted.format(lastRunDate);
//        System.out.println("BuzzFeed--:" + lastRunDate);
    }

    @Scheduled(cron = "0 */30 * * * *")
    public void callDailyMail() throws IOException, ParseException {
        DailyMail dmNews = new DailyMail();
        String dmnews = dmNews.scrapMainPage(dailyMailLastRunDate);

        Gson gson = new Gson();
        News[] newsDM = gson.fromJson(dmnews, News[].class);

        this.newsRepository.saveAll(Arrays.asList(newsDM));

        Date lastRunDate = new Date();

        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
        dailyMailLastRunDate = formatted.format(lastRunDate);
//        System.out.println("DailyMail--:" + lastRunDate);
    }
}
