package it.gbresciani.stargazers.network

import java.util.*

/**
 * Function that parses the "Link" GitHub API header and return the url of the
 * next page if it can found it, null otherwise.
 * <https:></https:>//api.github.com/repositories/7508411/stargazers?page=2>; rel=\"next\", <https:></https:>//api.github.com/repositories/7508411/stargazers?page=682>; rel=\"last\"
 * @param linkHeader the String containing the header.
 * *
 * @return the next page url or null if it was not found.
 */
fun getNextPageUrl(linkHeader: String?): String? {
    data class Link(val rel: String, val url: String)

    if (linkHeader == null) return null
    try {
        return linkHeader.split(",".toRegex()).dropLastWhile(String::isEmpty)
                .map { s ->
                    val section = s.split(";").dropLastWhile(String::isEmpty)
                    val url = section[0].replace("<", "").replace(">", "").trim({ it <= ' ' })
                    val relations = section[1].split("=".toRegex()).dropLastWhile(String::isEmpty)
                    val relation = relations[1].replace("\"", "").trim({ it <= ' ' })
                    Link(relation, url)
                }
                .first { link -> "next" == link.rel }
                .url
    } catch (e: NoSuchElementException) {
        return null
    } catch (e: IndexOutOfBoundsException) {
        return null
    }

}


