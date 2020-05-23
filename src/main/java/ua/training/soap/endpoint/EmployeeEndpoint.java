package ua.training.soap.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import ua.training.soap.CreateEmployeeRequest;
import ua.training.soap.CreateEmployeeResponse;
import ua.training.soap.GetEmployeeRequest;
import ua.training.soap.GetEmployeeResponse;
import ua.training.soap.ObjectFactory;
import ua.training.soap.entity.Employee;
import ua.training.soap.entity.JobFunction;
import ua.training.soap.entity.Skill;
import ua.training.soap.exception.NoEntityFoundException;
import ua.training.soap.exception.NonUniqueObjectException;
import ua.training.soap.repository.EmployeeRepository;

@Endpoint
public class EmployeeEndpoint {
    private static final String NAMESPACE_URI = "http://soap.training.ua";

    @Autowired
    private EmployeeRepository employeeRepository;


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "getEmployeeRequest")
    @ResponsePayload
    public GetEmployeeResponse getEmployee(@RequestPayload GetEmployeeRequest request) {

        Employee employeeFromRepo = employeeRepository.findById((long) request.getId())
                .orElseThrow(() -> new NoEntityFoundException("There is no employee with provided id (" + request.getId() + ")"));

        ua.training.soap.Employee employee = convertEntityIntoXmlForSoap(employeeFromRepo);

        GetEmployeeResponse response = new ObjectFactory().createGetEmployeeResponse();
        response.setEmployee(employee);
        return response;
    }


    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "createEmployeeRequest")
    @ResponsePayload
    public CreateEmployeeResponse createEmployee(@RequestPayload CreateEmployeeRequest request) {

        Employee employeeToSaveIntoRepo = convertSoapXmlIntoEntity(request.getEmployee());

        try {
            Employee savedEmployee = employeeRepository.save(employeeToSaveIntoRepo);

            CreateEmployeeResponse response = new ObjectFactory().createCreateEmployeeResponse();
            response.setId(savedEmployee.getId().intValue());
            return response;
        } catch (DataIntegrityViolationException e) {
            throw new NonUniqueObjectException("Error creating employee. Employee with such name already exists. " + e.getMessage());
        }
    }


    private ua.training.soap.Employee convertEntityIntoXmlForSoap(Employee employeeFromRepo) {
        ObjectFactory objectFactory = new ObjectFactory();

        ua.training.soap.JobFunction jobFunction = ua.training.soap.JobFunction.fromValue(employeeFromRepo.getJobFunction().toString());

        ua.training.soap.PrimarySkill primarySkill = objectFactory.createPrimarySkill();
        primarySkill.setId(employeeFromRepo.getPrimarySkill().getId().intValue());
        primarySkill.setName(employeeFromRepo.getPrimarySkill().getName());

        ua.training.soap.Employee employee = objectFactory.createEmployee();
        employee.setId(employeeFromRepo.getId().intValue());
        employee.setFirstName(employeeFromRepo.getFirstName());
        employee.setLastName(employeeFromRepo.getLastName());
        employee.setEmail(employeeFromRepo.getEmail());
        employee.setJobFunction(jobFunction);
        employee.setPrimarySkill(primarySkill);
        return employee;
    }

    private Employee convertSoapXmlIntoEntity(ua.training.soap.Employee employeeFromRequest) {
        Skill primarySkill = new Skill();
        primarySkill.setId((long) employeeFromRequest.getPrimarySkill().getId());

        Employee employeeToSaveIntoRepo = new Employee();
        employeeToSaveIntoRepo.setFirstName(employeeFromRequest.getFirstName());
        employeeToSaveIntoRepo.setLastName(employeeFromRequest.getLastName());
        employeeToSaveIntoRepo.setEmail(employeeFromRequest.getEmail());
        employeeToSaveIntoRepo.setJobFunction(JobFunction.valueOf(employeeFromRequest.getJobFunction().value()));
        employeeToSaveIntoRepo.setPrimarySkill(primarySkill);

        return employeeToSaveIntoRepo;
    }
}
