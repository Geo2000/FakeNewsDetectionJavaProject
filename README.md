# Data Ingestion

Modulul care s-a ocupat de furnizarea de stiri/articole pentru proiectul Fake News Detection.

## Componente:

### - Web Scraping - Cristina-Ecaterina Tiganasu
 - modulul care s-a ocupat de popularea bazei de date cu stiri/articole din diverse surse (BBC News, BuzzFeed, DailyMail, Huffington Post, NBC News, New York Post)

### - Cron Jobs - Georgiana Murarasu
 - modulul care s-a ocupat de executarea scraper-elor la un anumit interval de timp (astfel incat sa avem in baza de date cele mai noi stiri/articole)

### - Spring Boot si REST Web Service - Georgiana Murarasu, Maria-Ecaterina Olariu, Cristina-Ecaterina Tiganasu
 -  business - Cristina-Ecaterina Tiganasu
 - controller - Maria-Ecaterina Olariu
 - cron - Georgiana Murarasu
 - model 
 - repository
 - service

### - Swagger - Maria-Ecaterina Olariu
- Pentru testarile functionalitatilor din NewsController se poate utiliza si Swagger:
http://localhost:8082/swagger-ui/index.html?configUrl=/v3/api-docs/swagger-config#/

### - Unit Tests - Georgiana Murarasu
 - s-au facut teste pentru fiecare metoda care necesita acest lucru

### - Deploy in cloud - Heroku

### - Baza de date stocata in cloud - MongoDB - Maria-Ecaterina Olariu
- Alegerea bazei de date de tip NoSQL a fost facuta deoarece trebuie preluata o cantitate mare de informatii in mod eficient si rapid
- Id-ul este de tip UUID pentru a nu permite accesul neautorizat la datele preluate, astfel respectandu-se un principiu OWASP
