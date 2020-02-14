package com.cmkpmoney.api.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cmkpmoney.api.model.Lancamento;
import com.cmkpmoney.api.model.Pessoa;
import com.cmkpmoney.api.repository.LancamentoRepository;
import com.cmkpmoney.api.repository.PessoaRepository;
import com.cmkpmoney.api.service.exception.PessoaInexistenteOuInativaException;

@Service
public class LancamentoService {

	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	//regra de negocio que verifica se pessoa esta existente, caso não, salva
	public Lancamento salvarLancamento(Lancamento lancamento) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(lancamento.getPessoa().getCodigo());
		if(!pessoa.isPresent() || !pessoa.get().getAtivo())
			throw new PessoaInexistenteOuInativaException();
		
		return lancamentoRepository.save(lancamento);
	}

	
}
