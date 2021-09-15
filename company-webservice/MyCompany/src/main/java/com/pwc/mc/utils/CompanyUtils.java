package com.pwc.mc.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import com.google.gson.Gson;
import com.pwc.mc.model.UserVO;
import com.pwc.mc.model.WebResponseBody;
import com.pwc.mc.service.UserRepository;
import com.mysql.cj.util.StringUtils;

public class CompanyUtils {

	public static String checkUserValidation(UserVO user, UserRepository userRepository) {
		String resultDesc = "";

		/*
		 * ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		 * Validator validator = factory.getValidator();
		 * 
		 * Set<ConstraintViolation<UserVO>> violations = validator.validate(user);
		 * 
		 * 
		 * for (ConstraintViolation<UserVO> violation : violations) { resultDesc +=
		 * violation.getMessage()+" --- "; }
		 */
		if (StringUtils.isEmptyOrWhitespaceOnly(user.getEmail())) {
			resultDesc += " --- Email should not be Empity or Null !";
		}
		if (StringUtils.isEmptyOrWhitespaceOnly(user.getPassword())) {
			resultDesc += " --- Password should not be Empity or Null !";
		}
		if (StringUtils.isEmptyOrWhitespaceOnly(user.getName())) {
			resultDesc += " --- Name should not be Empity or Null !";
		}
		if (!StringUtils.isEmptyOrWhitespaceOnly(resultDesc)) {
			resultDesc = "Failed - Could not save the user " + resultDesc;
			resultDesc.substring(0, resultDesc.length() - 4);// remove the last ---
			return resultDesc;
		} else {
			resultDesc = userRepository.findByEmail(user.getEmail()) != null
					? "Failed - Could not save the user --- Email Already Registered !"
					: "";
			return resultDesc;
		}
	}

	public static ResponseEntity<String> responseGenerate(WebResponseBody responseBody) {
		HttpHeaders responseHeaders = new HttpHeaders();
		if (responseBody.getUser() != null) {
			responseHeaders.set("Authorization", "Bearer " + JwtTokenUtil.generateToken(responseBody.getUser()));
		}
		if (StringUtils.isEmptyOrWhitespaceOnly(responseBody.getResult())) {
			responseBody.setResult("-1");
			responseBody.setResultDescription("Internal Error");
		}
		return ResponseEntity.ok().headers(responseHeaders).body(new Gson().toJson(responseBody));

	}

	public static String removeFirstandLast(String str) {

		StringBuffer sb = new StringBuffer(str);
		if (str.length() != 0) {
			sb.deleteCharAt(0);
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}
}
