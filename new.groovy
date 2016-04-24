import groovy.json.JsonSlurper
def slurper = new JsonSlurper()

def jobsJson = new JsonSlurper().parseText( new URL( 'https://api.github.com/repos/mthak/spark/git/trees/master?' ).text )
//println jobsJson
def giturl = "http://github.com/mthak/spark.git"
def branch = "master"
def command = "-e clean deploy -DskipTests"
categorizedJobsView('APM-Jobs') {
    jobs {
    }
    categorizationCriteria {
        regexGroupingRule(/^APM-.*$/)
    }
    columns {
        status()
        categorizedJob()
        buildButton()
    }
}

jobsJson.tree.each { 
if (it.type == 'tree' && it.path != '.github') {
   println it.path
   println ("Creating jobs ${it.path}")
mavenJob("APM-${it.path}") {
    scm {
        git(giturl,branch)
    }
    triggers {
     scm('*/15 * * * *')
    }
    maven {
        rootPOM("${it.path}/pom.xml")
        goals(command)
     }   
    /*steps {
        maven(command)
    }*/
}
}
}

