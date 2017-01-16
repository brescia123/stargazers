
package it.gbresciani.stargazers.network;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Data class representing a Stargazer.
 */
public class Stargazer {

    @Expose @SerializedName("login") private final String login;
    @Expose @SerializedName("id") private final Integer id;
    @Expose @SerializedName("avatar_url") private final String avatarUrl;
    @Expose @SerializedName("html_url") private final String htmlUrl;

    public Stargazer(String login, Integer id, String avatarUrl, String htmlUrl) {
        this.login = login;
        this.id = id;
        this.avatarUrl = avatarUrl;
        this.htmlUrl = htmlUrl;
    }

    public String getLogin() {return login;}
    public Integer getId() {return id;}
    public String getAvatarUrl() {return avatarUrl;}
    public String getHtmlUrl() {return htmlUrl;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stargazer stargazer = (Stargazer) o;

        if (login != null ? !login.equals(stargazer.login) : stargazer.login != null) return false;
        if (id != null ? !id.equals(stargazer.id) : stargazer.id != null) return false;
        if (avatarUrl != null ? !avatarUrl.equals(stargazer.avatarUrl) : stargazer.avatarUrl != null)
            return false;
        return htmlUrl != null ? htmlUrl.equals(stargazer.htmlUrl) : stargazer.htmlUrl == null;

    }

    @Override
    public int hashCode() {
        int result = login != null ? login.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (avatarUrl != null ? avatarUrl.hashCode() : 0);
        result = 31 * result + (htmlUrl != null ? htmlUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Stargazer{" +
                "login='" + login + '\'' +
                ", id=" + id +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", htmlUrl='" + htmlUrl + '\'' +
                '}';
    }
}

