package ie.koala.topics.framework.web

import java.io.IOException
import java.util.HashSet

import info.bliki.wiki.filter.ITextConverter
import info.bliki.wiki.model.IWikiModel
import info.bliki.wiki.tags.HTMLTag
import info.bliki.wiki.tags.util.INoBodyParsingTag
import ie.koala.topics.app.*

class InfoTag : HTMLTag("info"), INoBodyParsingTag {

    @Throws(IOException::class)
    override fun renderHTML(converter: ITextConverter, buf: Appendable, model: IWikiModel) {
        val node = this
        val tagAtttributes = node.attributes
        val keysSet = tagAtttributes.keys
        buf.append("<p>")
        for (str in keysSet) {
            when (str) {
                "timestamp" -> buf.append(TopicsApplication.versionName)
                "name" -> buf.append(TopicsApplication.versionName)
                "code" -> buf.append(TopicsApplication.versionCode)
                "githash" -> buf.append(TopicsApplication.versionGitHash)
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
