package com.pwc.mc.service;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.pwc.mc.model.UserVO;

@Repository
public interface UserRepository extends JpaRepository<UserVO, Long> {
	@Query(value = "SELECT * FROM user_tb u WHERE LOWER(u.email) = LOWER(:email) and u.password=:password", nativeQuery = true)
	public UserVO findByEmailAndPassword(String email, String password);

	@Query(value = "SELECT * FROM user_tb u WHERE LOWER(u.email) = LOWER(:email)", nativeQuery = true)
	public UserVO findByEmail(String email);
	
	@Query(value = "SELECT * FROM user_tb u WHERE LOWER(u.name) = LOWER(:name) AND is_manager=false", nativeQuery = true)
	public List<UserVO> findEmployerByName(String name);
	
	@Query(value = "SELECT * FROM user_tb u WHERE LOWER(u.email) = LOWER(:email) AND is_manager=false", nativeQuery = true)
	public List<UserVO> findEmployerByEmail(String email);
	
	@Query(value = "SELECT u FROM UserVO u WHERE is_manager=false order by ?#{#pageRequest}", nativeQuery = false)
	public List<UserVO> findAllEmployees(Sort sort);

	@Query(value = "SELECT COALESCE(max(user_id),0) FROM user_tb", nativeQuery = true)
	public int getMaxUserId();
	
	@Query(value = "SELECT * FROM user_tb u WHERE u.department_id = :id", nativeQuery = true)
	List<UserVO> getUsersDepartment(Long id);
}