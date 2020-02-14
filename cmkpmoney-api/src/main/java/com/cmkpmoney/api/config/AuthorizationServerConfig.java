package com.cmkpmoney.api.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import com.cmkpmoney.api.config.token.CustomTokenEnhancer;

@Profile("oauth-security")
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	public void configure(ClientDetailsServiceConfigurer clients) throws Exception { 
		clients.inMemory()
			.withClient("Angular")//configurando o usuario client(angular, react, node, terão que acessar por este usuario)
			.secret("$2a$10$tBPoNG7a6sEjLzi27liOruqspFJvJaqH3AdhMqWfTL8.R7oKhIy7q")		//obs. isso não é a senha do usuario (admin, admin)
			.scopes("read", "write") //client pode ler e escrever na api
			.authorizedGrantTypes("password", "refresh_token") //grand_type da requisição
			.accessTokenValiditySeconds(1800) //definido o tempo de validade do da bearer (30 min)
			.refreshTokenValiditySeconds(3600 * 24)
		.and()
		.withClient("mobile")
		.secret("$2a$10$tBPoNG7a6sEjLzi27liOruqspFJvJaqH3AdhMqWfTL8.R7oKhIy7q")//angular encodado
		.scopes("read")
		.authorizedGrantTypes("password", "refresh_token")
		.accessTokenValiditySeconds(1800)
		.refreshTokenValiditySeconds(3600 * 24); 	
	}
	
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
		TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
		//metodo que passa o username para o token
		tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
		
		endpoints
			.tokenStore(tokenStore()) // definindo onde será quardado o token
			.tokenEnhancer(tokenEnhancerChain)
			.reuseRefreshTokens(false) // o refresh toque ira durar enquanto o usuario estiver logado
			.userDetailsService(this.userDetailsService)
			.authenticationManager(this.authenticationManager);
	}

	@Bean
	public JwtAccessTokenConverter accessTokenConverter() {
		JwtAccessTokenConverter accessTokenConverter = new JwtAccessTokenConverter();
		accessTokenConverter.setSigningKey("M@rc&lo_CMKP");//Passando a o assinatura para o token
		return accessTokenConverter;
	}

	private JwtTokenStore tokenStore() {
		//Diferente do outro projeto, o JWT não precisa ser guardado em memória
		return new JwtTokenStore(accessTokenConverter());
	}
	
	@Bean
	public TokenEnhancer tokenEnhancer() {
	    return new CustomTokenEnhancer();
	}
	
}
