import groovy.json.JsonSlurper

class DustConfiguration {

    static Map load(String file) {
        new JsonSlurper().parseText(new File(file).text) as Map
    }

}
