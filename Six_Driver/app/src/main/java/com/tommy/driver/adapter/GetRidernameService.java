package com.tommy.driver.adapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by test on 28/3/17.
 */

public interface GetRidernameService {
    //@GET("tommy/{owner}/{repo}/contributors")
    @GET("tommy/rider/editProfile/user_id/{userid}")
    Call<List<Contributor>> repoContributors(
            @Path("userid") String userid);


    public static final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("http://demo.cogzideltemplates.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();
}
