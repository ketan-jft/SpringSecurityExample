package com.domain

import grails.converters.JSON
import grails.plugin.springsecurity.SpringSecurityUtils
import grails.web.RequestParameter
import org.springframework.security.access.annotation.Secured
import org.springframework.security.authentication.AccountExpiredException
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.context.SecurityContextHolder as SCH
import org.springframework.security.web.WebAttributes

import javax.servlet.http.HttpServletResponse
import grails.web.RequestParameter
@Secured('permitAll')
class LoginController {

	/**
	 * Dependency injection for the authenticationTrustResolver.
	 */
	def authenticationTrustResolver

	/**
	 * Dependency injection for the springSecurityService.
	 */
	def springSecurityService

	/**
	 * Default action; redirects to 'defaultTargetUrl' if logged in, /login/auth otherwise.
	 */
	def index() {
		if (springSecurityService.isLoggedIn()) {
			redirect uri: SpringSecurityUtils.securityConfig.successHandler.defaultTargetUrl
		}
		else {
			redirect action: 'auth', params: params
		}
	}

	/**
	 * Show the login page.
	 */
	def auth() {

		def config = SpringSecurityUtils.securityConfig

		if (springSecurityService.isLoggedIn()) {
			redirect uri: config.successHandler.defaultTargetUrl
			return
		}

		String view = 'auth'
		String postUrl = "${request.contextPath}${config.apf.filterProcessesUrl}"
		render view: view, model: [postUrl: postUrl,
		                           rememberMeParameter: config.rememberMe.parameter]
	}

	/**
	 * The redirect action for Ajax requests.
	 */
	def authAjax() {
		response.setHeader 'Location', SpringSecurityUtils.securityConfig.auth.ajaxLoginFormUrl
		response.sendError HttpServletResponse.SC_UNAUTHORIZED
	}

	/**
	 * Show denied page.
	 */
	def denied() {
		if (springSecurityService.isLoggedIn() &&
				authenticationTrustResolver.isRememberMe(SCH.context?.authentication)) {
			// have cookie but the page is guarded with IS_AUTHENTICATED_FULLY
			redirect action: 'full', params: params
		}
	}


	/**
	 * Login page for users with a remember-me cookie but accessing a IS_AUTHENTICATED_FULLY page.
	 */
	def full() {
		def config = SpringSecurityUtils.securityConfig
		render view: 'auth', params: params,
			model: [hasCookie: authenticationTrustResolver.isRememberMe(SCH.context?.authentication),
			        postUrl: "${request.contextPath}${config.apf.filterProcessesUrl}"]
	}

	/**
	 * Callback after a failed login. Redirects to the auth page with a warning message.
	 */
	def authfail() {

		String msg = ''
		def exception = session[WebAttributes.AUTHENTICATION_EXCEPTION]
		if (exception) {
			if (exception instanceof AccountExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.expired")
			}
			else if (exception instanceof CredentialsExpiredException) {
				msg = g.message(code: "springSecurity.errors.login.passwordExpired")
			}
			else if (exception instanceof DisabledException) {
				msg = g.message(code: "springSecurity.errors.login.disabled")
			}
			else if (exception instanceof LockedException) {
				msg = g.message(code: "springSecurity.errors.login.locked")
			}
			else {
				msg = g.message(code: "springSecurity.errors.login.fail")
			}
		}

		if (springSecurityService.isAjax(request)) {
			render([error: msg] as JSON)
		}
		else {
			flash.message = msg
			redirect action: 'auth', params: params
		}
	}

	/**
	 * The Ajax success redirect url.
	 */
	def ajaxSuccess() {
		render([success: true, username: springSecurityService.authentication.name] as JSON)
	}

	/**
	 * The Ajax denied redirect url.
	 */
	def ajaxDenied() {
		render([error: 'access denied'] as JSON)
	}

	String alphabet = (('A'..'N')+('P'..'Z')+('a'..'k')+('m'..'z')+('2'..'9')).join()

	def n = 40

	def giveMeKey(){
		def key
		for(def i=0;i<50;i++){
			key = new Random().with {
				(1..n).collect { alphabet[ nextInt( alphabet.length() ) ] }.join()
			}
		}
		return key
	}

	def forgetPassword(){}
	def resetPassword(){}
	def mailService
	def sendPassword(String username) {
		if (username) {
			def query = User.where {
				emailId == username
			}
			int total = query.updateAll(sessionId: giveMeKey())
			def users=User.findAllByEmailId("${username}")
			String pass=users.password
			String sessionId=users.sessionId.remove(0)
			mailService.sendMail {
				to params.username
				subject "Notifications"
            text """Hi, ${params.username} ! To Reset your password, click this link http://localhost:8080/SSLTest/login/resetPassword?sessionId=${sessionId}"""
			}
		}
		redirect(controller: "login", action: "forgetPassword")
	}
	def setPassword(String sessId,String newpass,String conpass){
		try {
			int total, delete
			String username,emailId
			if (sessId != null) {
				def users = User.findAllBySessionId(sessId)
				username = users.username.remove(0)
				emailId = users.emailId.remove(0)
				String password = springSecurityService?.passwordEncoder ? springSecurityService.encodePassword(newpass) : newpass
				if (username != null) {
					if (newpass == conpass) {
						def query = User.where {
							username == username
						}
						total = query.updateAll(password: password)

						delete = query.updateAll(sessionId: "")
					} else {
						flash.message = "Password Not Matched"
						redirect(controller: "login", action: "resetPassword")
					}
				} else {
					flash.message = "User not found"
					redirect(controller: "login", action: "resetPassword")
				}
			} else {
				flash.message = "Session invalid ! Link is outdated."
				redirect(controller: "login", action: "resetPassword")
			}
			mailService.sendMail {
				to emailId
				subject "Password Changed Successfully"
				text """Hi, ${username} ! Your Password has been changed recently. Please make sure this is you."""
			}
			redirect(controller: "login", action: "auth")
		}
		catch (Exception e){
			flash.message = e.getMessage()
			redirect(controller: "login", action: "resetPassword")
		}
	}
}