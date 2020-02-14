package com.cmkpmoney.api.exceptionhandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.cmkpmoney.api.service.exception.PessoaInexistenteOuInativaException;

@ControllerAdvice
public class MoneyExceptionHandler extends ResponseEntityExceptionHandler {

	// injetando o serviço que se comuniva com messages.properties
	@Autowired
	private MessageSource messageSource;

	// fazendo um sub-escrita no metodo para retornar as minhas mensagens de erro
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		// Pegando mensagens do arquivo Messages.properties
		String messageUsuario = messageSource.getMessage("mensagem.invalida", null, LocaleContextHolder.getLocale());
		String messageDeveloper = ex.getCause() != null ? ex.getCause().toString() : ex.toString();
		List<Error> erros = Arrays.asList(new Error(messageUsuario, messageDeveloper));
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Error> erros = criarListaDeError(ex.getBindingResult());
		return handleExceptionInternal(ex, erros, headers, HttpStatus.BAD_REQUEST, request);
	}

	// Criando a minha própria excessão
	// @ExceptionHandler recebe o erro.class com atributo para observar aquele erro
	// específico
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	private ResponseEntity<Object> handleEmptyResultDataAccessException(EmptyResultDataAccessException ex,
			WebRequest request) {

		// (recurso.nao-encontrado) => é um paramentro definido la no
		// messages.properties
		String messageUsuario = messageSource.getMessage("recurso.nao-encontrado", null,
				LocaleContextHolder.getLocale());
		String messageDeveloper = ex.toString();
		List<Error> erros = Arrays.asList(new Error(messageUsuario, messageDeveloper));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
	}

	@ExceptionHandler({ DataIntegrityViolationException.class })
	private ResponseEntity<Object> handleDataIntegrityViolationException(DataIntegrityViolationException ex,
			WebRequest request) {

		// (recurso.nao-encontrado) => é um paramentro definido la no
		// messages.properties
		String messageUsuario = messageSource.getMessage("recurso.operacao-nao-permitida", null,
				LocaleContextHolder.getLocale());
		String messageDeveloper = ExceptionUtils.getRootCauseMessage(ex);// add pom xml commons 3
		List<Error> erros = Arrays.asList(new Error(messageUsuario, messageDeveloper));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);

	}

	//erro disparado para pessoa inexistente
	@ExceptionHandler({PessoaInexistenteOuInativaException.class})
	private ResponseEntity<Object> handlePessoaInexistenteOuInativaException(PessoaInexistenteOuInativaException ex, WebRequest request){
		String messageUsuario = messageSource.getMessage("pessoa.inexistente-ou-inativa", null,
				LocaleContextHolder.getLocale());
		String messageDeveloper = ex.getCause() != null ? ex.getCause().toString() : ex.toString();// add pom xml commons 3
		List<Error> erros = Arrays.asList(new Error(messageUsuario, messageDeveloper));
		return handleExceptionInternal(ex, erros, new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
	}
	
	// classe que monta uma lista de erros
	private List<Error> criarListaDeError(BindingResult bindingResult) {
		List<Error> erros = new ArrayList<Error>();

		// Devolve todos os erros localizados ao preecher as entidades
		for (FieldError fieldError : bindingResult.getFieldErrors()) {
			String messageUsuario = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String messageDeveloper = fieldError.toString();
			erros.add(new Error(messageUsuario, messageDeveloper));
		}
		return erros;
	}

	// casse apenas para montar o layout do json quando disparar a mensegem.
	public static class Error {

		private String messageUsuario;
		private String messageDeveloper;

		public Error(String messageUsuario, String messageDeveloper) {
			super();
			this.messageUsuario = messageUsuario;
			this.messageDeveloper = messageDeveloper;
		}

		public String getMessageUsuario() {
			return messageUsuario;
		}

		public String getMessageDeveloper() {
			return messageDeveloper;
		}

	}
}
