package com.vocatio.repository;

import com.vocatio.model.Testimonio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestimonioRepository extends JpaRepository<Testimonio, Long> {
    List<Testimonio> findAllByCarreraId(Long carreraId);
}

