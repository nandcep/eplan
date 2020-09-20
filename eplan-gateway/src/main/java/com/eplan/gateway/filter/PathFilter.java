package com.eplan.gateway.filter;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eplan.gateway.config.PathConfiguration;
import com.eplan.gateway.dto.UserLoginDto;
import com.eplan.gateway.util.CacheUtility;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Component
@Slf4j
public class PathFilter extends ZuulFilter {

    @Autowired
    private PathConfiguration pathConfiguration;

    @Autowired
    private CacheUtility cacheUtility;

    @Value("${eplan.jwt.secret}")
    private String eplanJwtSecret;

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();
        String[] ignores = pathConfiguration.getIgnores();
        boolean isIgnore = false;
        for (String ignore : ignores) {
            if (request.getRequestURL().toString().contains(ignore)) {
                isIgnore = true;
                break;
            }
        }
        if (isIgnore) {
            return null;
        } else {
            String token = this.getCookie(request, "Authorization");
            String userId = request.getHeader("userId");
            if (StringUtils.isEmpty(token)) {
                this.doThrow401(response, requestContext);
                return null;
            } else {
                String usernameEncode = null;
                try {
                    Jws<Claims> claims = this.getJwtClaim(token);
                    if(claims == null){
                        log.error("JWT claims is null ");
                        this.doThrow401(response, requestContext);
                        return null;
                    }
                    usernameEncode = claims.getBody().getSubject();
                } catch (SignatureException se) {
                    log.error("SignatureException = {}", se);
                    this.doThrow401(response, requestContext);
                    return null;
                }
                Base64 base64 = new Base64();
                String decodedUsername = new String(base64.decode(usernameEncode.getBytes()));
                String userLoginCache = cacheUtility.get("USERLOGIN", decodedUsername);
                if (StringUtils.isEmpty(userLoginCache)) {
                    log.error("userLoginCache is empty");
                    this.doThrow401(response, requestContext);
                    return null;
                }
                UserLoginDto userLoginDto = null;
                try {
                    userLoginDto = new ObjectMapper().readValue(userLoginCache, UserLoginDto.class);
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                if(userLoginDto == null){
                    log.error("user login cache is unparseable");
                    this.doThrow401(response, requestContext);
                    return null;
                }
                if(token.equals(userLoginDto.getToken()) && userLoginDto.getId() == Long.valueOf(userId)){
                    log.error("token cookie mismatched with redis token");
                    this.doThrow401(response, requestContext);
                    return null;
                }
                Boolean isGranted = false;
                log.info("userLoginDto.getPermissions() = {} ", userLoginDto.getPermissions());
                for (String permission : userLoginDto.getPermissions()){
                    log.info("permission [{}] = request.getRequestURI [{}] ", permission, request.getRequestURI());
                    if(request.getRequestURI().contains(permission)){
                        this.cacheUtility.set("GWAUTH", request.getHeader("userId"), userLoginDto.getUsername(), null);
                        isGranted = true;
                        break;
                    }
                }
                if(!isGranted){
                    log.error("unable to access, permission denied");
                    this.doThrow401(response, requestContext);
                    return null;
                }
            }
        }
        return null;
    }

    private void doThrow401(HttpServletResponse response, RequestContext requestContext){
        requestContext.setSendZuulResponse(false);
        requestContext.setResponseStatusCode(401);
        response.setContentType("application/json");
        try {
            requestContext.setResponseBody(new ObjectMapper().writeValueAsString(getUnauthorizedBody()));
        } catch (JsonProcessingException e) {
            log.error("PathFilter.e = {}", e.getMessage());
            e.printStackTrace();
        }
        requestContext.setResponse(response);
    }

    private Jws<Claims> getJwtClaim(String token){
        if(StringUtils.isEmpty(token)){
            return null;
        }
        return Jwts.parser().setSigningKey(eplanJwtSecret).parseClaimsJws(token);
    }

    private String getCookie(HttpServletRequest request, String cookieName){
        Cookie cookie = WebUtils.getCookie(request, cookieName);
        if(cookie != null){
            return cookie.getValue();
        }
        return null;
    }

    private Map<String, String> getUnauthorizedBody(){
        Map<String, String> body = new HashMap<String, String>();
        body.put("message", "Unauthorized access");
        return body;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 1;
    }


    
}
