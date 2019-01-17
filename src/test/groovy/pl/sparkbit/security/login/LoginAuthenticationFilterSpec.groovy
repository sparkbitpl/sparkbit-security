package pl.sparkbit.security.login

import groovy.json.JsonOutput
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.AuthenticationEntryPoint
import pl.sparkbit.security.hooks.LoginHook
import spock.lang.Specification

import javax.servlet.FilterChain

class LoginAuthenticationFilterSpec extends Specification {

    def authentication
    def authManager
    def entryPoint
    def loginHook
    def filterChain

    def setup() {
        authentication = Mock(Authentication) {
            getPrincipal() >> Mock(LoginUserDetails)
        }
        authManager = Mock(AuthenticationManager)
        entryPoint = Mock(AuthenticationEntryPoint)
        loginHook = Mock(LoginHook)
        filterChain = Mock(FilterChain)
    }

    def "should accept a password login dto"() {
        setup:
        Set<String> expectedAuthnAttributes = ['applicationName', 'email']
        def filter = new LoginAuthenticationFilter(authManager, entryPoint,
                new LoginPrincipalFactory(expectedAuthnAttributes), loginHook)
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        def json = [
                "authnAttributes": [
                        "email"          : "test@test.com",
                        "applicationName": "ACME"
                ],
                "password"       : "1"
        ]
        AuthnAttributes authnAttributes = new AuthnAttributes(json.authnAttributes)
        LoginPrincipal principal = new LoginPrincipal(authnAttributes)
        request.setContent(JsonOutput.toJson(json).bytes)
        def expectedToken = new UsernamePasswordAuthenticationToken(principal, json.password)

        when:
        filter.doFilter(request, response, filterChain)

        then:
        _ * authentication.isAuthenticated() >> true
        1 * authManager.authenticate(expectedToken) >> authentication
        1 * filterChain.doFilter(request, response)
    }

    def "should fail with an unsupported json payload"() {
        setup:
        Set<String> expectedAuthnAttributes = ['applicationName', 'email']
        def filter = new LoginAuthenticationFilter(authManager, entryPoint,
                new LoginPrincipalFactory(expectedAuthnAttributes), loginHook)
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        def json = [
                "authnAttributes": [
                        "email"          : "test@test.com",
                        "applicationName": "ACME"
                ],
                "type"          : "asfsdad"
        ]
        request.setContent(JsonOutput.toJson(json).bytes)

        when:
        filter.doFilter(request, response, filterChain)

        then:
        1 * entryPoint.commence(request, response, _)
    }
}