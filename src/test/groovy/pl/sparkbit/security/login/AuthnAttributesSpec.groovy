package pl.sparkbit.security.login

import spock.lang.Specification

class AuthnAttributesSpec extends Specification {

    @SuppressWarnings("GroovyAccessibility")
    def SEP = AuthnAttributes.SEPARATOR

    def "shouldChangeCamelcaseToLowerUnderscoreInAttributeNames"() {
        setup:
        def providedAttributes = ["userName": "batman"]
        when:
        def lui = new AuthnAttributes(providedAttributes).withUnderscoredKeys()
        then:
        lui.size() == 1
        lui["user_name"] == "batman"
        lui["userName"] == null
    }

    def "shouldLeaveUnderscoreInAttributeNamesUnchanged"() {
        setup:
        def providedAttributes = ["user_name": "batman"]
        when:
        def lui = new AuthnAttributes(providedAttributes)
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

    def "shouldConvertUserIdAttributesToEncodedString"() {
        setup:
        def attributesMap = ["username": "batman", "company": "gotham"]
        def userIdAttributes = new AuthnAttributes(attributesMap)
        when:
        def s = userIdAttributes.toString()
        then:
        s == "username${SEP}batman${SEP}company${SEP}gotham" || s == "company${SEP}gotham${SEP}username${SEP}batman"
    }
}
