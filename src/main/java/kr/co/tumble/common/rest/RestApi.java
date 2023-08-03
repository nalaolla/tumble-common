package kr.co.tumble.common.rest;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import kr.co.tumble.common.constant.TumbleConstants;
import kr.co.tumble.common.context.ApplicationContextWrapper;
import kr.co.tumble.common.context.ConfigProperties;
import kr.co.tumble.common.context.CookieContextHolder;
import kr.co.tumble.common.context.RequestContextUtil;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import kr.co.tumble.common.rest.*;
import kr.co.tumble.common.token.MemberTokenService;
import kr.co.tumble.common.token.ServiceTokenService;
import kr.co.tumble.common.token.TokenRequest;
import kr.co.tumble.common.token.UserDetail;
import kr.co.tumble.common.util.JsonUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


import io.netty.channel.ChannelOption;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;

/**
 * WebClient 모듈
 */
@Slf4j
public abstract class RestApi {

	private static ServiceTokenService serviceTokenService = null;
	private static MemberTokenService memberTokenService = null;

	private static int connectionTimeoutSeconds = 10;

	private HttpHeaders requestHeaders;
	private UriComponentsBuilder uriComponentsBuilder;
	private Map<String, Object> uriVariables;

	private long latencyTimes = -1;

	private boolean enableTokenAuth = false;
	private boolean useMemberToken = false;

	public static RestApi client(String url) {
		return client(url, true, false);
	}

	public static RestApi client(String url, boolean enableTokenAuth) {
		return client(url, enableTokenAuth, false);
	}

	public static RestApi client(String url, boolean enableTokenAuth, boolean useMemberToken) {
		RestApi restApi = new RestApi() {};
		if (RestApi.serviceTokenService == null) {
			RestApi.serviceTokenService = (ServiceTokenService)ApplicationContextWrapper.getBean("serviceTokenService");
			RestApi.memberTokenService = (MemberTokenService) ApplicationContextWrapper.getBean("memberTokenService");
		}
		restApi.uriComponentsBuilder = UriComponentsBuilder.fromHttpUrl(url);
		restApi.enableTokenAuth = enableTokenAuth;
		restApi.useMemberToken = useMemberToken;
		restApi.requestHeaders = new HttpHeaders();
		restApi.requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		restApi.requestHeaders.setContentType(MediaType.APPLICATION_JSON);

		return restApi;
	}

	public RestApi addHeader(String key, String value) {
		if (Objects.isNull(requestHeaders)) {
			requestHeaders = new HttpHeaders();
		}
		requestHeaders.add(key, value);
		return this;
	}

	public RestApi setHeader(String key, String value) {
		if (Objects.isNull(requestHeaders)) {
			requestHeaders = new HttpHeaders();
		}
		requestHeaders.set(key, value);
		return this;
	}

	public RestApi queryParam(String name, Object... values) {
		this.uriComponentsBuilder.queryParam(name, values);
		return this;
	}

	public RestApi uriVariable(String name, Object value) {
		if (Objects.isNull(uriVariables)) {
			this.uriVariables = new HashMap<>();
		}
		this.uriVariables.put(name, value);
		return this;
	}

	public MultiValueMap<String, String> getHeaders() {
		return requestHeaders;
	}

	public long getLatencyTimes() {
		return latencyTimes;
	}

