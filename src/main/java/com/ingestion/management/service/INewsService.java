package com.ingestion.management.service;

import com.ingestion.management.model.News;
import java.util.List;

public interface INewsService {

    List<News> findPaginated(int pageNo, int pageSize);
}
