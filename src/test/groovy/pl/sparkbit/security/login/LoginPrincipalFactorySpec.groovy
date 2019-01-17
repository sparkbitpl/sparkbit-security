package pl.sparkbit.security.login

import spock.lang.Specification

class LoginPrincipalFactorySpec extends Specification {

    def "shouldSucceedForProvidedAttributesMatchingExpected"() {
        setup:
        def providedAttributes = ["username": "batman"]
        Set<String> expectedAttributes = ["username"]
        def factory = new LoginPrincipalFactory(expectedAttributes)
        when:
        factory.validateAuthnAttributes(providedAttributes)
        then:
        noExceptionThrown()
    }

    def "shouldThrowExceptionForAdditionalProvidedAttribute"() {
        setup:
        def providedAttributes = ["username": "batman", "company": "gotham"]
        Set<String> expectedAttributes = ["username"]
        def factory = new LoginPrincipalFactory(expectedAttributes)
        when:
        factory.validateAuthnAttributes(providedAttributes)
        then:
        thrown(ExpectedAndProvidedAuthnAttributesMismatchException)
    }

    def "shouldThrowExceptionForMissingProvidedAttribute"() {
        setup:
        def providedAttributes = ["username": "batman"]
        Set<String> expectedAttributes = ["username", "company"]
        def factory = new LoginPrincipalFactory(expectedAttributes)
        when:
        factory.validateAuthnAttributes(providedAttributes)
        then:
        thrown(ExpectedAndProvidedAuthnAttributesMismatchException)
    }

    def "shouldThrowExceptionForEmptyProvidedAttributes"() {
        setup:
        def providedAttributes = [:]
        Set<String> expectedAttributes = ["username", "company"]
        def factory = new LoginPrincipalFactory(expectedAttributes)
        when:
        factory.validateAuthnAttributes(providedAttributes)
        then:
        thrown(ExpectedAndProvidedAuthnAttributesMismatchException)
    }

    def "shouldThrowExceptionForNullProvidedAttributes"() {
        setup:
        def providedAttributes = null
        Set<String> expectedAttributes = []
        def factory = new LoginPrincipalFactory(expectedAttributes)
        when:
        factory.validateAuthnAttributes(providedAttributes)
        then:
        thrown(ExpectedAndProvidedAuthnAttributesMismatchException)
    }


    def "shouldThrowExceptionWhenCreatingFromNullString"() {
        setup:
        def input = null
        when:
        new AuthnAttributes(input)
        then:
        thrown(IllegalArgumentException)
    }

    def "shouldThrowExceptionWhenCreatingFromStringNotContainingSeparator"() {
        setup:
        def input = "aaa"
        when:
        new AuthnAttributes(input)
        then:
        thrown(IllegalArgumentException)
    }
}
