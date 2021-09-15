package com.pwc.mc.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.mc.model.ProjectVO;

@Repository
public interface ProjectRepository extends JpaRepository<ProjectVO, Long> {
	@Query(value = "SELECT p.* FROM projects_users_tb pu, project_tb p WHERE pu.user_id = :userId AND pu.project_id = p.project_id", nativeQuery = true)
	public List<ProjectVO> findByUserId(long userId);
}