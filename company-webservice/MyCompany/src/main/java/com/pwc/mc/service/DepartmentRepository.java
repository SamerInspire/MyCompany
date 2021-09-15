package com.pwc.mc.service;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.mc.model.DepartmentVO;

@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentVO, Long> {
	@Query(value = "SELECT * FROM department_tb d WHERE d.department_id = :departmentId", nativeQuery = true)
	List<DepartmentVO> findById(String departmentId);
}