package com.ingestion.management.service;

import com.ingestion.management.model.News;
import com.ingestion.management.repository.NewsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Service
public class NewsService implements INewsService {

    @Autowired
    private NewsRepository repository;

    public NewsService(NewsRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<News> findPaginated(int pageNo, int pageSize) {

        Pageable paging = PageRequest.of(pageNo, pageSize);
        Page<News> pagedResult = repository.findAll(paging);

        return pagedResult.toList();
    }
}
