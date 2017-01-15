package it.gbresciani.stargazers.network;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.adapter.rxjava.Result;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface StargazersService {

    @GET("users/{owner}/{repo}/stargazers")
    Observable<Result<List<Stargazer>>> getStargazers(@Path("owner") String owner,
                                                      @Path("repo") String repo);

    @GET("users/{owner}/{repo}/stargazers")
    Observable<Result<List<Stargazer>>> getStargazers(@Path("owner") String owner,
                                                      @Path("repo") String repo,
                                                      @Query("page") Integer page);

    @GET("users/{owner}/{repo}/stargazers")
    Observable<Result<List<Stargazer>>> getStargazers(@Url String stargazersUrl);
}
