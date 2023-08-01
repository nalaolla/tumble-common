package kr.co.tumble.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.tumble.common.exception.InvalidTokenException;
import kr.co.tumble.common.messageconverter.CustomObjectMapper;
import kr.co.tumble.common.rest.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * ExceptionHandlerFilter
 */
@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
      try{
          filterChain.doFilter(request,response);
      } catch (InvalidTokenException ex){
          log.error("exception exception handler filter", ex);
          setErrorResponse(response,ex);
      }
    }

    public void setErrorResponse(HttpServletResponse response,InvalidTokenException e){
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        Response<Object> errorResponse = Response.builder()
                .code(e.getErrorCode())
                .message(e.getErrorMessage())
                .error(true)
                .build();

        try{
            ObjectMapper objectMapper = CustomObjectMapper.get();

            String json = objectMapper.writeValueAsString(errorResponse);
            response.getWriter().write(json);
        }catch (IOException ex){
            log.error("", ex);
        }
    }
}