package ua.training.soap.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.training.soap.entity.Skill;

public interface SkillRepository extends JpaRepository<Skill, Long> {
}
