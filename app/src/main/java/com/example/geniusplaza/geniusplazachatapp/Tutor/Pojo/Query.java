package com.example.geniusplaza.geniusplazachatapp.Tutor.Pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by geniusplaza on 7/10/17.
 */

public class Query {
    @SerializedName("queryresult")
    @Expose
    private Queryresult queryresult;

    public Queryresult getQueryresult() {
        return queryresult;
    }

    public void setQueryresult(Queryresult queryresult) {
        this.queryresult = queryresult;
    }
}
