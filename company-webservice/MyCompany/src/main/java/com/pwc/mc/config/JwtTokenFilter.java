package com.pwc.mc.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;

import com.google.gson.Gson;
import com.mysql.cj.util.StringUtils;
import com.pwc.mc.exceptions.UserNotFoundException;
import com.pwc.mc.model.AuthoritiesVO;
import com.pwc.mc.model.UserVO;
import com.pwc.mc.model.WebResponseBody;
import com.pwc.mc.service.UserRepository;
import com.pwc.mc.utils.CompanyUtils;
import com.pwc.mc.utils.JwtTokenUtil;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

@Service
public class JwtTokenFilter extends OncePerRequestFilter {

	private final JwtTokenUtil jwtTokenUtil;

	public JwtTokenFilter(UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
		this.jwtTokenUtil = jwtTokenUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
		System.out.println("doFilterInternal");

		WebResponseBody responseBody = new WebResponseBody();
		String headerRequestBefore = request.getHeader("Authorization");
		String requestTokenHeader = !StringUtils.isEmptyOrWhitespaceOnly(headerRequestBefore)
				&& headerRequestBefore.startsWith("\"") ? CompanyUtils.removeFirstandLast(headerRequestBefore)
						: headerRequestBefore;

		/*
		 * if(!StringUtils.isEmptyOrWhitespaceOnly(headerRequestBefore)) {
		 * requestTokenHeader = headerRequestBefore.startsWith("\"") ?
		 * ComplaintUtils.removeFirstandLast(headerRequestBefore) : headerRequestBefore;
		 * } else { requestTokenHeader = headerRequestBefore; }
		 */
		try {
			System.out.println("requestTokenHeader  " + requestTokenHeader);
			UserVO user = new UserVO();
			String jwtToken = null;
			// JWT Token is in the form "Bearer token". Remove Bearer word and get
			// only the Token
			if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
				jwtToken = requestTokenHeader.substring(7);
			} else {
				logger.warn("JWT Token does not begin with Bearer String");
				chain.doFilter(request, response);
			}


			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				if (jwtTokenUtil.getRoleFromToken(jwtToken)) {
					user.getAuthorities().add(new AuthoritiesVO("ROLE_MANAGER"));
				} else {
					user.getAuthorities().add(new AuthoritiesVO("ROLE_EMPLOYER"));
				}
				// if token is valid configure Spring Security to manually set
				// authentication

				if (jwtTokenUtil.validateToken(jwtToken)) {

					UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
							user, null, user.getAuthorities());
					usernamePasswordAuthenticationToken
							.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					// After setting the Authentication in the context, we specify
					// that the current user is authenticated. So it passes the
					// Spring Security Configurations successfully.

					SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
					chain.doFilter(request, response);
				}

			} else {
				throw new UserNotFoundException();
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Unable to get JWT Token");
			response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			responseBody.setResult("106");
			responseBody.setResultDescription("Unable to get JWT Token");
			response.getWriter().write(new Gson().toJson(responseBody));
		} catch (ExpiredJwtException e) {
			System.out.println("JWT Token has expired");
			responseBody.setResult("107");
			response.setStatus(HttpStatus.ACCEPTED.value());
			responseBody.setResultDescription("JWT Token has expired");
			response.getWriter().write(new Gson().toJson(responseBody));
		} catch (SignatureException e) {
			System.out.println("JWT signature does not match locally computed signature.");
			responseBody.setResult("108");
			response.setStatus(HttpStatus.ACCEPTED.value());
			responseBody.setResultDescription("JWT signature does not match locally computed signature.");
			response.getWriter().write(new Gson().toJson(responseBody));
		} catch (UserNotFoundException e) {
			System.out.println("JWT signature does not belong to any registered user.");
			responseBody.setResult("109");
			response.setStatus(HttpStatus.ACCEPTED.value());
			responseBody.setResultDescription("JWT signature does not belong to any registered user.");
			response.getWriter().write(new Gson().toJson(responseBody));
		} catch (MalformedJwtException e) {
			System.out.println("JWT signature does not belong to any registered user.");
			responseBody.setResult("109");
			response.setStatus(HttpStatus.ACCEPTED.value());
			responseBody.setResultDescription("JWT signature does not belong to any registered user.");
			response.getWriter().write(new Gson().toJson(responseBody));
		} finally {
			System.out.println("Response - " + responseBody);
		}
	}

	/*
	 * @Override protected boolean shouldNotFilter(HttpServletRequest request)
	 * throws ServletException { return new AntPathMatcher().match("/api/users/**",
	 * request.getServletPath()); }
	 */
}
