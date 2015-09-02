package org.yxh.news.weibo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.yxh.news.util.NewsUtil;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

public class WeiboCrawler implements IWeiboCrawler {
	private BasicCookieStore bcs;
	private CloseableHttpClient httpclient;
	private Random r = new Random();
	private Scanner scan = new Scanner(System.in);
	private final Pattern p = Pattern.compile("\\((.*?)\\)");
	private final Pattern p0 = Pattern.compile("location.replace\\((.*?)\\)");
	private final DateFormat format = new SimpleDateFormat("yy_MM_dd_HH_mm_ss");
	public WeiboCrawler(String user, String passwd) throws Exception {
		bcs = new BasicCookieStore();
		httpclient = HttpClients.custom().setDefaultCookieStore(bcs).build();
		String url = "http://login.sina.com.cn/sso/prelogin.php?entry=weibo&callback=sinaSSOController.preloginCallBack&su="
				+ encodeUserName(user)
				+ "&rsakt=mod&checkpin=1&client=ssologin.js(v1.4.11)&_="
				+ System.currentTimeMillis();
		HttpGet get = new HttpGet(url);
		ResponseHandler<String> responseHandler = new StringResponseHandler();
		String responstr = httpclient.execute(get, responseHandler);
		String servertime = getCode(responstr, "servertime");
		String nonce = getCode(responstr, "nonce");
		String sin_pk = getCode(responstr, "pubkey");
		String rsakv = getCode(responstr, "rsakv");
		String pwdString = servertime + "\t" + nonce + "\n" + passwd;
		String sp = NewsUtil.rsaCrypt(sin_pk, "10001", pwdString);
		String postURL = "http://login.sina.com.cn/sso/login.php?client=ssologin.js(v1.4.11)";
		// 登录POST参数
		Map<String, String> partam = new HashMap<String, String>();
		partam.put("entry", "weibo");
		partam.put("gateway", "1");
		partam.put("savestate", "7");
		partam.put("from", "");
		partam.put("useticket", "1");
		partam.put("pagerefer", "http://weibo.com/a/download");
		partam.put("vsnf", "1");
		partam.put("su", encodeUserName(user));
		partam.put("service", "miniblog");
		partam.put("servertime", servertime);
		partam.put("nonce", nonce);
		partam.put("pwencode", "rsa2");
		partam.put("rsakv", rsakv);
		partam.put("sp", sp);
		partam.put("encoding", "UTF-8");
		// partam.put("prelt", "115");
		partam.put(
				"url",
				"http://weibo.com/ajaxlogin.php?framelogin=1&callback=parent.sinaSSOController.feedBackUrlCallBack");
		partam.put("returntype", "META");
		partam.put("ssosimplelogin", "1");
		List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		for (Entry<String, String> entry : partam.entrySet()) {
			formparams.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}
		HttpPost post = new HttpPost(postURL);
		post.setEntity(new UrlEncodedFormEntity(formparams));
		String s = httpclient.execute(post, responseHandler);
		int checkResult = checkDoor(getNexrURL(s));
		if (checkResult == 2) {
			boolean isRun = true;
			int num = 3;
			while (isRun) {
				String verifyCode = null;
				// 当需要输入验证码
				String check = "http://login.sina.com.cn/cgi/pin.php?r="
						+ r.nextLong() + "&s=0&p=" + getCode(responstr, "pcid");
				File file = getMarkFile(check);
				if (file != null) {
					if (num % 2 == 0) {
						String path = file.getAbsolutePath();
						System.out.println("用户名:" + user);
						System.out.print("登录需要验证码，文件路径: " + path + ",请输入验证码: ");
						NewsUtil.openFile(file);
						verifyCode = scan.nextLine();
					}

				}
				String pcid = getCode(responstr, "pcid");
				partam.put("pcid", pcid);
				partam.put("sp", NewsUtil.rsaCrypt(sin_pk, "10001",
						pwdString));
				partam.put("pagerefer", "");
				partam.put("door", verifyCode);
				formparams = new ArrayList<NameValuePair>();
				for (Entry<String, String> entry : partam.entrySet()) {
					formparams.add(new BasicNameValuePair(entry.getKey(), entry
							.getValue()));
				}
				post = new HttpPost(postURL);
				post.setEntity(new UrlEncodedFormEntity(formparams));
				String rr = httpclient.execute(post, responseHandler);
				String nextURL = getNexrURL0(URLDecoder.decode(rr, "GBK"));
				get = new HttpGet(nextURL);
				String str = httpclient.execute(get, responseHandler);
				num++;
				if (str.contains("displayname")) {
					isRun = false;
				}
			}

		} else if (checkResult == 1) {
			throw new Exception("登陆的账号或者密码错误...");
		} else if (checkResult == 0) {
			String nextURL = getNexrURL0(URLDecoder.decode(s, "GBK"));
			get = new HttpGet(nextURL);
			String str = httpclient.execute(get, responseHandler);
			if (!str.contains("displayname")) {
				throw new Exception("登陆错误...");
			}
		}
	}

