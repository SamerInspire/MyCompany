package com.pwc.mc.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.util.StringUtils;
import com.pwc.mc.model.ProjectsUsersVO;
import com.pwc.mc.model.UserVO;
import com.pwc.mc.model.WebResponseBody;
import com.pwc.mc.service.DepartmentRepository;
import com.pwc.mc.service.ProjectRepository;
import com.pwc.mc.service.ProjectsUsersRepository;
import com.pwc.mc.service.UserRepository;
import com.pwc.mc.utils.CompanyUtils;
import com.pwc.mc.utils.JwtTokenUtil;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/api/users")
public class UsersController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ProjectsUsersRepository projectsUsersRepository;
	@Autowired
	private ProjectRepository projectRepository;
	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@GetMapping
	public ResponseEntity<String> findAllUsers(@RequestHeader MultiValueMap<String, String> headers) {

		WebResponseBody responseBody = new WebResponseBody();

		responseBody.setUsers((List<UserVO>) userRepository.findAll());
		responseBody.setResult("0");
		responseBody.setResultDescription("Success");
		return CompanyUtils.responseGenerate(responseBody);

	}

	@PostMapping("/findEmployees/{orderBy}")
	public ResponseEntity<String> findEmplyersWithFilter(@RequestHeader MultiValueMap<String, String> headers,
			@Validated @RequestBody UserVO requestedEmployees, @PathVariable("orderBy") String orderBy) {

		WebResponseBody responseBody = new WebResponseBody();
		List<UserVO> employers = new ArrayList<UserVO>();

		if (!StringUtils.isNullOrEmpty(requestedEmployees.getName())) {
			employers = userRepository.findEmployerByName(requestedEmployees.getName());
		} else if (!StringUtils.isNullOrEmpty(requestedEmployees.getEmail())) {
			employers = userRepository.findEmployerByEmail(requestedEmployees.getEmail());
		} else {
			orderBy = orderBy != null ? orderBy.toLowerCase() : "user_id";
			if (orderBy.startsWith("department")) {
				orderBy = orderBy.contains("Id") ? orderBy : orderBy + "Id";
				employers = userRepository.findAllEmployees(Sort.by(Sort.Direction.ASC, orderBy));
			} else {
				orderBy = "userId";
				employers = userRepository.findAllEmployees(Sort.by(Sort.Direction.ASC, orderBy));
			}
		}

		responseBody.setUsers(employers);
		responseBody.setResult("0");
		responseBody.setResultDescription("Success");
		return CompanyUtils.responseGenerate(responseBody);

	}

	@PostMapping("/registration")
	public ResponseEntity<String> registration(@Validated @RequestBody UserVO user) {

		WebResponseBody responseBody = new WebResponseBody();
		try {
			responseBody.setResultDescription(CompanyUtils.checkUserValidation(user, userRepository));

			if (!StringUtils.isEmptyOrWhitespaceOnly(responseBody.getResultDescription())) {
				responseBody.setResult("103");
				return CompanyUtils.responseGenerate(responseBody);
			}

			user.setIsManager(user.getIsManager());
			user.setUserId(userRepository.getMaxUserId() + 1);
			user.setPassword(JwtTokenUtil.encryptData(user.getPassword()));
			userRepository.saveAndFlush(user);

			responseBody.setUser(user);

			responseBody.setResult("0");
			responseBody.setResultDescription("Success");

		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);

	}

	@GetMapping("/currentUserInfo")
	public ResponseEntity<String> currentUser(@RequestHeader MultiValueMap<String, String> headers) {
		WebResponseBody responseBody = new WebResponseBody();
		try {
			String token = headers.get("authorization") != null ? headers.get("authorization").get(0) : "";
			token = token.startsWith("\"") ? CompanyUtils.removeFirstandLast(token) : token;
			UserVO user = jwtTokenUtil.getUserFromToken(token);
			if (user != null && user.getUserId() != 0) {
				responseBody.setUser(user);
				responseBody.setProjects(projectRepository.findByUserId(user.getUserId()));
				responseBody.setDepartments(departmentRepository.findById(user.getDepartmentId()));
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
			} else {
				responseBody.setResult("101");
				responseBody.setResultDescription("Failed - Invalied JWT Token");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}

	@PostMapping("/signIn")
	public ResponseEntity<String> signIn(@RequestBody UserVO user) {

		WebResponseBody responseBody = new WebResponseBody();

		try {
			user.setPassword(JwtTokenUtil.encryptData(user.getPassword()));
			user = userRepository.findByEmailAndPassword(user.getEmail(), user.getPassword());

			if (user != null) {
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
				responseBody.setUser(user);
			} else {
				responseBody.setResult("102");
				responseBody.setResultDescription("Failed - User/Password not correct");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}

		return CompanyUtils.responseGenerate(responseBody);
	}

	@GetMapping("/deleteUser/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {

		WebResponseBody responseBody = new WebResponseBody();
		Optional<UserVO> userOptional = userRepository.findById(id);

		try {
			if (userOptional != null && !userOptional.isEmpty()) {
				UserVO user = userOptional.get();
				userRepository.deleteById(id);
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
				responseBody.setUser(user);
			} else {
				responseBody.setResult("105");
				responseBody.setResultDescription("Failed - User not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}

		return CompanyUtils.responseGenerate(responseBody);
	}

	@PutMapping("/{id}")
	ResponseEntity<String> editEmployee(@RequestBody UserVO newUser, @PathVariable Long id,
			@RequestHeader MultiValueMap<String, String> headers) {
		WebResponseBody responseBody = new WebResponseBody();

		try {

			Optional<UserVO> userOptional = userRepository.findById(id);
			if (userOptional != null && !userOptional.isEmpty()) {
				System.out.println("New User ===>" + newUser);
				UserVO user = userOptional.get();
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getName())) {
					user.setName(newUser.getName());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getEmail())) {
					user.setEmail(newUser.getEmail());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getPassword())) {
					user.setPassword(JwtTokenUtil.encryptData(newUser.getPassword()));
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getPhoneNo())) {
					user.setPhoneNo(newUser.getPhoneNo());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getGender())) {
					user.setGender(newUser.getGender());
				}
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getAddress())) {
					user.setAddress(newUser.getAddress());
				}
				userRepository.save(user);
				responseBody.setUser(user);
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
			} else {
				responseBody.setResult("105");
				responseBody.setResultDescription("Failed - User not found");
			}
		} catch (Exception e) {
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}

	@PutMapping("employeeDepartment/{id}")
	ResponseEntity<String> changeEmployeeDepartment(@RequestBody UserVO newUser, @PathVariable Long id,
			@RequestHeader MultiValueMap<String, String> headers) {
		WebResponseBody responseBody = new WebResponseBody();

		try {
			Optional<UserVO> userOptional = userRepository.findById(id);
			if (userOptional != null && !userOptional.isEmpty()) {
				UserVO user = userOptional.get();
				if (!StringUtils.isEmptyOrWhitespaceOnly(newUser.getDepartmentId())
						&& user.getDepartmentId() != newUser.getDepartmentId()) {
					user.setDepartmentId(newUser.getDepartmentId());
					userRepository.save(user);
					responseBody.setUser(user);
				}
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
			} else {
				responseBody.setResult("105");
				responseBody.setResultDescription("Failed - User not found");
			}
		} catch (Exception e) {
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}

	@PostMapping("userProjectsManage")
	ResponseEntity<String> projectAssign(@RequestBody ProjectsUsersVO projectsUsers,
			@RequestHeader MultiValueMap<String, String> headers) {
		WebResponseBody responseBody = new WebResponseBody();
		try {
			if (projectsUsers.getUserId() == 0 || projectsUsers.getProjectId() == 0) {
				responseBody.setResult("116");
				responseBody.setResultDescription("Failed - Please provide the full information to assigne");
				return CompanyUtils.responseGenerate(responseBody);
			}

			List<ProjectsUsersVO> registeredUserProjects = projectsUsersRepository
					.findUsersProjects(projectsUsers.getUserId());
			List<ProjectsUsersVO> filteredToRequestedProject = registeredUserProjects.stream()
					.filter(record -> record.getProjectId() == projectsUsers.getProjectId())
					.collect(Collectors.toList());
			if (filteredToRequestedProject.isEmpty()) {// assigne process
				if (userRepository.findById(projectsUsers.getUserId()).isEmpty()) {
					responseBody.setResult("105");
					responseBody.setResultDescription("Failed - User not found");
					return CompanyUtils.responseGenerate(responseBody);
				}

				if (projectRepository.findById(projectsUsers.getProjectId()).isEmpty()) {
					responseBody.setResult("113");
					responseBody.setResultDescription("Failed - Project not found");
					return CompanyUtils.responseGenerate(responseBody);
				}
				projectsUsersRepository.save(projectsUsers);
				responseBody.setResultDescription("Success - assigne");
			} else { // unassign project

				projectsUsersRepository.delete(filteredToRequestedProject.get(0));
				responseBody.setResultDescription("Success - unassign");
			}
			responseBody.setResult("0");
		} catch (Exception e) {
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}
}
