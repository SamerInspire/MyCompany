package com.pwc.mc.config;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pwc.mc.service.UserRepository;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final JwtTokenFilter jwtTokenFilter;

	public SecurityConfig(UserRepository userRepository, JwtTokenFilter jwtTokenFilter) {
		this.jwtTokenFilter = jwtTokenFilter;
	}
	// Details omitted for brevity

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Enable CORS and disable CSRF
		http = http.cors().and().csrf().disable();
		// Set session management to stateless
		http = http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and();

		// Set unauthorized requests exception handler
		http = http.exceptionHandling().authenticationEntryPoint((request, response, ex) -> {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, ex.getMessage());
		}).and();
		/*
		 * for the unauthorized access System.out.println("Unauthorized Access");
		 * responseBody.setComplaints(null); responseBody.setResult("110");
		 * responseBody.setResultDescription("Unauthorized Access");
		 * response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
		 * response.getWriter().write(new Gson().toJson(responseBody));
		 */

		// Set permissions on endpoints
		http.authorizeRequests()
				// Our public endpoints
				.antMatchers(HttpMethod.GET, "/api/projects/deleteProject/{\\d+}").hasRole("MANAGER")
				.antMatchers(HttpMethod.GET, "/api/projects").hasRole("MANAGER")
				.antMatchers(HttpMethod.POST, "/api/projects/createProject").hasRole("MANAGER")
				.antMatchers(HttpMethod.PUT, "/api/projects/{\\d+}").hasRole("MANAGER")

				.antMatchers(HttpMethod.GET, "/api/departments").hasRole("MANAGER")
				.antMatchers(HttpMethod.POST, "/api/departments/createDepartmnet").hasRole("MANAGER")
				.antMatchers(HttpMethod.GET, "/api/departments/deleteDepartment/{\\d+}").hasRole("MANAGER")
				.antMatchers(HttpMethod.PUT, "/api/departments/{\\d+}").hasRole("MANAGER")

				.antMatchers(HttpMethod.POST, "/api/users/findEmployees/{\\d+}").hasRole("MANAGER")
				.antMatchers(HttpMethod.GET, "/api/users/deleteUser/{\\d+}").hasRole("MANAGER")
				.antMatchers(HttpMethod.GET, "/api/users").hasRole("MANAGER")
				.antMatchers(HttpMethod.POST, "/api/users/signIn").permitAll()
				.antMatchers(HttpMethod.POST, "/api/users/registration").permitAll()
				.antMatchers(HttpMethod.POST, "/api/users/userProjectsManage").hasRole("MANAGER")
				.antMatchers(HttpMethod.GET, "/api/users/currentUserInfo").permitAll()
				.antMatchers(HttpMethod.PUT, "/api/users/{\\d+}").hasRole("MANAGER")
				.antMatchers(HttpMethod.PUT, "/api/users/employeeDepartment/{\\d+}").hasRole("MANAGER")

				.antMatchers("/**").denyAll().antMatchers("**").denyAll();

		// Our private endpoints

		// Add JWT token filter
		http.addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}