	/**
	 * 检验是否需要输入验证码,还是密码错误,账号不存在的情况
	 * 
	 * @param s
	 * @return 当返回0, 不需要验证码 当返回1,账号或者密码错误 当返回2,需要输入验证码 当返回-1,说明是未知情况
	 * 
	 */
	private int checkDoor(String s) {
		if (getCode("{" + s + "}", "retcode").equals("0")) {
			return 0;
		}
		// 当retcode=0是不需要输入验证码的
		if (s.matches("(.*?)retcode=[0](.*?)")) {
			return 0;
		}
		if (s.matches("(.*?)retcode=[101](.*?)")) {
			return 1;
		}
		// 其他的数值说明不是正常情况,需要输入验证码或者账号不存在或者密码不正确等
		return s.matches("(.*?)retcode=[4049|6102|\\d+](.*?)") ? 2 : -1;
	}

	public String getWeiBoInfoFromId(String id) {
		HttpGet hg = new HttpGet("http://www.weibo.com/"+id);
		try {
			return this.httpclient.execute(hg,new StringResponseHandler());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private String encodeUserName(String email) {
		String userName = "";
		try {
			email = email.replaceAll("@", "%40");
			userName = Base64.encode(email.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userName;
	}

	private String getCode(String input, String mark) {
		if (input == null || mark == null) {
			return null;
		}

		int point = input.indexOf(mark);
		int start = input.indexOf(":", point);
		int end = input.indexOf(",", point);
		if (end <= 0) {
			end = input.indexOf("}", point);
			if (end <= 0) {
				end = input.indexOf("]", point);
			}
		}
		String str = input.substring(start + 1, end);
		if (StringUtils.isNotBlank(str)) {
			str = str.trim();
			if (str.startsWith("\"")) {
				str = str.substring(1, str.length() - 1);
			}
		}
		return str;

	}

	/**
	 * 最后登陆成功前,需要到这个地址去取得Cookie的
	 * 
	 * @param s
	 * @return 返回取得Cookie的地址URL
	 */
	private String getNexrURL0(String s) {
		String url = "";
		if (StringUtils.isNotBlank(s)) {

			Matcher m = p0.matcher(s);
			if (m.find()) {
				url = m.group();
			}
		}
		if (url.startsWith("location")) {
			url = url.substring(18, url.length() - 2);
		}
		return url;
	}

	/**
	 * 取得验证码图片
	 * 
	 * @param check
	 * @return
	 */
	private File getMarkFile(String check) {
		String tmpPath = System.getProperty("user.home") + File.separator
				+ ".loginsina";
		File pngf = new File(tmpPath);
		if (!(pngf.exists() || pngf.isDirectory())) {
			pngf.mkdirs();
		}

		String filePath = tmpPath + File.separator + format.format(new Date())
				+ ".png";

		return getFile(check, filePath, "image");

	}

	private File getFile(String url, String filePath, String mimeType) {

		File imageFile = new File(filePath);
		if (!(imageFile.exists() || imageFile.isFile())) {
			try {
				imageFile.createNewFile();
			} catch (IOException e) {

			}
		}
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpclient.execute(httpGet);
		} catch (ClientProtocolException e1) {

		} catch (IOException e1) {

		}
		boolean success = false;
		// 请求成功
		System.out.println(response.getStatusLine().getStatusCode());
		if (HttpStatus.SC_OK == response.getStatusLine().getStatusCode()) {
			// 取得请求内容
			HttpEntity entity = response.getEntity();
			// 显示内容
			if (entity != null) {
				// 可以判断是否是文件数据流
				String mimetype1 = EntityUtils.getContentMimeType(entity);
				if (StringUtils.isNotBlank(mimetype1)
						&& mimetype1.contains(mimeType)) {
					FileOutputStream output = null;

					try {
						output = new FileOutputStream(imageFile);
						entity.writeTo(output);
						output.flush();
						success = true;
					} catch (IOException e) {

					} finally {
						if (output != null) {
							try {
								output.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		}
		if (success) {
			return imageFile;
		}
		return null;
	}

	/**
	 * 当需要输入验证码时取得下一个URL连接
	 * 
	 * @param s
	 * @return
	 */
	private String getNexrURL(String s) {
		String url = "";
		if (StringUtils.isNotBlank(s)) {
			Matcher m = p.matcher(s);
			if (m.find()) {
				url = m.group();
			}
		}
		if (url.startsWith("(")) {
			url = url.substring(2, url.length() - 2);
		}
		return url;
	}

	private class StringResponseHandler implements ResponseHandler<String> {

		@Override
		public String handleResponse(HttpResponse response)
				throws ClientProtocolException, IOException {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				HttpEntity entity = response.getEntity();
				return entity != null ? EntityUtils.toString(entity) : null;
			} else {
				throw new ClientProtocolException(
						"Unexpected response status: " + status);
			}
		}

	}

	@Override
	public String getMySelf() {
		HttpGet hg = new HttpGet("http://www.weibo.com/u/1773610573/home?wvr=5");
		try {
			String content = this.httpclient.execute(hg,
					new StringResponseHandler());
			return content;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}

	public static void main(String[] args) throws Exception {
		WeiboCrawler wc = new WeiboCrawler("yixiaohuamax@gmail.com",
				"hua8xiao6");
		//System.out.println(wc.getMySelf());
		System.out.println(wc.getWeiBoInfoFromId("shenzhenfabu"));
	}

}
