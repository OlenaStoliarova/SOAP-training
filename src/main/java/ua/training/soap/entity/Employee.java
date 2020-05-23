package ua.training.soap.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Size;


@Entity
@Table(name = "employee", uniqueConstraints = {@UniqueConstraint(columnNames = {"firstName", "lastName"})})
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "firstName", nullable = false)
    private String firstName;
    @Column(name = "lastName", nullable = false)
    private String lastName;

    @Column(name = "email", nullable = false)
    @Size(min = 5, message = "Email can't be shorter than 5 characters")
    private String email;

    @Column(name = "jobFunction", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobFunction jobFunction;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "primarySkill", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Skill primarySkill;

    public Employee() {
    }

    public Employee(Long id, String firstName, String lastName, String email, JobFunction jobFunction, Skill primarySkill) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.jobFunction = jobFunction;
        this.primarySkill = primarySkill;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public JobFunction getJobFunction() {
        return jobFunction;
    }

    public void setJobFunction(JobFunction jobFunction) {
        this.jobFunction = jobFunction;
    }

    public Skill getPrimarySkill() {
        return primarySkill;
    }

    public void setPrimarySkill(Skill primarySkill) {
        this.primarySkill = primarySkill;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", jobFunction=" + jobFunction +
                ", primarySkill=" + primarySkill +
                '}';
    }
}
