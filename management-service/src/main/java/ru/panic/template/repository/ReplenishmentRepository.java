package ru.panic.template.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.panic.template.entity.Replenishment;

public interface ReplenishmentRepository extends JpaRepository<Replenishment, Long> {
}
