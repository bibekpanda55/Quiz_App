package com.example.trivia.data;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.trivia.controller.AppController;
import com.example.trivia.model.Question;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class QuestionBank {
    private String url="https://raw.githubusercontent.com/curiousily/simple-quiz/master/script/statements-data.json";
    private ArrayList<Question> arrayList =new ArrayList<>();


    public List<Question> getQuestions(final AnswerListAsyncResponse callback)
    {
        JsonArrayRequest arrayRequest = new JsonArrayRequest(Request.Method.GET, url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
               for(int i=0;i<response.length();i++)
               {
                   try {
                       Question question=new Question();
                       question.setAnswer(response.getJSONArray(i).get(0).toString());
                       question.setAnswerTrue(response.getJSONArray(i).getBoolean(1));
                        arrayList.add(question);
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               if(null!=callback)
                   callback.processfinshed(arrayList);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(arrayRequest);
        return arrayList;
    }

}
