package it.gbresciani.stargazers.network;

import com.jakewharton.retrofit2.adapter.rxjava2.Result;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface StargazersService {

    @GET("repos/{owner}/{repo}/stargazers")
    Observable<Result<List<Stargazer>>> getStargazers(@Path("owner") String owner,
                                                      @Path("repo") String repo);

    @GET
    Observable<Result<List<Stargazer>>> getStargazers(@Url String stargazersUrl);
}
