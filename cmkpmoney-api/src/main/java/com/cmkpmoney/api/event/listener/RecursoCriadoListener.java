package com.cmkpmoney.api.event.listener;

import java.net.URI;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.cmkpmoney.api.event.RecursoCriadoEvent;

//Esta classe é responsavel por ficar escutando o evento Recurso Criado evendo

@Component
public class RecursoCriadoListener implements ApplicationListener<RecursoCriadoEvent> {

	@Override
	public void onApplicationEvent(RecursoCriadoEvent event) {
		//HttpServletResponse classe que acessa o header
		HttpServletResponse response = event.getResponse();
		Long codigo = event.getCodigo();

		adcionarHeaderLocation(response, codigo);
	}

	private void adcionarHeaderLocation(HttpServletResponse response, Long codigo) {
		
		// Montando um uri (apartir da requisição atual 'fromCurrentRequestUri()')da
		// path criada
		// para consutar a categoria criada para a resposta via header
		URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.path("/{codigo}").buildAndExpand(codigo).toUri();

		// ultilizando o response para inserir o path no location do header
		response.setHeader("Location", uri.toASCIIString());
	}

}
