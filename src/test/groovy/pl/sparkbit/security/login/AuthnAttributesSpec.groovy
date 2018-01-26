package pl.sparkbit.security.login

import spock.lang.Specification

class AuthnAttributesSpec extends Specification {

    @SuppressWarnings("GroovyAccessibility")
    def SEP = AuthnAttributes.SEPARATOR

    def "shouldSucceedForProvidedAttributesMatchingExpected"() {
        setup:
        def providedAttributes = ["username": "batman"]
        def expectedAttributes = ["username"] as Set
        when:
        def lui = new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        lui.size() == 1
        lui["username"] == "batman"
    }

    def "shouldThrowExceptionForAdditionalProvidedAttribute"() {
        setup:
        def providedAttributes = ["username": "batman", "company": "gotham"]
        def expectedAttributes = ["username"] as Set
        when:
        new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        thrown(InvalidJsonAuthenticationException)
    }

    def "shouldThrowExceptionForMissingProvidedAttribute"() {
        setup:
        def providedAttributes = ["username": "batman"]
        def expectedAttributes = ["username", "company"] as Set
        when:
        new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        thrown(InvalidJsonAuthenticationException)
    }

    def "shouldThrowExceptionForEmptyProvidedAttributes"() {
        setup:
        def providedAttributes = [:]
        def expectedAttributes = ["username", "company"] as Set
        when:
        new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        thrown(InvalidJsonAuthenticationException)
    }

    def "shouldThrowExceptionForNullProvidedAttributes"() {
        setup:
        def providedAttributes = null
        def expectedAttributes = [] as Set
        when:
        new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        thrown(InvalidJsonAuthenticationException)
    }

    def "shouldChangeCamelcaseToLowerUnderscoreInAttributeNames"() {
        setup:
        def providedAttributes = ["userName": "batman"]
        def expectedAttributes = ["userName"] as Set
        when:
        def lui = new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        lui.size() == 1
        lui["user_name"] == "batman"
        lui["userName"] == null
    }

    def "shouldLeaveUnderscoreInAttributeNamesUnchanged"() {
        setup:
        def providedAttributes = ["user_name": "batman"]
        def expectedAttributes = ["user_name"] as Set
        when:
        def lui = new AuthnAttributes(providedAttributes, expectedAttributes)
        then:
        lui.size() == 1
        lui["user_name"] == "batman"
    }

    def "shouldSucceedWhenCreatingFromProperString"() {
        setup:
        def input = "aaa${SEP}bbb${SEP}ccc${SEP}ddd"
        when:
        def lui = new AuthnAttributes(input)
        then:
        lui.size() == 2
        lui["aaa"] == "bbb"
        lui["ccc"] == "ddd"
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

    def "shouldThrowExceptionWhenCreatingFromStringWithOddNumberOfFragments"() {
        setup:
        def input = "aaa${SEP}bbb${SEP}ccc"
        when:
        new AuthnAttributes(input)
        then:
        thrown(IllegalArgumentException)
    }

    def "shouldConvertUserIdAtrributesToEncodedString"() {
        setup:
        def providedAttributes = ["username": "batman", "company": "gotham"]
        def expectedAttributes = ["username", "company"] as Set
        def userIdAttributes = new AuthnAttributes(providedAttributes, expectedAttributes)
        when:
        def s = userIdAttributes.toString()
        then:
        s == "username${SEP}batman${SEP}company${SEP}gotham" || s == "company${SEP}gotham${SEP}username${SEP}batman"
    }
}
