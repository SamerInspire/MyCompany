package com.pwc.mc.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.mc.model.ProjectsUsersVO;

@Repository
public interface ProjectsUsersRepository extends JpaRepository<ProjectsUsersVO, Long> {
	
	@Query(value = "SELECT * FROM projects_users_tb u WHERE u.user_id = :userId", nativeQuery = true)
	List<ProjectsUsersVO> findUsersProjects(Long userId);
}