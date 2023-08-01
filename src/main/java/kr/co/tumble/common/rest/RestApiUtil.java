package kr.co.tumble.common.rest;

import kr.co.tumble.common.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * packageName    : kr.co.tumble.common.rest
 * fileName       : RestApiUtil
 * author         : DANIEL
 * date           : 2023-08-01
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2023-08-01        DANIEL       최초 생성
 */

@Slf4j
@Service
public class RestApiUtil {

    /**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> get(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).get(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환. RequestBody 대신 Query String param 적용
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> getWithQueryParam(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).getWithQueryParam(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> post(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).post(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> put(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).put(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> patch(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).patch(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> delete(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url).delete(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출결과 처리. 정상인경우 응답반환. 오류인지 체크하여 오류 response 생성.
    	 * @param response
    	 * @return
    	 * @throws Exception
    	 */
    	private <T> Response<T> processResult(RestResponse<Response<T>> response) {
    		// 오류응답인 경우(exception 이 발생한 경우)
    		Response<T> responseBoby = response.getBody();
    		if (responseBoby != null  && responseBoby.getError() != null && Boolean.TRUE.equals(responseBoby.getError())) {
    			throw new AppException(response.getBody().getCode(), response.getBody().getMessage());
    		}

    		// 응답코드가 "0000":성공 인 경우
    		return responseBoby;
    	}

    	/**
    	 * RestApi 호출하여 응답+HttpStatus 반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> ResponseEntity<Response<T>> getAndGetResponseEntity(String url, Object params) throws Exception {
    		RestResponse<Response<T>> response = RestApi.client(url).get(params, new ParameterizedTypeReference<Response<T>>() {});
    		if (response.hasError()) {
    			log.error("", response.getException());
    			if (response.getBody() != null && response.getBody().getMessage() != null) {
    				throw new AppException(response.getBody().getCode(), response.getBody().getMessage());
    			}
    			else {
    				throw response.getException();
    			}
    		}
    		return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getStatusCode()));
    	}

    	/**
    	 * RestApi 호출하여 응답+HttpStatus 반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> ResponseEntity<Response<T>> postAndGetResponseEntity(String url, Object params) throws Exception {
    		RestResponse<Response<T>> response = RestApi.client(url).post(params, new ParameterizedTypeReference<Response<T>>() {});
    		if (response.hasError()) {
    			log.error("", response.getException());
    			throw response.getException();
    		}
    		return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getStatusCode()));
    	}

    	/**
    	 * RestApi 호출하여 응답+HttpStatus 반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> ResponseEntity<Response<T>> putAndGetResponseEntity(String url, Object params) throws Exception {
    		RestResponse<Response<T>> response = RestApi.client(url).put(params, new ParameterizedTypeReference<Response<T>>() {});
    		if (response.hasError()) {
    			log.error("", response.getException());
    			throw response.getException();
    		}
    		return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getStatusCode()));
    	}

    	/**
    	 * RestApi 호출하여 응답+HttpStatus 반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> ResponseEntity<Response<T>> patchAndGetResponseEntity(String url, Object params) throws Exception {
    		RestResponse<Response<T>> response = RestApi.client(url).patch(params, new ParameterizedTypeReference<Response<T>>() {});
    		if (response.hasError()) {
    			log.error("", response.getException());
    			throw response.getException();
    		}
    		return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getStatusCode()));
    	}

    	/**
    	 * RestApi 호출하여 응답+HttpStatus 반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> ResponseEntity<Response<T>> deleteAndGetResponseEntity(String url, Object params) throws Exception {
    		RestResponse<Response<T>> response = RestApi.client(url).delete(params, new ParameterizedTypeReference<Response<T>>() {});
    		if (response.hasError()) {
    			log.error("", response.getException());
    			throw response.getException();
    		}
    		return new ResponseEntity<>(response.getBody(), HttpStatus.valueOf(response.getStatusCode()));
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> getSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).get(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환. RequestBody 대신 Query String param 적용
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> getWithQueryParamSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).getWithQueryParam(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> postSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).post(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> putSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).put(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> patchSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).patch(params, responseReference);
    		return processResult(response);
    	}

    	/**
    	 * RestApi 호출하여 응답반환.
    	 * @param <T>
    	 * @param url
    	 * @param params
    	 * @param responseReference
    	 * @return
    	 * @throws Exception
    	 */
    	public <T> Response<T> deleteSecured(String url, Object params, ParameterizedTypeReference<Response<T>> responseReference) {
    		RestResponse<Response<T>> response = RestApi.client(url, true, true).delete(params, responseReference);
    		return processResult(response);
    	}
}
