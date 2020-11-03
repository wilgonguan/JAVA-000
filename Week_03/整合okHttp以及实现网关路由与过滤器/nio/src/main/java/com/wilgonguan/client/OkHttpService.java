package com.wilgonguan.client;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
/**
 * http访问服务
 * @author wilgonguan
 *
 */
public class OkHttpService {

	public static final OkHttpClient client = new OkHttpClient();// http 客户端
	public static final MediaType JSON = MediaType
			.parse("application/json; charset=utf-8");

	/**
	 * 同步发送GET请求，访问指定地址
	 * 
	 * @param url
	 * @return 返回请求地址的内容
	 */
	public String getRequest(String url) {
		Request request = new Request.Builder().url(url).build();
		Response response;
		try {
			response = client.newCall(request).execute();
			if (response.isSuccessful()) {
				return response.body().string();
			} else {
				throw new IOException("Unexpected code " + response);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 异步发送GET请求
	 * @param url
	 * @throws Exception
	 */
	public void asyncGetRequest(String url) {
		Request request = new Request.Builder().url(url).build();
		client.newCall(request).enqueue(new Callback() {
			@Override
			public void onFailure(Call call, IOException e) {
				e.printStackTrace();
				System.exit(0);//退出系统
			}

			@Override
			public void onResponse(Call call, Response response)
					throws IOException {
				if (response.isSuccessful()) {
					System.out.println(response.body().string());
					System.exit(0);//退出系统
					
				} else {
					throw new IOException("Unexpected code " + response);
				}
			}
		});
		System.out.println("异步发送GET请求");
	}

	/**
	 * 发送POST请求，提交JSON数据
	 * 
	 * @param url
	 * @param json
	 * @return
	 * @throws IOException
	 */
	public String post(String url, String json) throws IOException {
		RequestBody body = RequestBody.create(JSON, json);
		Request request = new Request.Builder().url(url).post(body).build();
		Response response = client.newCall(request).execute();
		if (response.isSuccessful()) {
			return response.body().string();
		} else {
			throw new IOException("Unexpected code " + response);
		}
	}

}
