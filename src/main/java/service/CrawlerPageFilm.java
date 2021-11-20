package service;

import entity.info_film.InfoFilm;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class CrawlerPageFilm {

    private HashSet<String> links;

    public CrawlerPageFilm() {
        links = new HashSet<>();
    }

    public List<InfoFilm> getInfoMovie(String URL) {
        List<InfoFilm> listInfoFilm = new ArrayList<>();
        if (!links.contains(URL)) {
            try {
                Document document = Jsoup.connect(URL).get();

                InfoFilm infoFilm = new InfoFilm();
                Element listFilms = document.getElementsByClass("item-list-wrapper w-dyn-list").first();
                if (listFilms != null) {

                    Element listFilm = listFilms.getElementsByClass("item-list w-dyn-items w-row").first();

                    if (listFilm != null) {
                        Elements films = listFilm.getElementsByClass("item mobile-half w-dyn-item w-col w-col-3");

                        if (films != null && films.size() >= 1) {
                            for (Element film : films) {
                                String titleFilm = film.getElementsByClass("item-block-title").first().text();

                                System.out.println(titleFilm);
//                                String categoryFilm = film.getElementsByClass("info-title-link w-inline-block").first().text();
//                                String score = film.getElementsByClass("item-number").first().text();
//
//                                String linkPoster = film.getElementsByClass("item-block").first().getElementsByTag("a").first().absUrl("style");
//                                String image = linkPoster.replace("background-image:url(\"", "https:").replace("\")", "");
//
//                                Element links = film.getElementsByClass("item-block").first();
//                                String linkFilm = links.getElementsByTag("a").first().absUrl("href");
//
//                                crawlerFilmSingle(linkFilm, infoFilm);
//
//                                infoFilm.setTitle(titleFilm);
//                                infoFilm.setImage(image);
//                                infoFilm.setScore(score);
//                                infoFilm.setCategory(categoryFilm);
//
//                                listInfoFilm.add(infoFilm);

                            }
                        }
                    }
                }
                return listInfoFilm;
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
        return listInfoFilm;
    }

    public void crawlerFilmSingle(String urlFilm, InfoFilm infoFilm) {

        if (!links.contains(urlFilm)) {
            try {
                Document document = Jsoup.connect(urlFilm).get();
                Element film = document.getElementsByClass("header-content-block").first();
                if (film != null) {
                    Element infoDetailFilm = film.getElementsByClass("header-short-description w-richtext").first();
                    if (infoDetailFilm != null) {
                        String infoFilmSingle = film.getElementsByTag("p").first().html();
                        String[] ele = infoFilmSingle.split("<br>");
                        for (String el : ele) {
                            String[] e = el.split(": ");
                            switch (e[0]) {
                                case "Th?i gian":
                                    String duration = e[e.length - 1];
                                    infoFilm.setDuration(duration);
                                    break;
                                case "??o di?n":
                                    String director = e[e.length - 1];
                                    infoFilm.setDirector(director);
                                    break;
                                case "Qu?c gia":
                                    String country = e[e.length - 1];
                                    infoFilm.setCountry(country);
                                    break;
                                case "Ph�t h�nh":
                                    String openDay = e[e.length - 1];
                                    infoFilm.setOpenDay(openDay);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

                Element classContent = document.getElementsByClass("rtb w-richtext").first();
                if (classContent != null) {
                    Elements getcontent = classContent.getElementsByTag("p");
                    StringBuilder content = new StringBuilder();
                    for (Element element : getcontent) {
                        content.append(element.text());
                    }

                    infoFilm.setContent(content.toString());
                }
            } catch (IOException e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        CrawlerPageFilm crawler = new CrawlerPageFilm();
        crawler.getInfoMovie("https://www.ssphim.net/the-loai/phim-chieu-rap");
    }

}
