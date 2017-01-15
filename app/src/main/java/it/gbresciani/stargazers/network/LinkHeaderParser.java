package it.gbresciani.stargazers.network;

import java.util.NoSuchElementException;

import io.reactivex.Observable;

public class LinkHeaderParser {
    public static String getNextPageUrl(String linkHeader) {
        if (linkHeader == null) return null;
        try {
            return Observable.fromArray(linkHeader.split(","))
                    .map(s -> {
                        String[] section = s.split(";");
                        String url = section[0].replace("<", "").replace(">", "").trim();
                        String[] relations = section[1].split("=");
                        String relation = relations[1].replace("\"", "").trim();
                        return new Link(relation, url);
                    })
                    .filter(link -> "next".equals(link.rel))
                    .map(link -> link.url)
                    .blockingFirst();
        } catch (NoSuchElementException | ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    private static class Link {
        final String rel;
        final String url;
        Link(String rel, String url) {
            this.rel = rel;
            this.url = url;
        }
    }
}
