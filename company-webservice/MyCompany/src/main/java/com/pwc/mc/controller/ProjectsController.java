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
import com.pwc.mc.model.ProjectVO;
import com.pwc.mc.model.WebResponseBody;
import com.pwc.mc.service.ProjectRepository;
import com.pwc.mc.utils.CompanyUtils;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "*")
@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

	@Autowired
	private ProjectRepository projectRepository;

	@GetMapping
	public ResponseEntity<String> findAllDepartments(@RequestHeader MultiValueMap<String, String> headers) {

		WebResponseBody responseBody = new WebResponseBody();

		// UserVO user =
		// jwtTokenUtil.getUserFromToken(ComplaintUtils.removeFirstandLast(headers.get("authorization").get(0)));
		// System.out.println("user =-- " + user.toString());
		// if (user.getIsAdmin()) {
		responseBody.setProjects((List<ProjectVO>) projectRepository.findAll());

		// } else {
		// }
		responseBody.setResult("0");
		responseBody.setResultDescription("Success");
		return CompanyUtils.responseGenerate(responseBody);
	}

	@PostMapping("/createProject")
	public ResponseEntity<String> createProject(@Validated @RequestBody ProjectVO project) {

		WebResponseBody responseBody = new WebResponseBody();
		try {

			if (StringUtils.isEmptyOrWhitespaceOnly(project.getProjectName())) {
				responseBody.setResult("112");
				responseBody.setResultDescription("Failed - Missing Project Information");
				return CompanyUtils.responseGenerate(responseBody);
			}

			projectRepository.saveAndFlush(project);

			responseBody.setResult("0");
			responseBody.setResultDescription("Success");

		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}

	@GetMapping("/deleteProject/{id}")
	public ResponseEntity<String> deleteProject(@PathVariable Long id) {

		WebResponseBody responseBody = new WebResponseBody();
		Optional<ProjectVO> userOptional = projectRepository.findById(id);

		try {
			if (userOptional != null && !userOptional.isEmpty()) {
				ProjectVO project = userOptional.get();
				projectRepository.deleteById(id);
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
				responseBody.setProjects(Arrays.asList(project));
			} else {
				responseBody.setResult("113");
				responseBody.setResultDescription("Failed - Project not found");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return CompanyUtils.responseGenerate(responseBody);
		}

		return CompanyUtils.responseGenerate(responseBody);
	}

	@PutMapping("/{id}")
	ResponseEntity<String> editProject(@RequestBody ProjectVO newProject, @PathVariable Long id,
			@RequestHeader MultiValueMap<String, String> headers) {
		System.out.println("newProject ===>" + newProject);
		WebResponseBody responseBody = new WebResponseBody();

		try {
			System.out.println("newProject ===>" + newProject);

			Optional<ProjectVO> projectOptional = projectRepository.findById(id);
			if (projectOptional != null && !projectOptional.isEmpty()) {
				ProjectVO project = projectOptional.get();
				if (!StringUtils.isEmptyOrWhitespaceOnly(newProject.getProjectName())) {
					project.setProjectName(newProject.getProjectName());
				}
				projectRepository.save(project);
				responseBody.setProjects(Arrays.asList(project));
				responseBody.setResult("0");
				responseBody.setResultDescription("Success");
			} else {
				responseBody.setResult("113");
				responseBody.setResultDescription("Failed - Project not found");
			}
		} catch (Exception e) {
			return CompanyUtils.responseGenerate(responseBody);
		}
		return CompanyUtils.responseGenerate(responseBody);
	}
}
