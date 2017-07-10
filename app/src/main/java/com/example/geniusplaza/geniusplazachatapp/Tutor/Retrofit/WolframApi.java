package com.example.geniusplaza.geniusplazachatapp.Tutor.Retrofit;

import com.example.geniusplaza.geniusplazachatapp.Tutor.Pojo.Query;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by geniusplaza on 7/10/17.
 */

public interface WolframApi {

    @GET("query?format=plaintext&output=JSON&appid=WJ3YTH-TRKLRPKWHP&")
    Observable<Query> postGetQueryResult(@retrofit2.http.Query(value = "input", encoded = true) String input);
}
