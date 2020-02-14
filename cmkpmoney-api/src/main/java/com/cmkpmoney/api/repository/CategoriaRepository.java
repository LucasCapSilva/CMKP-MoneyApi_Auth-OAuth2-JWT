package com.cmkpmoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmkpmoney.api.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

}
