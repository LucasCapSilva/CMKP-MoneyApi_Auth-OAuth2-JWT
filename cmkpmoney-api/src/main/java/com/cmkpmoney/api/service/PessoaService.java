package com.cmkpmoney.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.cmkpmoney.api.model.Pessoa;
import com.cmkpmoney.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;
	
	//metodo Atualizar pessoa
	public Pessoa atualizar(Long id, Pessoa pessoa) {
		
		Pessoa pessoaSalva = buscarPessoalSalvaPeloCodigo(id);
		BeanUtils.copyProperties(pessoa, pessoaSalva, "codigo");
		
		return pessoaRepository.save(pessoaSalva);		
	}	

	//metodo que apenas atualiza se pessoa é ativa ou não
	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalva = buscarPessoalSalvaPeloCodigo(codigo);
		pessoaSalva.setAtivo(ativo);
		pessoaRepository.save(pessoaSalva);
	}
	
	//retorna se pessoa existe ou não para o metodo de atualizar
	public Pessoa buscarPessoalSalvaPeloCodigo(Long id) {
		Pessoa pessoaSalva = pessoaRepository.findById(id)
				.orElseThrow(() -> new EmptyResultDataAccessException(1));
		return pessoaSalva;
	}
}
