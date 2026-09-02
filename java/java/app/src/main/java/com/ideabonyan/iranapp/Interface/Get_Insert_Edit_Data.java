package com.ideabonyan.iranapp.Interface;

import com.android.volley.VolleyError;

/**
 * Created by Novin Pendar on 03/23/2017.
 */

public interface Get_Insert_Edit_Data {

    public void on_volley_response(String response,int id);
    public void on_volley_error(VolleyError error, int id);


}