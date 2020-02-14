package com.cmkpmoney.api.resourse;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cmkpmoney.api.event.RecursoCriadoEvent;
import com.cmkpmoney.api.model.Lancamento;
import com.cmkpmoney.api.repository.LancamentoRepository;
import com.cmkpmoney.api.repository.filter.LancamentoFilter;
import com.cmkpmoney.api.repository.projection.ResumoLancamento;
import com.cmkpmoney.api.service.LancamentoService;

@RestController
@RequestMapping("/lancamentos")
public class LancamentoResourse {
	
	@Autowired
	private LancamentoRepository lancamentoRepository;
	
	@Autowired
	private LancamentoService lancamentoService;
	
	@Autowired //injetando "publicador" de eventos
	private ApplicationEventPublisher publisher;

	@GetMapping //recebe lancamentoFilter e pageable caso forem passados
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Page<Lancamento>> pesquisar(LancamentoFilter lancamentoFilter, Pageable pageable){
		return ResponseEntity.ok(lancamentoRepository.filtrar(lancamentoFilter, pageable));
	}
	
	@GetMapping(params = "resumo") // com o parametro resumo ele diferencia do metodo acima
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Page<ResumoLancamento>> resumir(LancamentoFilter lancamentoFilter, Pageable pageable){
		return ResponseEntity.ok(lancamentoRepository.resumir(lancamentoFilter, pageable));
	}
	
	@GetMapping("/{id}")
	@PreAuthorize("hasAuthority('ROLE_PESQUISAR_LANCAMENTO') and #oauth2.hasScope('read')")
	public ResponseEntity<Lancamento> listarPorCodigo(@PathVariable long id){
		return lancamentoRepository.findById(id).map(resp -> ResponseEntity.ok(resp))
				.orElse(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	@PreAuthorize("hasAuthority('ROLE_CADASTRAR_LANCAMENTO') and #oauth2.hasScope('write')")
	public ResponseEntity<Lancamento> cadastrarLancamento(@Valid @RequestBody Lancamento lancamento, HttpServletResponse response){
		Lancamento lancamentoSalvo = lancamentoService.salvarLancamento(lancamento);
		// Chamando o Evento que é responsavel pelo Location
		publisher.publishEvent(new RecursoCriadoEvent(this, response, lancamentoSalvo.getCodigo()));
		return ResponseEntity.status(HttpStatus.CREATED).body(lancamentoSalvo);
	}
	
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	@PreAuthorize("hasAuthority('ROLE_REMOVER_LANCAMENTO') and #oauth2.hasScope('write')")
	public void remover(@PathVariable long id) {
		lancamentoRepository.deleteById(id);
	}
	
}
