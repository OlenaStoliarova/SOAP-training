package ua.training.soap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.soap.entity.Employee;


public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}
