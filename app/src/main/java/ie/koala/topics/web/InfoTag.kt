package ie.koala.topics.web

import java.io.IOException
import java.util.HashSet

import info.bliki.wiki.filter.ITextConverter
import info.bliki.wiki.model.IWikiModel
import info.bliki.wiki.tags.HTMLTag
import info.bliki.wiki.tags.util.INoBodyParsingTag
import ie.koala.topics.app.*
import ie.koala.topics.app.TopicsApplication.Companion.getMyApplication

class InfoTag : HTMLTag("info"), INoBodyParsingTag {


    @Throws(IOException::class)
    override fun renderHTML(converter: ITextConverter, buf: Appendable, model: IWikiModel) {
        val node = this
        val tagAtttributes = node.attributes
        val keysSet = tagAtttributes.keys
        buf.append("<p>")
        val application: TopicsApplication = getMyApplication()
        for (str in keysSet) {
            if (str == "timestamp") {
                buf.append(application.versionName)
            } else if (str == "name") {
                buf.append(application.versionName)
            } else if (str == "code") {
                buf.append(application.versionCode)
            } else if (str == "githash") {
                buf.append(application.versionGitHash)
            }
        }
        buf.append("</p>")
    }

    override fun isAllowedAttribute(attName: String?): Boolean {
        return ALLOWED_ATTRIBUTES_SET.contains(attName)
    }

    companion object {
        val ALLOWED_ATTRIBUTES_SET = HashSet<String>()
        val ALLOWED_ATTRIBUTES = arrayOf("timestamp", "name", "code", "githash")

        init {
            for (i in ALLOWED_ATTRIBUTES.indices) {
                ALLOWED_ATTRIBUTES_SET.add(ALLOWED_ATTRIBUTES[i])
            }
        }
    }

}
