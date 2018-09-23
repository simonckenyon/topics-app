package ie.koala.topics.feature.topic

import android.content.DialogInterface
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import android.view.Gravity
import android.view.View
import android.view.ViewManager
import android.widget.LinearLayout
import android.widget.TextView
import ie.koala.topics.R
import org.jetbrains.anko.*
import org.jetbrains.anko.custom.ankoView

private const val ACTIVITY_PADDING = 16

class AddTopicDialog(ui: AnkoContext<View>) {

    lateinit var dialog: DialogInterface
    lateinit var topicTitleText: TextInputEditText
    lateinit var topicContentText: TextInputEditText
    lateinit var cancelButton: TextView
    lateinit var okButton: TextView

    init {
        with(ui) {
            dialog = alert {
                customView {
                    verticalLayout {
                        padding = dip(ACTIVITY_PADDING)

                        textView(R.string.title_add_topic) {
                            textSize = 24f
                            textColor = R.color.black
                        }.lparams {
                            bottomMargin = dip(ACTIVITY_PADDING)
                        }

                        textInputLayout {
                            hint = context.getString(R.string.title_topic)
                            topicTitleText = textInputEditText {
                                textSize = 16f
                            }
                        }

                        textInputLayout {
                            hint = "Content"
                            topicContentText = textInputEditText {
                                textSize = 16f
                            }
                        }

                        linearLayout {
                            topPadding = dip(24)
                            orientation = LinearLayout.HORIZONTAL
                            horizontalGravity = Gravity.END

                            cancelButton = textView("Cancel") {
                                textSize = 14f
                            }.lparams {
                                marginEnd = dip(ACTIVITY_PADDING)
                            }
                            okButton = textView("OK") {
                                textSize = 14f
                            }
                        }
                    }
                }
            }.show()
        }
    }
}

inline fun ViewManager.textInputEditText(theme: Int = 0, init: TextInputEditText.() -> Unit) =
        ankoView({ TextInputEditText(it) }, theme, init)

inline fun ViewManager.textInputLayout(theme: Int = 0, init: TextInputLayout.() -> Unit) =
        ankoView({ TextInputLayout(it) }, theme, init)