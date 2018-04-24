package pl.sparkbit.security.login

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import pl.sparkbit.security.restauthn.AuthenticationTokenHelper
import pl.sparkbit.security.service.SessionService
import spock.lang.Specification

import javax.servlet.FilterChain
import java.time.Instant;

class SessionExpirationHeaderFilterSpec extends Specification {

    def sessionService
    def sessionExpirationHeaderName
    def authenticationTokenHelper
    def filterChain

    def setup() {
        sessionService = Mock(SessionService)
        sessionExpirationHeaderName = "any_string"
        authenticationTokenHelper = Mock(AuthenticationTokenHelper)
        filterChain = Mock(FilterChain)
    }

    def "shouldDoNothingIfExpirationDisabled"() {
        setup:
          def filter = new SessionExpirationHeaderFilter(sessionService, sessionExpirationHeaderName, authenticationTokenHelper)
          def request = new MockHttpServletRequest()
          def response = new MockHttpServletResponse()

        when:
          filter.doFilter(request,response,filterChain)

        then:
          1 * sessionService.isSessionExpirationEnabled() >> false
          0 * sessionService.updateAndGetSessionExpirationTimestamp()
          response.headerNames.isEmpty()
    }

    def "shouldUpdateTimestampAndAddHeaderIfExpirationEnabled"() {
        setup:
        def filter = new SessionExpirationHeaderFilter(sessionService, sessionExpirationHeaderName, authenticationTokenHelper)
        def request = new MockHttpServletRequest()
        def response = new MockHttpServletResponse()

        when:
        filter.doFilter(request,response,filterChain)

        then:
        1 * sessionService.isSessionExpirationEnabled() >> true
        1 * authenticationTokenHelper.extractAuthenticationToken(request) >> Optional.of("randomToken")
        1 * sessionService.updateAndGetSessionExpirationTimestamp("randomToken") >> Instant.ofEpochMilli(0)
        response.headerNames.contains(sessionExpirationHeaderName)
    }
}
