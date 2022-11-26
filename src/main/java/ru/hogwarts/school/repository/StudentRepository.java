package ru.hogwarts.school.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.hogwarts.school.model.Student;

import java.util.Collection;
import java.util.List;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Collection<Student> findByAge(int age);
    Collection<Student> findByAgeBetween(int minAge, int maxAge);
	List<Student> findAllBy();
	
	@Query(value="select count(*) as total from student", nativeQuery = true)
	Integer getTotalStudents();
	
	@Query(value="select avg(age) as average_age from student", nativeQuery = true)
	Double getAverageAgeOfStudents();
	
	@Query(value="select * from student order by id desc limit :number", nativeQuery = true)
	List<Student> getLastStudents(@Param("number") int studentsNumber);
}
