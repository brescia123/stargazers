package it.gbresciani.stargazers.network

import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNull
import org.junit.Test

class LinkHeaderParserTest {
    @Test
    @Throws(Exception::class)
    fun getNextPageUrl_shouldReturnTheRightUrl_whenLinkHeaderContainsIt() {
        // When...
        val linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"next\", <https://api.github.com/repositories/7508411/stargazers?page=682>; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\""
        // Return...
        val pageUrl = getNextPageUrl(linkHeader)
        // Assert...
        assertEquals("https://api.github.com/repositories/7508411/stargazers?page=682", pageUrl)
    }

    @Test
    @Throws(Exception::class)
    fun getNextPageUrl_shouldReturnNull_whenLinkHeaderDoesNotContainNextUrl() {
        // When...
        val linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>;;; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\""
        // Return...
        val pageUrl = getNextPageUrl(linkHeader)
        // Assert...
        assertNull(pageUrl)
    }

    @Test
    @Throws(Exception::class)
    fun getNextPageUrl_shouldReturnNull_whenLinkHeaderIsMalformed() {
        // When...
        val linkHeader = "<https://api.github.com/repositories/7508411/stargazers?page=682>;;; rel=\"last\", <https://api.github.com/repositories/7508411/stargazers?page=1>; rel=\"first\", <https://api.github.com/repositories/7508411/stargazers?page=680>; rel=\"prev\""
        // Return...
        val pageUrl = getNextPageUrl(linkHeader)
        // Assert...
        assertNull(pageUrl)
    }

    @Test
    @Throws(Exception::class)
    fun getNextPageUrl_shouldReturnNull_whenLinkHeaderIsNull() {
        // When...
        val linkHeader: String? = null
        // Return...
        val pageUrl = getNextPageUrl(linkHeader)
        // Assert...
        assertNull(pageUrl)
    }


}