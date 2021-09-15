package com.pwc.mc.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.pwc.mc.model.DepartmentVO;
import com.pwc.mc.model.WebResponseBody;
import com.pwc.mc.service.DepartmentRepository;
import com.pwc.mc.service.UserRepository;
import com.pwc.mc.utils.CompanyUtils;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/api/departments")
public class DepartmentsController {

	@Autowired
	private DepartmentRepository departmentRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping
	public ResponseEntity<String> findAllDepartments(@RequestHeader MultiValueMap<String, String> headers) {

		WebResponseBody responseBody = new WebResponseBody();

		//UserVO user = jwtTokenUtil.getUserFromToken(ComplaintUtils.removeFirstandLast(headers.get("authorization").get(0)));
		//System.out.println("user =-- " + user.toString());
		//if (user.getIsAdmin()) {
			responseBody.setDepartments((List<DepartmentVO>) departmentRepository.findAll());

		//} else {
		//}
		responseBody.setResult("0");
		responseBody.setResultDescription("Success");
		return CompanyUtils.responseGenerate(responseBody);
	}
	
	@PostMapping("/createDepartmnet")
	public ResponseEntity<String> createDepartmnet(@Validated @RequestBody DepartmentVO department) {

		WebResponseBody responseBody = new WebResponseBody();
		try {

			if (StringUtils.isEmptyOrWhitespaceOnly(department.getDepartmentName())) {
				responseBody.setResult("111");
				responseBody.setResultDescription("Failed -  Missing department information");
				return CompanyUtils.responseGenerate(responseBody);
			}

			departmentRepository.saveAndFlush(department);

			responseBody.setResult("0");
			responseBody.setResultDescription("Success");

		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}
	
	@GetMapping("/deleteDepartment/{id}")
	public ResponseEntity<String> deleteDepartment(@PathVariable Long id) {

		WebResponseBody responseBody = new WebResponseBody();
		Optional<DepartmentVO> departmentOptional = departmentRepository.findById(id);

		try {
			if (departmentOptional != null && !departmentOptional.isEmpty()) {
				DepartmentVO department = departmentOptional.get();
				if(userRepository.getUsersDepartment(id).isEmpty()) {
					departmentRepository.deleteById(id);
					responseBody.setResult("0");
					responseBody.setResultDescription("Success");
					responseBody.setDepartments(Arrays.asList(department));
				}else {
					responseBody.setResult("115 ");
					responseBody.setResultDescription("Failed - The Department with ID:"+id+" has employees");
				}
				
			} else {
				responseBody.setResult("114");
				responseBody.setResultDescription("Failed - Department not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}

		return CompanyUtils.responseGenerate(responseBody);
	}
	@PutMapping("/{id}")
	ResponseEntity<String> editDepartment(@RequestBody DepartmentVO newDepartment, @PathVariable Long id,
			@RequestHeader MultiValueMap<String, String> headers) {
		System.out.println("newDepartment ===>" + newDepartment);
		WebResponseBody responseBody = new WebResponseBody();

		try {
			System.out.println("newDepartment ===>" + newDepartment);

			Optional<DepartmentVO> departmentOptional = departmentRepository.findById(id);
			if (departmentOptional != null && !departmentOptional.isEmpty()) {
				DepartmentVO department = departmentOptional.get();
				if (!StringUtils.isEmptyOrWhitespaceOnly(newDepartment.getDepartmentName())) {
					department.setDepartmentName(newDepartment.getDepartmentName());
				}
				departmentRepository.save(department);
				responseBody.setDepartments(Arrays.asList(department));
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
			} else {
				responseBody.setResult("114");
				responseBody.setResultDescription("Failed - Department not found");
			}
		} catch (Exception e) {
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}
}
