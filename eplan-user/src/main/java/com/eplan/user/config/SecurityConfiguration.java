package com.eplan.user.config;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.eplan.user.dto.UserLoginDto;
import com.eplan.user.service.UserDetailService;
import com.eplan.user.util.CacheUtility;
import com.eplan.user.util.UserUtility;
import com.eplan.user.util.WebUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.SignatureException;
import lombok.extern.slf4j.Slf4j;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Slf4j
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	public static final String ROOT_URI = "/api/user";

    @Autowired
	private UserDetailService userDetailService;
	
	@Autowired
	private WebUtility webUtility;
	
	@Autowired
	private UserUtility userUtility;

	@Autowired
	private CacheUtility cacheUtility;

	@Override
	public void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.userDetailsService(this.userDetailService).passwordEncoder(passwordEncoder());
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {		
		http.cors().and().csrf().disable().
			authorizeRequests()
			.antMatchers("/v1/sign-in").permitAll();
		http.authorizeRequests().antMatchers("/v1/create").authenticated();
        http.addFilterBefore(serviceFilter(), UsernamePasswordAuthenticationFilter.class);
	}
	
	private Filter serviceFilter() {
		return new OncePerRequestFilter(){
			@Override
			protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
				String userId = request.getHeader("userId");
				String gatewayAuthByUserId = cacheUtility.get("GWAUTH", userId);
				if(StringUtils.isEmpty(gatewayAuthByUserId)){
					log.info(request.getRequestURI());
					String token = webUtility.getCookie(request, "Authorization");
					log.info("token = {}", token);
					log.info("userId = {}", userId);
					if(StringUtils.isEmpty(userId) || StringUtils.isEmpty(token)) {
						SecurityContextHolder.getContext().setAuthentication(null);
					}
					else{
						String usernameEncode = null;
						try{
							usernameEncode = userUtility.getJwtClaim(token).getBody().getSubject();
						}
						catch(SignatureException se){
							log.error("SignatureException = {}", se);
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
							return;
						}
						Base64 base64 = new Base64();
						String decodedUsername = new String(base64.decode(usernameEncode.getBytes()));
						String userLoginCache = cacheUtility.get("USERLOGIN", decodedUsername);
						if(StringUtils.isEmpty(userLoginCache)){
							log.error("userLoginCache is empty");
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
							return;
						}
						UserLoginDto userLoginDto = new ObjectMapper().readValue(userLoginCache, UserLoginDto.class);
						if(!token.equals(userLoginDto.getToken()) || userLoginDto.getId() != Long.valueOf(userId)){
							log.error("token cookie mismatched with redis token");
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
							return;
						}
						Boolean isGranted = false;
						log.info("userLoginDto.getPermissions() = {} ", userLoginDto.getPermissions());
						for (String permission : userLoginDto.getPermissions()){
							log.info("permission [{}] = request.getRequestURI [{}] ", permission, ROOT_URI.concat(request.getRequestURI()));
							if(ROOT_URI.concat(request.getRequestURI()).contains(permission)){
								isGranted = true;
								break;
							}
						}
						if(!isGranted){
							log.error("unable to access, permission denied");
							response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized access");
							return;
						}
						UserDetails userDetails = userDetailService.loadUserByUsername(decodedUsername);
						UsernamePasswordAuthenticationToken authentication 
								= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
						authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authentication);
					}
				}
				else{
					UserDetails userDetails = userDetailService.loadUserByUsername(gatewayAuthByUserId);
					UsernamePasswordAuthenticationToken authentication 
							= new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
				filterChain.doFilter(request, response);
			}
		};
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
