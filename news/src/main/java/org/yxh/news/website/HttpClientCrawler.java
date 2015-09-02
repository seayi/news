package org.yxh.news.website;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.yxh.news.domain.Site;

public class HttpClientCrawler implements ICrawler {
	
	private static final Logger logger = Logger.getLogger(HttpClientCrawler.class);

	@Override
	public String crawlerContent(final Site site) {
		CloseableHttpClient client = HttpClients.custom().build();
		try{
			HttpGet get = new HttpGet(site.getUrl());
			RequestConfig rc = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(5000).build();
			get.setConfig(rc);
			String body = client.execute(get,new ResponseHandler<String>() {

				@Override
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {
					int status = response.getStatusLine()
							.getStatusCode();
					if (status >= 200 && status < 300) {
						HttpEntity entity = response.getEntity();
						String content = entity != null ? EntityUtils
								.toString(entity,
										site.getEncoding())
								: null;
						return content;
					} else {
						logger.error("Unexpected response status: "
								+ status);
						return null;
					}
				}
			});
			return body;
		}catch(Exception e){
			logger.error(e.getMessage(),e);
			return null;
		}finally{
			try {
				client.close();
			} catch (IOException e) {
				logger.error(e.getMessage(),e);
			}
		}
	}

}