	public <T> RestResponse<T> get(Object request, Class<T> type) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.GET, type);
	}

	public <T> RestResponse<T> get(Object request, ParameterizedTypeReference<T> responseReference) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.GET, responseReference);
	}

	public <T> RestResponse<T> get(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.GET, responseReference, responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	public <T> RestResponse<T> get(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.GET, responseReference, responseTimeoutSeconds, requestTimeoutSeconds);
	}

	public <T> RestResponse<T> post(Object request, Class<T> type) {
		return execute(request, HttpMethod.POST, type);
	}

	public <T> RestResponse<T> post(Object request, ParameterizedTypeReference<T> responseReference) {
		return execute(request, HttpMethod.POST, responseReference);
	}

	public <T> RestResponse<T> post(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds) {
		return execute(request, HttpMethod.POST, responseReference, responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	public <T> RestResponse<T> post(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		return execute(request, HttpMethod.POST, responseReference, responseTimeoutSeconds, requestTimeoutSeconds);
	}

	public <T> RestResponse<T> put(Object request, Class<T> type) {
		return execute(request, HttpMethod.PUT, type);
	}

	public <T> RestResponse<T> put(Object request, ParameterizedTypeReference<T> responseReference) {
		return execute(request, HttpMethod.PUT, responseReference);
	}

	public <T> RestResponse<T> put(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds) {
		return execute(request, HttpMethod.PUT, responseReference, responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	public <T> RestResponse<T> put(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		return execute(request, HttpMethod.PUT, responseReference, responseTimeoutSeconds, requestTimeoutSeconds);
	}

	public <T> RestResponse<T> patch(Object request, Class<T> type) {
		return execute(request, HttpMethod.PATCH, type);
	}

	public <T> RestResponse<T> patch(Object request, ParameterizedTypeReference<T> responseReference) {
		return execute(request, HttpMethod.PATCH, responseReference);
	}

	public <T> RestResponse<T> patch(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds) {
		return execute(request, HttpMethod.PATCH, responseReference, responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	public <T> RestResponse<T> patch(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		return execute(request, HttpMethod.PATCH, responseReference, responseTimeoutSeconds, requestTimeoutSeconds);
	}

	public <T> RestResponse<T> delete(Object request, Class<T> type) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.DELETE, type);
	}

	public <T> RestResponse<T> delete(Object request, ParameterizedTypeReference<T> responseReference) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.DELETE, responseReference);
	}

	public <T> RestResponse<T> delete(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.DELETE, responseReference, responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	public <T> RestResponse<T> delete(Object request, ParameterizedTypeReference<T> responseReference, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		setObjectAsQueryParam(request);
		return execute(null, HttpMethod.DELETE, responseReference, responseTimeoutSeconds, requestTimeoutSeconds);
	}

	public WebClient webClient(int responseTimeoutSeconds) {
		return WebClientInstance.get(responseTimeoutSeconds);
	}

	public WebClient webClient(int responseTimeoutSeconds, int connectionTimeoutSeconds) {
		return WebClientInstance.get(responseTimeoutSeconds, connectionTimeoutSeconds);
	}

	private void configRequestHeader(HttpHeaders httpHeaders) {
		configAuthorization(httpHeaders);
		WebClientInstance.configRequestHeader(httpHeaders);

		String callAppName = "";
		try {
			HttpServletRequest request = RequestContextUtil.getHttpServletRequest();
			callAppName = request.getHeader(TumbleConstants.HEADER_RESTAPI_CALL_APP_NAME_KEY);
		} catch (Exception ex) {
			callAppName = "";
		}
		if (StringUtils.isBlank(callAppName)) {
			callAppName = Optional.ofNullable(ConfigProperties.getInstance().getValue(TumbleConstants.SPRING_APP_NAME_KEY)).orElse("");
		}
		httpHeaders.add(TumbleConstants.HEADER_RESTAPI_CALL_APP_NAME_KEY, callAppName);

		Map<String, String> cookieMap = CookieContextHolder.getCookieMap().getMap();
		if (cookieMap == null) {
			cookieMap = new HashMap<>();
		}
		String siteNo = CookieContextHolder.getSiteNo();
		String langCd = CookieContextHolder.getLangCd();
		String dataLangCd = CookieContextHolder.getDataLangCd();
		String mallNo = CookieContextHolder.getMallNo();
		String chlNo = CookieContextHolder.getChlNo();
		String sessNo = CookieContextHolder.getSessNo();
		cookieMap.put(TumbleConstants.COOKIE_SITE_NO_KEY, siteNo);
		cookieMap.put(TumbleConstants.COOKIE_LANG_CD_KEY, langCd);
		cookieMap.put(TumbleConstants.COOKIE_DATA_LANG_CD_KEY, dataLangCd);
		cookieMap.put(TumbleConstants.COOKIE_MALL_NO_KEY, mallNo);
		cookieMap.put(TumbleConstants.COOKIE_CHL_NO_KEY, chlNo);
		cookieMap.put(TumbleConstants.COOKIE_SESS_NO_KEY, sessNo);

		List<String> cookieList = ConfigProperties.getInstance().getListValue("cookies");
		StringBuilder stringBuilder = new StringBuilder();
		String cookie = "";
		for (Map.Entry<String, String> entrySet : cookieMap.entrySet()) {
			if (StringUtils.isNotBlank(entrySet.getValue()) && cookieList.contains(entrySet.getKey())) {
				stringBuilder.append(entrySet.getKey() + "=" + entrySet.getValue() +"; ");
			}
		}
		cookie = StringUtils.removeEnd(stringBuilder.toString(), " ");

		httpHeaders.add(TumbleConstants.HEADER_COOKIE_KEY, cookie);
	}

	private void configAuthorization(HttpHeaders httpHeaders) {
		if ( this.enableTokenAuth ) {
			if (this.useMemberToken) {
				int accessExpMin = 1;
				httpHeaders.setBearerAuth(memberTokenService.createRestApiTempAccessToken(getUserDetail(), accessExpMin));
			} else {
				final long validMillis = 60 * 1000L;
				httpHeaders.setBearerAuth(serviceTokenService.createToken(
						new TokenRequest().setUserName("service").setValidMillis(validMillis)).getToken());
			}
		}
	}

	private <T> RestResponse<T> execute(Object requestObject, HttpMethod method, Object type) {
		return execute(requestObject, method, type, 0, connectionTimeoutSeconds);
	}
	@SuppressWarnings("unchecked")
	private <T> RestResponse<T> execute(Object requestObject, HttpMethod method, Object type, int responseTimeoutSeconds, int requestTimeoutSeconds) {
		if (Objects.nonNull(uriVariables)) {
			this.uriComponentsBuilder.uriVariables(uriVariables);
		}

		URI orgUrl = this.uriComponentsBuilder.build().toUri(); // 한글 파라미터 로깅을 위하여.
		this.uriComponentsBuilder.encode();
		URI url = this.uriComponentsBuilder.build().toUri();

		configRequestHeader(requestHeaders);

		ResponseEntity<T> responseEntity = null;

		long start = System.currentTimeMillis();

		try {
			ResponseSpec respenseSpec;

			if (requestObject == null) { // method == HttpMethod.GET || method == HttpMethod.DELETE
				// request body 없는 경우
				respenseSpec = webClient(responseTimeoutSeconds, requestTimeoutSeconds)
						.method(method)
						.uri(url)
						.headers(newRequestHeader -> newRequestHeader.addAll(requestHeaders))
						.retrieve();
			} else {
				// request body 있는 경우
				respenseSpec = webClient(responseTimeoutSeconds, requestTimeoutSeconds)
						.method(method)
						.uri(url)
						.body(BodyInserters.fromValue(requestObject))
						.headers(newRequestHeader -> newRequestHeader.addAll(requestHeaders))
						.retrieve();
			}

			// 결과 type이 Class<T>로 지정된 경우
			if (type instanceof Class<?>) {
				responseEntity = respenseSpec.toEntity((Class<T>)type).block();
			}
			// 결과 type이 ParameterizedTypeReference<T>로 지정된 경우
			else {
				responseEntity = respenseSpec.toEntity((ParameterizedTypeReference<T>)type).block();
			}

			this.latencyTimes = System.currentTimeMillis() - start;
			logging(orgUrl, method, requestHeaders, requestObject, responseEntity, null);

			return new RestResponse<>(responseEntity);
		} catch (RestClientResponseException e) {
			log.debug("RestClientResponseException {}", e);
			responseEntity = null;
			this.latencyTimes = System.currentTimeMillis() - start;
			logging(orgUrl, method, requestHeaders, requestObject, responseEntity, e);

			return new RestResponse<>(e);
		} catch (WebClientResponseException e) {
			log.debug("WebClientResponseException {}", e);
			responseEntity = null;
			this.latencyTimes = System.currentTimeMillis() - start;
			logging(orgUrl, method, requestHeaders, requestObject, responseEntity, e);

			return (RestResponse<T>) this.createErrorResponse(e);
		} catch (Exception e) {
			log.debug("Exception {}", e);
			responseEntity = null;
			this.latencyTimes = System.currentTimeMillis() - start;
			logging(orgUrl, method, requestHeaders, requestObject, responseEntity, e);

			return new RestResponse<>(e);
		}
	}

	@SuppressWarnings("unchecked")
	private <T> RestResponse<Response<T>> createErrorResponse(WebClientResponseException e) {
		String message = null;
		if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
			message = "{\"code\":\"404\", \"message\":\"url not found\"}";
		} else {
			message = e.getResponseBodyAsString(Charset.defaultCharset());
		}
		ObjectMapper objectMapper = CustomObjectMapper.get();
		Map<String, Object> data = null;
		try {
			data = objectMapper.readValue(message, Map.class);
		} catch (JsonMappingException e1) {
			log.error("", e1);
		} catch (JsonProcessingException e1) {
			log.error("", e1);
		}

		Response<T> response = new Response<>();
		if (data == null) {
			response.setCode("500");
			response.setMessage("Unkonw error.");
			response.setError(true);
		} else {
			if (ObjectUtils.isNotEmpty(data.get("code"))) {
				response.setCode((String)data.get("code"));
			}

			if (ObjectUtils.isNotEmpty(data.get("message"))) {
				response.setMessage((String)data.get("message"));
			}

			if (ObjectUtils.isNotEmpty(data.get("error"))) {
				response.setError((Boolean) data.get("error"));
			}

			if (ObjectUtils.isNotEmpty(data.get("isProcess"))) {
				response.setIsProcess((Boolean) data.get("isProcess"));
			}

			if (ObjectUtils.isNotEmpty(data.get("errors"))) {
				response.setErrors((List<ValidationError>) data.get("errors"));
			}

			if (ObjectUtils.isNotEmpty(data.get("payload"))) {
				response.setPayload((T) data.get("payload"));
			}
		}

		ResponseEntity<Response<T>> responseEntity = new ResponseEntity<>(response, e.getStatusCode());
		RestResponse<Response<T>> restResponse =  new RestResponse<>(responseEntity, e);
		return restResponse;
	}

	private <T> void logging(URI url, HttpMethod method, HttpHeaders reqHeaders, Object reqBody, ResponseEntity<T> responseEntity, Exception e) {
		if (log.isDebugEnabled()) {
			if (Objects.isNull(e)) {
				Object resBody = Optional.ofNullable(responseEntity.getBody()).orElse(null);
				logging(url, method, reqHeaders, reqBody==null?null:JsonUtils.string(reqBody), responseEntity.getStatusCode().value(),
						responseEntity.getStatusCode().toString(), responseEntity.getHeaders(),
						resBody==null?null:JsonUtils.string(resBody), null);
			} else if (e instanceof RestClientResponseException re) {
				logging(url, method, reqHeaders, reqBody==null?null:JsonUtils.string(reqBody), re.getStatusCode().value(), re.getStatusText(),
						re.getResponseHeaders(), re.getResponseBodyAsString(), e);
			} else {
				logging(url, method, reqHeaders, reqBody==null?null:JsonUtils.string(reqBody), -1, null, null, null, e);
			}
		}
	}

	private void logging(URI url, HttpMethod method, HttpHeaders reqHeaders, String reqBody,
			int statusCode,String statusText, HttpHeaders resHeaders, String resBody, Exception e) {
		StringBuilder sb = new StringBuilder().append('\n');
		sb.append("##################################################################").append('\n');
		sb.append("# [REST_API] latency-time: ").append(this.latencyTimes).append(" ms\n");
		sb.append("##[Request]#######################################################").append('\n');
		sb.append("# URL    : ").append(url).append('\n');
		sb.append("# Method : ").append(method).append('\n');
		sb.append("# Headers: ").append(reqHeaders).append('\n');
		sb.append("# Body   : ").append(StringUtils.abbreviate(reqBody, 1000)).append('\n');
		sb.append("##[Response]######################################################").append('\n');
		sb.append("# Code   : ").append(statusCode).append(' ').append(statusText).append('\n');
		sb.append("# Headers: ").append(resHeaders).append('\n');
		sb.append("# Body   : ").append(resBody).append('\n');
		sb.append("##################################################################");

		if (Objects.isNull(e)) {
			if (log.isDebugEnabled()) {
				log.debug(sb.toString());
			}
		} else {
			sb.append('\n').append("# Exception : ");
			log.error(sb.toString(), e);
		}
	}

	@Component
	static class WebClientInstance {

		// MAX_IN_MEMORY_SIZE 설정값
		private static final int WEBCLIENT_MAX_IN_MEMORY_SIZE = 100 * 1024 * 1024; // 100M
		private static WebClient webClient;
		private static ClientInfoResolver clientInfoResolver;

		static void configRequestHeader(HttpHeaders httpHeaders) {
			if (Objects.nonNull(WebClientInstance.clientInfoResolver)) {
				ClientInfo clientInfo = WebClientInstance.clientInfoResolver.resolve();
				if (Objects.nonNull(clientInfo)) {
					httpHeaders.set(ClientInfo.CLIENT_INFO_HEADER_NAME, JsonUtils.string(clientInfo));
				}
			}
		}

		public static void setWebClient(WebClient webClient) {
			if (WebClientInstance.webClient == null) {
				WebClientInstance.webClient = webClient;
			}
		}

		public static WebClient getWebClient() {
			return WebClientInstance.webClient;
		}

		public static void setClientInfoResolver(ClientInfoResolver clientInfoResolver) {
			if (WebClientInstance.clientInfoResolver == null) {
				WebClientInstance.clientInfoResolver = clientInfoResolver;
			}
		}

		public static ClientInfoResolver getClientInfoResolver() {
			return WebClientInstance.clientInfoResolver;
		}

		@Autowired
		public void init(WebClient webClient) {
			setWebClient(webClient);
		}

		@Autowired(required = false)
		public void init(ClientInfoResolver clientInfoResolver) {
			setClientInfoResolver(clientInfoResolver);
		}

		/**
		 * 응답 타임아웃 설정이 필요한 경우에만 사용.
		 * @param responseTimeoutSeconds
		 * @return
		 */
		static WebClient get(int responseTimeoutSeconds) {
			return get(responseTimeoutSeconds, connectionTimeoutSeconds);
		}

		static WebClient get(int responseTimeoutSeconds, int connectionTimeoutSeconds ) {
			// TIMEOUT 설정
			HttpClient client = HttpClient.create();
			if (connectionTimeoutSeconds > 0) {
				client = client.responseTimeout(Duration.ofSeconds(responseTimeoutSeconds))
						.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeoutSeconds*1000); // Connection Timeout
			}
			ClientHttpConnector connector = new ReactorClientHttpConnector(client);

			// MAX_IN_MEMORY_SIZE 설정
			ExchangeStrategies exchangeStrategies = ExchangeStrategies.builder()
					.codecs(configurer -> configurer
							.defaultCodecs()
							.maxInMemorySize(WEBCLIENT_MAX_IN_MEMORY_SIZE))
					.build();

			ObjectMapper objectMapper = CustomObjectMapper.get();

			return WebClient.builder()
					.clientConnector(connector)
					.exchangeStrategies(exchangeStrategies)
					.codecs(configurer -> {
						configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper, MediaType.APPLICATION_JSON));
						configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper, MediaType.APPLICATION_JSON));
					})
					.build();
		}

	}

	/**
	 * GET 메소드 api를 호출한다. 호출 시 파라미터 객체를 query string으로 설정한다.
	 * @param <T>
	 * @param request
	 * @param responseReference
	 * @return
	 */
	public <T> RestResponse<T> getWithQueryParam(Object request, ParameterizedTypeReference<T> responseReference) {
		setObjectAsQueryParam(request);
		return execute(request, HttpMethod.GET, responseReference);
	}

	/**
	 * 객체를 필드 중에서 WrapperType+String 필드를 queryString param으로 세팅한다.
	 * @param request
	 */
    private void setObjectAsQueryParam(Object request) {
		ObjectMapper objectMapper = CustomObjectMapper.get();
        if (request != null) {
            Map<String, Object> map = objectMapper.convertValue(request, Map.class);
            map.entrySet().stream().forEach(entry ->
            	{
            		if (entry.getValue() != null && isWrapperType(entry.getValue())) {
            	    	if (entry.getValue() instanceof List<?> && !((List<?>)entry.getValue()).isEmpty()) {
            				this.uriComponentsBuilder.queryParam(entry.getKey(), ((List<?>)entry.getValue()).toArray());
            	    	} else {
            				this.uriComponentsBuilder.queryParam(entry.getKey(), entry.getValue());
            	    	}
            		}
            	});
        }
    }

    /**
     * WrapperType 목록
     */
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    /**
     * WrapperType + String 여부 체크
     * @param obj
     * @return
     */
    private static boolean isWrapperType(Object obj) {
    	Class<?> clazz = obj.getClass();
    	if (clazz.isArray()) {
    		return WRAPPER_TYPES.contains(clazz.getComponentType());
    	} else if (obj instanceof List<?> && !((List<?>)obj).isEmpty()) {
    		return WRAPPER_TYPES.contains(((List<?>)obj).get(0).getClass());
    	} else {
    		return WRAPPER_TYPES.contains(clazz);
    	}
    }

    /**
     * WrapperType 목록 저장
     * @return
     */
    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

    private UserDetail getUserDetail() {
    	if (SecurityContextHolder.getContext().getAuthentication() != null
			&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() != null
			&& SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetail userDetail
			&& StringUtils.isNotBlank(userDetail.getMbrNo())) {
    		return (UserDetail)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	} else {
    		throw new AccessDeniedException("UNAUTHORIZED");
    	}
    }
}