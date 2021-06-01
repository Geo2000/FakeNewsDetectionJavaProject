package com.ingestion.management.controller;

//import com.google.gson.Gson;

//import com.ingestion.management.business.BBCNewsController;

//import com.ingestion.management.business.*;
//import com.ingestion.management.business.NewsSources;
import com.ingestion.management.model.IntWrapper;
//import com.ingestion.management.business.BuzzFeedController;
//import com.ingestion.management.business.NBCNewsController;
import com.ingestion.management.model.News;
import com.ingestion.management.model.ProviderEntity;
import com.ingestion.management.repository.NewsRepository;
import com.ingestion.management.service.INewsService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
//import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
//import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin(origins = "")
@RequestMapping("v1/api/news")
public class NewsController {
    private NewsRepository newsRepository;

    public NewsController(NewsRepository newsRepository, INewsService newsService) {
        this.newsRepository = newsRepository;
        // this.newsService = newsService;
    }

    @Autowired
    private INewsService newsService;

    /* get request cu paginare */
    @GetMapping("/paging/{pageNo}/{pageSize}")
    public List<News> getPaginatedNews(@PathVariable int pageNo, @PathVariable int pageSize) {
        return newsService.findPaginated(pageNo, pageSize);
    }

    @PostMapping("")
    public ResponseEntity<News> create(@RequestBody News news) {
        if (news == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        News newNews = this.newsRepository.save(news);
        return new ResponseEntity<>(newNews, HttpStatus.CREATED);
    }

    @GetMapping("/newssources")
    public ResponseEntity<List<String>> getNewsSources() {
        List<String> newsSources = new ArrayList<>();

        newsSources.add("bbc");
        newsSources.add("buzzfeednews");
        newsSources.add("dailymail");
        newsSources.add("huffpost");
        newsSources.add("nbcnews");
        newsSources.add("nypost");

        return new ResponseEntity<>(newsSources, HttpStatus.OK);
    }

    @GetMapping("{newsSource}")
    public ResponseEntity<List<News>> getAllNews(@RequestParam String newsSource) {
        List<News> list = this.newsRepository.findAll();

        List<News> filteredList = new ArrayList<>();

        for (News news : list) {
            if (news.getUrl() != null && newsSource != null && news.getUrl().contains(newsSource.toLowerCase())) {
                filteredList.add(news);
            }
        }

        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<List<News>> getAll() {
        List<News> list = this.newsRepository.findAll();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @GetMapping("{id}")
    public ResponseEntity<News> getById(@PathVariable UUID id) {
        Optional<News> news = this.newsRepository.findById(id);

        return news.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    // requested by backend
    @GetMapping("/getInterval")
    public List<News> getInterval(@RequestParam(name = "skip", required = true) Integer skip,
            @RequestParam(name = "count", required = true) Integer count) {
        List<News> list = this.newsRepository.findAll();

        List<News> filteredList = new ArrayList<>();

        int end = skip + count;
        if (end > list.size())
            end = list.size();
        for (int i = skip; i < end; i++) {
            filteredList.add(list.get(i));
        }

        return filteredList;
    }

    @GetMapping("/getIntervalByProvider")
    public List<News> getIntervalByProvider(@RequestParam(name = "provider_id", required = true) Integer provider_id,
            @RequestParam(name = "skip", required = true) Integer skip,
            @RequestParam(name = "count", required = true) Integer count) {

        List<News> list = this.newsRepository.findAll();

        list.sort((n1, n2) -> {
            DateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            try {
                Date date1 = format.parse(n1.getPostDate());
                Date date2 = format.parse(n2.getPostDate());

                return date2.compareTo(date1);
            } catch (Exception e) {
                return 0;
            }
        });

        List<News> filteredList = new ArrayList<>();
        String newsSource = "";
        if (provider_id == 0) {
            newsSource = "bbc";
        } else if (provider_id == 1) {
            newsSource = "buzzfeednews";
        } else if (provider_id == 2) {
            newsSource = "dailymail";
        } else if (provider_id == 3) {
            newsSource = "huffpost";
        } else if (provider_id == 4) {
            newsSource = "nbcnews";
        } else if (provider_id == 5) {
            newsSource = "nypost";
        }

        int i = 0;

        for (News news : list) {

            if (news.getUrl() != null && newsSource != null && news.getUrl().contains(newsSource.toLowerCase())) {
                if (i == (count + skip)) // numerotarea incepe de la 0..
                {
                    break;
                }
                if (i >= skip) {
                    filteredList.add(news);
                }
                i++;
            }
        }

        return filteredList;
    }

    // * Public method for requesting all providers from the database
    // * @return a List with every ProviderEntity from the database
    // */
    @GetMapping(path = "/getAll")
    public List<ProviderEntity> getProviders() {
        // return getNewsSources().getBody();
        List<String> providerStringList = getNewsSources().getBody();

        assert providerStringList != null;
        List<ProviderEntity> providerCorrectList = new ArrayList<>(providerStringList.size());
        for (int i = 0; i < providerStringList.size(); ++i) {
            providerCorrectList.add(i, new ProviderEntity(i, providerStringList.get(i), 0, "no-avatar"));
        }
        return providerCorrectList;
    }

    // /**
    // * Public method for requesting a specific number of providers from the
    // database
    // * @param s used for skipping a number of rows from the database
    // * @param c how many rows are necessary
    // * @return a List with the ProviderEntity requested
    // */
    @GetMapping(path = "providers/getInterval")
    public List<ProviderEntity> getInterval(@RequestParam(name = "skip", required = true) int skip,
            @RequestParam(name = "count", required = true) int count) {
        List<ProviderEntity> newsProvider = new ArrayList<>();
        List<String> list = getNewsSources().getBody();
        if (list == null)
            return null;
        int i = skip;

        while (i < count + skip) {
            if (i >= list.size())
                break;
            String name = list.get(i);
            newsProvider.add(new ProviderEntity(i, name, 0, "no-avatar"));
            i++;
        }

        return newsProvider;
    }

    // /**
    // * Public method for requesting the total number of providers enlisted in the
    // database
    // * @return the number of providers as a IntWrapper class
    // */

    @GetMapping(path = "/getCount")
    public IntWrapper getCount() {
        int i = getNewsSources().getBody().size();
        IntWrapper iNew = new IntWrapper(i);
        return iNew;
    }

    // /**
    // * Public method for requesting the number of providers containing the
    // provided string
    // * @param query used for providing the string to be searched within the names
    // of the providers
    // * @return an IntWrapper containing the number of providers
    // */
    @GetMapping(path = "/searchCount")
    public IntWrapper searchCount(@RequestParam(name = "query", required = true) String query) {

        // Integer number = 0;
        // List<String> list = getNewsSources().getBody();
        // for (String i : list) {
        // if (query == i)
        // number++;

        // }
        // IntWrapper iNew = new IntWrapper(number);
        // return iNew;
        List<String> list = getNewsSources().getBody();
        if (list == null)
            return new IntWrapper(0);

        int count = 0;
        for (String i : list) {
            if (i.contains(query))
                count++;
        }
        return new IntWrapper(count);
    }

    // /**
    // * Public method for requesting a list of providers containing the provided
    // string
    // * @param query used for providing the string to be searched within the names
    // of the providers
    // * @param s used for skipping a number of rows from the database
    // * @param c how many rows are necessary
    // * @return a List with the ProviderEntity requested
    // */

    @GetMapping(path = "/search")
    public List<ProviderEntity> search(@RequestParam(name = "query", required = true) String query,
                                       @RequestParam(name = "skip", required = true) int skip,
                                       @RequestParam(name = "count", required = true) int count) {

        List<ProviderEntity> filteredList = new ArrayList<ProviderEntity>();

        Integer number = 0;
        List<String> list = getNewsSources().getBody();

        int i = skip;


        while (i < count + skip) {
            if (i >= list.size())
                break;
            String name = list.get(i);
            filteredList.add(new ProviderEntity(i, name, 0, "no-avatar"));
            i++;
        }

        return filteredList;
    }

    // @GetMapping(path = "/getIntervalByArray")
    // public List<News> getIntervalByArray(@RequestParam(name = "list", required =
    // true) List<Integer> providerIds,
    // @RequestParam(name = "skip", required = true) Integer skip,
    // @RequestParam(name = "count", required = true) Integer count) {
    // List<News> list = this.newsRepository.findAll();

    // List<News> filteredList = new ArrayList<>();
    // String newsSources[] = new String[6];
    // int k = 0;

    // for (int j : providerIds) {
    // if (j == 0) {
    // newsSources[k] = "bbc";
    // } else if (j == 1) {
    // newsSources[k] = "buzzfeednews";
    // } else if (j == 2) {
    // newsSources[k] = "dailymail";
    // } else if (j == 3) {
    // newsSources[k] = "huffpost";
    // } else if (j == 4) {
    // newsSources[k] = "nbcnews";
    // } else if (j == 5) {
    // newsSources[k] = "nypost";
    // }
    // k++;
    // }

    // int end = skip + count;
    // int i = 0;

    // if (end > list.size())
    // end = list.size();
    // for (News news : list) {
    // k = 0;
    // while (k < providerIds.size()) {
    // if (news.getUrl() != null && news.getUrl().contains(newsSources[k])) {
    // if (i == (count + skip)) // numerotarea incepe de la 0..
    // {
    // break;
    // }
    // if (i >= skip) {
    // filteredList.add(news);
    // }
    // i++;
    // }
    // k++;
    // }
    // }

    // return filteredList;
    // }

    @GetMapping(path = "/getIntervalByArray")
    public List<News> getIntervalByArray(@RequestParam(name = "list", required = true) List<Integer> providerIds,
            @RequestParam(name = "skip", required = true) Integer skip,
            @RequestParam(name = "count", required = true) Integer count,
            @RequestParam(name = "order", required = false) String order,
            @RequestParam(name = "query", required = false) String query,
            @RequestParam(name = "date", required = false) String date) {

        List<News> list = this.newsRepository.findAll();

        if (order != null && order.equals("desc")) {
            list.sort((n1, n2) -> {
                DateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                try {
                    Date date1 = format.parse(n1.getPostDate());
                    Date date2 = format.parse(n2.getPostDate());

                    return date2.compareTo(date1);
                } catch (Exception e) {
                    return 0;
                }
            });
        } else {
            list.sort((n1, n2) -> {
                DateFormat format = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
                try {
                    Date date1 = format.parse(n1.getPostDate());
                    Date date2 = format.parse(n2.getPostDate());

                    return date1.compareTo(date2);
                } catch (Exception e) {
                    return 0;
                }
            });
        }

        List<News> filteredList = new ArrayList<>();
        String newsSources[] = new String[6];
        int k = 0;

        for (int j : providerIds) {
            if (j == 0) {
                newsSources[k] = "bbc";
            } else if (j == 1) {
                newsSources[k] = "buzzfeednews";
            } else if (j == 2) {
                newsSources[k] = "dailymail";
            } else if (j == 3) {
                newsSources[k] = "huffpost";
            } else if (j == 4) {
                newsSources[k] = "nbcnews";
            } else if (j == 5) {
                newsSources[k] = "nypost";
            }
            k++;
        }

        int end = skip + count;
        int i = 0;

        if (end > list.size()) {
            end = list.size();
        }
        for (News news : list) {
            k = 0;
            while (k < providerIds.size()) {
                if (news.getUrl() != null && news.getUrl().contains(newsSources[k])) {
                    if (i == end) {
                        return filteredList;
                    }

                    if (query != null && news.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        if (checkDate(date, news)) {
                            if (i >= skip)
                                filteredList.add(news);
                            i++;
                        }
                    } else if (query == null) {
                        if (checkDate(date, news)) {
                            if (i >= skip)
                                filteredList.add(news);
                            i++;
                        }
                    }
                }
                k++;
            }
        }
        return filteredList;
    }

    private boolean checkDate(String date, News news) {
        if (date != null) {
            switch (date) {
                case "day":
                    try {
                        Date postDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(news.getPostDate());

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date(System.currentTimeMillis()));
                        c.add(Calendar.DATE, -1); // number of days to add
                        Date dateNow = c.getTime();

                        if (!postDate.before(dateNow)) {
                            return true;
                        }

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case "week":
                    try {
                        Date postDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(news.getPostDate());

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date(System.currentTimeMillis()));
                        c.add(Calendar.DATE, -7); // number of days to add
                        Date dateNow = c.getTime();

                        if (!postDate.before(dateNow)) {
                            return true;
                        }

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;

                case "month":
                    try {
                        Date postDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(news.getPostDate());

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date(System.currentTimeMillis()));
                        c.add(Calendar.DATE, -30); // number of days to add
                        Date dateNow = c.getTime();

                        if (!postDate.before(dateNow)) {
                            return true;
                        }

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    break;
                case "year":
                    try {
                        Date postDate = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(news.getPostDate());

                        Calendar c = Calendar.getInstance();
                        c.setTime(new Date(System.currentTimeMillis()));
                        c.add(Calendar.DATE, -365); // number of days to add
                        Date dateNow = c.getTime();

                        if (!postDate.before(dateNow)) {
                            return true;
                        }

                    } catch (ParseException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            return true;
        }
        return false;
    }

}
