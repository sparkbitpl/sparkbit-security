package pl.sparkbit.security.session

import groovy.json.JsonOutput
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.AuthenticationEntryPoint
import pl.sparkbit.security.session.auth.AuthnAttributes
import pl.sparkbit.security.session.auth.LoginAuthenticationFilter
import pl.sparkbit.security.session.auth.LoginPrincipal
import spock.lang.Specification

import javax.servlet.FilterChain

class LoginAuthenticationFilterSpec extends Specification {

    def authentication
    def authManager
    def entryPoint
    def filterChain

    def setup() {
        authentication = Mock(Authentication)
        authManager = Mock(AuthenticationManager)
        entryPoint = Mock(AuthenticationEntryPoint)
        filterChain = Mock(FilterChain)
    }

    def "should accept a password login dto"() {
        setup:
        def expectedAuthnAttributes = ['applicationName', 'email'] as Set
        def filter = new LoginAuthenticationFilter(authManager, entryPoint, expectedAuthnAttributes)
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()
        def json = [
                "authnAttributes": [
                        "email"          : "test@test.com",
                        "applicationName": "ACME"
                ],
                "password"       : "1"
        ]
        AuthnAttributes authnAttributes =
                new AuthnAttributes(json.authnAttributes, expectedAuthnAttributes)
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
        def expectedAuthnAttributes = ['applicationName', 'email'] as Set
        def filter = new LoginAuthenticationFilter(authManager, entryPoint, expectedAuthnAttributes)
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