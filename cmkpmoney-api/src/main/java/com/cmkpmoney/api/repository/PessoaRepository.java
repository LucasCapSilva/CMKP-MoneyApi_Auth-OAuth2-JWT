package com.cmkpmoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.cmkpmoney.api.model.Pessoa;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
	public Page<Pessoa> findByNomeContaining(String nome, Pageable pageable);
}
