package com.cmkpmoney.api.event;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationEvent;

//Criando um evento para que sempre que dispararmos alguma coisa gere o link de consulta no header
public class RecursoCriadoEvent extends ApplicationEvent{
    // classe herdade nessecissta de um serealizable.
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private Long codigo;

	//construtor de classo
	public RecursoCriadoEvent(Object source, HttpServletResponse response, Long codigo) {
		super(source);
		this.codigo = codigo;
		this.response = response;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public Long getCodigo() {
		return codigo;
	}

}
