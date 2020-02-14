package com.cmkpmoney.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmkpmoney.api.model.Lancamento;
import com.cmkpmoney.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

}
