package com.ingestion.management.model;

//import java.util.Date;
import java.util.Objects;
import java.util.UUID;


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "news")
public class News {

    @Id
    private UUID id;

    private String title;
    private String author;
    private String url;
    private String description;
    private String postDate;
    private String thumbnail;
//    private String content;

    public News() {
        setId(UUID.randomUUID());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setPostDate(String postDate) {
        this.postDate = postDate;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

//    public String getContent() { return content; }
//
//    public void setContent(String content) { this.content=content; }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        News news = (News) o;
        return url.equals(news.url) && Objects.equals(id, news.id) && Objects.equals(author, news.author)
                && Objects.equals(thumbnail, news.thumbnail) && Objects.equals(title, news.title)
                && Objects.equals(description, news.description) && Objects.equals(postDate, news.postDate);
//                && Objects.equals(content, news.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, description, author, postDate, thumbnail);//, content);
    }

    @Override
    public String toString() {
        return "News{" + "id=" + id + ", title='" + title + '\'' + ", thumbnail='" + thumbnail + '\'' + ", url='" + url
                + '\'' + ", description='" + description + '\'' + ", postDate=" + postDate + '\'' +'}'; //+ ",content =" +'}';
    }


}
