package it.gbresciani.stargazers.network;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LinkHeaderParserTest {
    @Test
    public void getNextPageUrl_shouldReturnTheRightUrl_whenLinkHeaderContainsIt() throws Exception {
        // When...
        String linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\"";
        // Return...
        String pageUrl = LinkHeaderParser.getNextPageUrl(linkHeader);
        // Assert...
        assertEquals("https://api.github.com/repositories/7508411/stargazers?page=682", pageUrl);
    }

    @Test
    public void getNextPageUrl_shouldReturnNull_whenLinkHeaderDoesNotContainNextUrl() throws Exception {
        // When...
        String linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>;;; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\"";
        // Return...
        String pageUrl = LinkHeaderParser.getNextPageUrl(linkHeader);
        // Assert...
        assertNull(pageUrl);
    }

    @Test
    public void getNextPageUrl_shouldReturnNull_whenLinkHeaderIsMalformed() throws Exception {
        // When...
        String linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>;;; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\"";
        // Return...
        String pageUrl = LinkHeaderParser.getNextPageUrl(linkHeader);
        // Assert...
        assertNull(pageUrl);
    }

    @Test
    public void getNextPageUrl_shouldReturnNull_whenLinkHeaderIsNull() throws Exception {
        // When...
        String linkHeader = null;
        // Return...
        String pageUrl = LinkHeaderParser.getNextPageUrl(linkHeader);
        // Assert...
        assertNull(pageUrl);
    }


}