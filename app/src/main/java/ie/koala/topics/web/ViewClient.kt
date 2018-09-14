package ie.koala.topics.web

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.AssetManager
import android.net.Uri
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.google.common.io.ByteStreams
import ie.koala.topics.R
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import org.slf4j.LoggerFactory
import android.webkit.WebResourceRequest
import android.os.Build
import android.annotation.TargetApi



class ViewClient : WebViewClient {

    protected var replacementMap: Map<String, String>? = null

    constructor() {}

    constructor(replacementMap: Map<String, String>?) {
        this.replacementMap = replacementMap
    }

    override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
        Toast.makeText(view.context, description, Toast.LENGTH_SHORT).show()
    }

    @SuppressWarnings("deprecation")
    override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
        return handleUrl(view, url)
    }

    @TargetApi(Build.VERSION_CODES.N)
    override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
        val url = request.url.toString()
        return handleUrl(view, url)
    }

    private fun handleUrl(view: WebView, url: String): Boolean {
        val context: Context = view.context
        if (url.startsWith("assets://") || url.startsWith("file://")) {
            return false
        } else if (url.startsWith("mailto:")) {
            val intent = Intent(Intent.ACTION_SENDTO, Uri.parse(url))
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, context.getText(R.string.invalid_url), Toast.LENGTH_LONG).show()
            }

            view.reload()
            return true
        } else {
            // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            try {
                context.startActivity(intent)
            } catch (e: ActivityNotFoundException) {
                Toast.makeText(context, context.getText(R.string.invalid_url), Toast.LENGTH_LONG).show()
            }
            return true
        }
    }

    private fun newEmailIntent(@Suppress("UNUSED_PARAMETER") context: Context, address: String, subject: String, body: String, cc: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
        intent.putExtra(Intent.EXTRA_TEXT, body)
        intent.putExtra(Intent.EXTRA_SUBJECT, subject)
        intent.putExtra(Intent.EXTRA_CC, cc)
        intent.type = "message/rfc822"
        return intent
    }

    override fun shouldInterceptRequest(view: WebView, url: String): WebResourceResponse? {
        if (url.startsWith("assets://") || url.startsWith("file://")) {
            val am = view.context.resources.assets
            val fileName = url.substring(9)
            val mimeType = getMimeType(url)

            if (url.endsWith("wiki")) {
                // assumption is that this is coming from the assets directory
                // so remove "assets://" from beginning of url
                return wikiResponse(fileName, mimeType, am)
            } else {
                if (replacementMap != null) {
                    for ((key, value) in replacementMap!!) {
                        if (url.endsWith(key)) {
                            return assetResponse(value, mimeType, am)
                        }
                    }
                }
                return assetResponse(fileName, mimeType, am)
            }
        } else {
            return super.shouldInterceptRequest(view, url)
        }
    }

    companion object {

        val log = LoggerFactory.getLogger(ViewClient::class.java)

        fun getMimeType(fileName: String): String {
            return if (fileName.endsWith("png")) {
                "image/png"
            } else if (fileName.endsWith("jpg")) {
                "image/jpeg"
            } else if (fileName.endsWith("html")) {
                "text/html"
            } else if (fileName.endsWith("wiki")) {
                "text/html"
            } else if (fileName.endsWith("js")) {
                "text/javascript"
            } else if (fileName.endsWith("css")) {
                "text/css"
            } else {
                ""
            }
        }

        protected fun wikiResponse(fileName: String, mimeType: String, am: AssetManager): WebResourceResponse? {
            val inputStream: InputStream
            val htmlStream: InputStream
            try {
                inputStream = am.open(fileName)
                val encoding = "UTF-8"
                val wikiModel = AppWikiModel()
                val wikiStr = String(ByteStreams.toByteArray(inputStream))
                val htmlStr = wikiModel.render(wikiStr)
                log.debug("html=\"" + htmlStr + "\"")
                htmlStream = ByteArrayInputStream(htmlStr.toByteArray(charset(encoding)))
                return WebResourceResponse(mimeType, encoding, htmlStream)
            } catch (e: IOException) {
                error("wikiResponse: io exception")
            }
        }

        protected fun assetResponse(fileName: String, mimeType: String, am: AssetManager): WebResourceResponse? {
            log.debug("fileName=" + fileName);
            val in_s: InputStream
            try {
                in_s = am.open(fileName)
                val encoding = "UTF-8"
                return WebResourceResponse(mimeType, encoding, in_s)
            } catch (e: IOException) {
                error("assetResponse: not found fileName=\"$fileName\"")
            }
        }
    }
}
