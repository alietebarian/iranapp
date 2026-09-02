package com.ideabonyan.iranapp.Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SIM on 9/20/2017.
 */

public class BookSearchModel {

    String bookName;
    String writerName;
    String link;

    static public List<BookSearchModel> Import(JSONArray jsonArray) {
        List<BookSearchModel> datas = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                BookSearchModel data = new BookSearchModel();
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                data.bookName = jsonObject.getString("title");
                data.writerName = jsonObject.getString("writer");
                data.link = jsonObject.getString("link");

                datas.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return datas;
    }


    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getWriterName() {
        return writerName;
    }

    public void setWriterName(String writerName) {
        this.writerName = writerName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
