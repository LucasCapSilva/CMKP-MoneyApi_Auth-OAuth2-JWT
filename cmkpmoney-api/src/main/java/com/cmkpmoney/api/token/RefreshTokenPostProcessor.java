package com.cmkpmoney.api.token;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class RefreshTokenPostProcessor implements ResponseBodyAdvice<OAuth2AccessToken> {

	//Metodo para interceptar o refresh token e passar ele em um cookie seguro para não ser interceptado
	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		//Intercepta todas as requisições feitas pela class OAuth2AccessToken e só retorna true quando o nome do metodo for postAccessToken
		return returnType.getMethod().getName().equals("postAccessToken");
	}

	//passando o token para o cookie seguro
	@Override
	public OAuth2AccessToken beforeBodyWrite(OAuth2AccessToken body, MethodParameter returnType,
			MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType,
			ServerHttpRequest request, ServerHttpResponse response) {
		
		HttpServletRequest req = ((ServletServerHttpRequest) request).getServletRequest();
		HttpServletResponse resp = ((ServletServerHttpResponse) response).getServletResponse();
		
		DefaultOAuth2AccessToken token = (DefaultOAuth2AccessToken) body; //feito o cast para chamar o metodo removerRefresh...
																		// passando o objeto correto		
		String refreshToken = body.getRefreshToken().getValue();
		adicionarRefreshTokenNoCookie(refreshToken, req, resp);
		removerRefreshTokenDoBody(token);
		
		return body;
	}
	
	private void removerRefreshTokenDoBody(DefaultOAuth2AccessToken token) {
		token.setRefreshToken(null); // removenso o refresh cooke do body
	}
	
	private void adicionarRefreshTokenNoCookie(String refreshToken, HttpServletRequest req, HttpServletResponse resp) {
		Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken); // passando o valor para o cookie com a chave refreshtoken
		refreshTokenCookie.setHttpOnly(true); //O cookie será acessivel apenas por http
		refreshTokenCookie.setSecure(false); // TODO: Mudar para true em producao, segurança do cookie
		refreshTokenCookie.setPath(req.getContextPath() + "/oauth/token");// para qual caminho o token devera ser enviado
		refreshTokenCookie.setMaxAge(2592000);// quanto tempo o cookie ira expirar 30 dias neste caso
		resp.addCookie(refreshTokenCookie); // adicionando o cookie na resposta
	}

}
