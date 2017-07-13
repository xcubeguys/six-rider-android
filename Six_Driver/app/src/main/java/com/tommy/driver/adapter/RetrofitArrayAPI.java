package com.tommy.driver.adapter;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by navneet on 4/6/16.
 */
public interface RetrofitArrayAPI {

    /*
     * Retrofit get annotation with our URL
     * And our method that will return us details of student.
    */
    @GET("editProfile/user_id/{userid}")
    Call<List<RiderInfo>> repoContributors(
            @Path("userid") String userid);

    @GET("home/getpercentage")
    Call<List<Contributor>> repoContributors(
            @Query("start_time") String starttime, @Query("end_time") String endtime, @Query("driver_id") String driver_id);

}
