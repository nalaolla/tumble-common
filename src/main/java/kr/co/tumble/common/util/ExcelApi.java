package kr.co.tumble.common.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.tumble.common.context.RequestContextUtil;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import org.springframework.util.FileCopyUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * ExcelApi Class
 */
public abstract class ExcelApi {

	private static HttpServletResponse response;

	private static String requestUrl;

	private static StringBuilder postData;

	private static final int BUFFER_SIZE = 1024 * 1024;

	public static ExcelApi excelClient(String url, Object params) throws IOException {
		return excelClient(url, params, "file");
	}

	public static ExcelApi excelClient(String url, Object params, String fileName) throws IOException {
		String fileNm = StringUtil.nvl(fileName, "file");
		ExcelApi excelApi = new ExcelApi() {};
		response = RequestContextUtil.getHttpServletResponse();
		postData = new StringBuilder();
		requestUrl = url;
		ObjectMapper objectMapper = CustomObjectMapper.get();
		Map<String, Object> map = objectMapper.convertValue(params, Map.class);
		for (Map.Entry<String,Object> param : map.entrySet()) {
			if(postData.length() != 0) postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		response.setContentType("application/vnd.ms-excel");
		response.setHeader("Content-Disposition", String.format("attachment;filename=%s.xlsx", fileNm));
		return excelApi;
	}

	public void get() throws IOException {

		URL url = new URL(requestUrl + "?" + postData.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setDoOutput(true);

		InputStream ins = conn.getInputStream();
		FileCopyUtils.copy(new BufferedInputStream(ins), response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		ins.close();
	}

	public void post() throws IOException {

		URL url = new URL(requestUrl);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setChunkedStreamingMode(BUFFER_SIZE);
		byte[] postDataBytes = postData.toString().getBytes(StandardCharsets.UTF_8);
		try (OutputStream os = conn.getOutputStream()) {
			os.write(postDataBytes, 0, postDataBytes.length);
		}

		InputStream ins = conn.getInputStream();
		FileCopyUtils.copy(new BufferedInputStream(ins), response.getOutputStream());
		response.getOutputStream().flush();
		response.getOutputStream().close();
		ins.close();
	}

}