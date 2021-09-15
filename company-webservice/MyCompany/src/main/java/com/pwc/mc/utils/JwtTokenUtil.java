package com.pwc.mc.utils;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.function.Function;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.pwc.mc.model.UserVO;
import com.pwc.mc.service.UserRepository;
import com.mysql.cj.util.StringUtils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final byte[] SIGNING_KEY = "Samer".getBytes();
	private static final int ACCESS_TOKEN_VALIDITY_SECONDS = 42500;

	@Autowired
	private UserRepository userRepository;

	public String getEmailFromToken(String token) throws ExpiredJwtException {
		return getClaimFromToken(token, Claims::getSubject).split("---")[0];
	}
	public boolean getRoleFromToken(String token) throws ExpiredJwtException {
		return getClaimFromToken(token, Claims::getSubject).split("---")[1].equals("true");
	}

	public UserVO getUserFromToken(String jwtToken) {
		try {
			if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
				jwtToken = jwtToken.substring(7);
			} else {
				return new UserVO();
			}

			if (jwtToken != null && !StringUtils.isEmptyOrWhitespaceOnly(jwtToken)) {
				UserVO user = new UserVO();
				user = userRepository.findByEmail(getEmailFromToken(jwtToken));
				if (user != null) {
					return user;
				}
			}
		} catch (ExpiredJwtException eje) {
			System.out.println(eje.getMessage());
		}
		return null;
	}

	public Date getExpirationDateFromToken(String token) throws ExpiredJwtException {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) throws ExpiredJwtException {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	private Claims getAllClaimsFromToken(String token) throws ExpiredJwtException {
		return Jwts.parser().setSigningKey(SIGNING_KEY).parseClaimsJws(token).getBody();
	}

	private Boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	public static String generateToken(UserVO user) {
		return doGenerateToken(user.getEmail()+"---"+user.getIsManager());
	}

	private static String doGenerateToken(String subject) {

		Claims claims = Jwts.claims().setSubject(subject);
		// claims.put("scopes", Arrays.asList(new
		// SimpleGrantedAuthority("ROLE_ADMIN")));

		return Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY_SECONDS * 1000))
				.signWith(SignatureAlgorithm.HS256, SIGNING_KEY).compact();
	}

	public Boolean validateToken(String token) {
		if (StringUtils.isEmptyOrWhitespaceOnly(token))
			return false;
		final String email = getEmailFromToken(token);
		UserVO userDetails = this.userRepository.findByEmail(email);

		return (email.equals(userDetails.getEmail()) && !isTokenExpired(token));
	}

	static byte[] HmacSHA256(String data, byte[] key) throws Exception {
		String algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes("UTF-8"));
	}

	public static String encryptData(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	// generate token for user
	public String generateToken(UserDetails userDetails) {
		return doGenerateToken(userDetails.getUsername());
	}

}