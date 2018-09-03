package ie.koala.topics.web

import info.bliki.wiki.model.Configuration
import info.bliki.wiki.model.WikiModel

class AppWikiModel : WikiModel("assets://images/\${image}", "assets://wiki/\${title}.wiki") {
    companion object {

        init {
            Configuration.DEFAULT_CONFIGURATION.addTokenTag("info", InfoTag())
        }
    }

}
