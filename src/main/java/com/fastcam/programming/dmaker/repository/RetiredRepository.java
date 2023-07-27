package com.fastcam.programming.dmaker.repository;

import com.fastcam.programming.dmaker.entity.RetiredDeveloper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RetiredRepository extends JpaRepository<RetiredDeveloper, Long> {
}


