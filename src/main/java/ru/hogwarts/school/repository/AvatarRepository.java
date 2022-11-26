package ru.hogwarts.school.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.hogwarts.school.model.Avatar;
import ru.hogwarts.school.model.AvatarRecord;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
public interface AvatarRepository extends JpaRepository<Avatar, Long> {
    @Override
    Optional<Avatar> findById(Long id);

    Optional<Avatar> findByStudentId(Long id);
	
	Page<AvatarRecord> findAllBy(PageRequest pageRequest);
}
