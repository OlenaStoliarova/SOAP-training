package ua.training.soap.endpoint;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.dao.DataIntegrityViolationException;
import ua.training.soap.CreateEmployeeRequest;
import ua.training.soap.CreateEmployeeResponse;
import ua.training.soap.GetEmployeeRequest;
import ua.training.soap.GetEmployeeResponse;
import ua.training.soap.ObjectFactory;
import ua.training.soap.PrimarySkill;
import ua.training.soap.entity.Employee;
import ua.training.soap.entity.JobFunction;
import ua.training.soap.entity.Skill;
import ua.training.soap.exception.NoEntityFoundException;
import ua.training.soap.exception.NonUniqueObjectException;
import ua.training.soap.repository.EmployeeRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmployeeEndpointTest {
    private static final Long ID = 1L;
    private static final Long NON_EXISTING_ID = 0L;
    private static final String NAME = "Name";
    private static final String EMAIL = "email@mail.ua";
    private static final JobFunction TEST_JOB_FUNCTION = JobFunction.DEVELOPER;
    private static final Skill TEST_PRIMARY_SKILL = new Skill(1L, "Java");

    @InjectMocks
    private EmployeeEndpoint instance;

    @Mock
    private EmployeeRepository employeeRepository;

    private Employee employee;
    private ua.training.soap.Employee soapXmlEmployee;

    @Before
    public void init() {
        employee = new Employee();
        employee.setId(ID);
        employee.setFirstName(NAME);
        employee.setLastName(NAME);
        employee.setEmail(EMAIL);
        employee.setJobFunction(TEST_JOB_FUNCTION);
        employee.setPrimarySkill(TEST_PRIMARY_SKILL);

        ObjectFactory objectFactory = new ObjectFactory();
        soapXmlEmployee = objectFactory.createEmployee();
        soapXmlEmployee.setId(ID.intValue());
        soapXmlEmployee.setFirstName(NAME);
        soapXmlEmployee.setLastName(NAME);
        soapXmlEmployee.setEmail(EMAIL);
        soapXmlEmployee.setJobFunction(ua.training.soap.JobFunction.fromValue(TEST_JOB_FUNCTION.toString()));

        PrimarySkill primarySkill = objectFactory.createPrimarySkill();
        primarySkill.setId(TEST_PRIMARY_SKILL.getId().intValue());
        primarySkill.setName(TEST_PRIMARY_SKILL.getName());
        soapXmlEmployee.setPrimarySkill(primarySkill);
    }

    @Test
    public void getEmployeeShouldReturnEmployeeSoapXmlResponceWhenRequestedIdFound() {
        when(employeeRepository.findById(ID)).thenReturn(Optional.of(employee));

        ObjectFactory objectFactory = new ObjectFactory();
        GetEmployeeRequest request = objectFactory.createGetEmployeeRequest();
        request.setId(ID.intValue());

        GetEmployeeResponse response = instance.getEmployee(request);

        assertThat(response.getEmployee().getId()).isEqualTo(employee.getId().intValue());
        assertThat(response.getEmployee().getFirstName()).isEqualTo(employee.getFirstName());
        assertThat(response.getEmployee().getEmail()).isEqualTo(employee.getEmail());
        assertThat(response.getEmployee().getJobFunction().value()).isEqualTo(employee.getJobFunction().toString());
        assertThat(response.getEmployee().getPrimarySkill().getName()).isEqualTo(employee.getPrimarySkill().getName());
    }

    @Test(expected = NoEntityFoundException.class)
    public void getEmployeeShouldThrowNoEntityFoundExceptionWhenRequestedIdNotFound() {
        when(employeeRepository.findById(NON_EXISTING_ID)).thenReturn(Optional.empty());

        ObjectFactory objectFactory = new ObjectFactory();
        GetEmployeeRequest request = objectFactory.createGetEmployeeRequest();
        request.setId(NON_EXISTING_ID.intValue());

        instance.getEmployee(request);
    }


    @Test
    public void createEmployeeShouldReturnEmployeeIdWhenCreateSuccess(){
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        ObjectFactory objectFactory = new ObjectFactory();
        CreateEmployeeRequest request = objectFactory.createCreateEmployeeRequest();
        request.setEmployee(soapXmlEmployee);

        CreateEmployeeResponse response = instance.createEmployee(request);

        assertThat(response.getId()).isEqualTo(employee.getId().intValue());
    }


    @Test(expected = NonUniqueObjectException.class)
    public void createEmployeeShouldThrowNonUniqueObjectExceptionWhenCreatingDuplicate() {
        when(employeeRepository.save(any(Employee.class))).thenThrow(new DataIntegrityViolationException("constraint violation"));

        ObjectFactory objectFactory = new ObjectFactory();
        CreateEmployeeRequest request = objectFactory.createCreateEmployeeRequest();
        request.setEmployee(soapXmlEmployee);

        instance.createEmployee(request);
    }
}
