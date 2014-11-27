Software_Development
====================
###Java, JUnit, jQuery, MySQL
____________________
1. In project 1 Inverted Index, I write a Java program that recursively processes all text files in a directory and builds an inverted index to store the mapping from words to the documents (and position within those documents) where those words were found.

2. In project 2 Partial Search, I extend my previous project to support partial search. My program returns a sorted list of results from my inverted index that start with the query word(s).

3. In project 3 Multithreading, I extend my previous project to support multithreading. This inverted index data structure is thread-safe by using a custom lock class that allows multiple read operations and exclusive write operations. The building and searching of the inverted index are multithreaded by using a work queue and thread pool.

4. In project 4 Web Crawler, I create a web crawler that takes as input a seed URL to crawl and a query file. This program crawl all links found on the seed web page and resulting pages until all links have been crawled or have reached a maximum of 50 unique links. For each webpage crawled, this program removes all HTML tags and populates an inverted index from the resulting text. Finally, the program returns partial search results for each query in the supplied query file. This one still use multithreading for building and searching index.

5. In project 5 Search Engine, I create a customizable search engine that builds off all of my previous projects. My project uses multi-threading, an inverted index, servlets, sockets, cookies, Jetty, HTTP, HTML, JDBC, and SQL.
