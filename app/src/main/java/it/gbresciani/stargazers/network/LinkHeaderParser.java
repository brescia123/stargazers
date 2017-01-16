package it.gbresciani.stargazers.network;

import java.util.NoSuchElementException;

import io.reactivex.Observable;

/**
 * Utility parser class for the "Link" GitHub API header.
 *
 * <https://api.github.com/repositories/7508411/stargazers?page=2>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"last\"
 */
public class LinkHeaderParser {
    /**
     * Function that parses the "Link" GitHub API header and return the url of the
     * next page if it can found it, null otherwise.
     *
     * @param linkHeader the String containing the header.
     * @return the next page url or null if it was not found.
     */
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
