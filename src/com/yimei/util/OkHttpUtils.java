package com.yimei.util;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSON;
import com.aliyun.openservices.shade.com.alibaba.rocketmq.shade.com.alibaba.fastjson.JSONObject;

import okhttp3.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class OkHttpUtils {
    private static final byte[] LOCKER = new byte[0];
    private static OkHttpUtils mInstance;
    private OkHttpClient mOkHttpClient;

    private OkHttpUtils() {
        okhttp3.OkHttpClient.Builder ClientBuilder=new okhttp3.OkHttpClient.Builder();
        ClientBuilder.readTimeout(30, TimeUnit.SECONDS);//读取超时
        ClientBuilder.connectTimeout(10, TimeUnit.SECONDS);//连接超时
        ClientBuilder.writeTimeout(60, TimeUnit.SECONDS);//写入超时
        mOkHttpClient=ClientBuilder.build();
    }

    public static OkHttpUtils getInstance() {
        if (mInstance == null) {
            synchronized (LOCKER) {
                if (mInstance == null) {
                    mInstance = new OkHttpUtils();
                }
            }
        }
        return mInstance;
    }
    /**
     * 设置请求头
     * @param headersParams
     * @return
     */
    private Headers SetHeaders(Map<String, String> headersParams){
        Headers headers=null;
        okhttp3.Headers.Builder headerSbuilder=new okhttp3.Headers.Builder();

        if(headersParams != null)
        {
            Iterator<String> iterator = headersParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                headerSbuilder.add(key, headersParams.get(key));
                Log.d("get http", "get_headers==="+key+"===="+headersParams.get(key));
            }
        }
        headers=headerSbuilder.build();

        return headers;
    }


    /**
     * post请求参数
     * @param BodyParams
     * @return
     */
    private RequestBody SetRequestBody(Map<String, String> BodyParams){
        RequestBody body=null;
        okhttp3.FormBody.Builder formEncodingBuilder=new okhttp3.FormBody.Builder();
        for (String key : BodyParams.keySet()) {
        	formEncodingBuilder.add(key, BodyParams.get(key));
        	Log.d("post http", "post_Params==="+key+"===="+BodyParams.get(key));
        }
        /*if(BodyParams != null){
            Iterator<String> iterator = BodyParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                formEncodingBuilder.add(key, BodyParams.get(key));
                Log.d("post http", "post_Params==="+key+"===="+BodyParams.get(key));
            }
        }*/
        body=formEncodingBuilder.build();
        return body;

    }

    /**
     * get方法连接拼加参数
     * @param mapParams
     * @return
     */
    private String setUrlParams( Map<String, String> mapParams){
        String strParams = "";
        if(mapParams != null){
            Iterator<String> iterator = mapParams.keySet().iterator();
            String key = "";
            while (iterator.hasNext()) {
                key = iterator.next().toString();
                strParams += "&"+ key + "=" + mapParams.get(key);
            }
        }

        return strParams;
    }

    /**
     * 获取服务器返回信息
     * @param reqUrl UR连接
     * @param headersParams 请求头参数
     * @param params 请求参数
     * @param object 标签
     * @param mHandler
     * @param BPost 是否post提交,false 为get，true为Post
     */
    public void getServerExecute(String reqUrl, Map<String, String> headersParams, Map<String, String> params,
                               Object object, final Handler mHandler,final  boolean BPost,final String id){
        okhttp3.Request.Builder RequestBuilder=new okhttp3.Request.Builder();
        RequestBuilder.headers(SetHeaders(headersParams));//添加请求头
        RequestBuilder.tag(object);//添加请求标签
        Request request=null;
        if(BPost){
            RequestBuilder.url(reqUrl);//添加URL地址
            RequestBody rqbody = this.SetRequestBody(params);
            request=RequestBuilder.post(rqbody).build();
        }else {
            RequestBuilder.url(reqUrl+setUrlParams(params));//添加URL地址
            request=RequestBuilder.build();
        }
        Log.d("get http", "get_url==="+request.url());
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call arg0, IOException arg1) {
                // TODO Auto-generated method stub
                Log.d("get http", "get_onFailure==="+arg1.toString());
                Message mess = mHandler.obtainMessage();//
                mess.what = 404;
                mess.obj = "通讯错误-020";
                mHandler.sendMessage(mess);
            }

            @Override
            public void onResponse(Call arg0, Response arg1) throws IOException {
                // TODO Auto-generated method stub
                Log.d("get http", "get_code==="+arg1.code());
                String callRes = "";
                Message mess = mHandler.obtainMessage();
                if (arg1.code() == 200) {
                    callRes = arg1.body().string();
                    Log.d("get http", "get==="+callRes);
                    try {
                        JSONObject aliJson = new JSONObject();
                        // 转换返回结果信息
                        aliJson = JSON.parseObject(callRes);
                        
                        Bundle b = new Bundle();
        				b.putString("jsonObj", aliJson.toString());
        				b.putString("type", id);
        				mess.setData(b);
                    } catch (Exception e) {
                        mess.what = 404;
                        mess.obj = "数据异常-021";
                    }
                } else {
                    mess.what = arg1.code();
                    mess.obj = "通讯异常-"+arg1.code();
                }
                mHandler.sendMessage(mess);
            }
        });
    }
}
