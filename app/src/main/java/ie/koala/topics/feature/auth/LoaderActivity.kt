package ie.koala.topics.feature.auth

import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import androidx.loader.app.LoaderManager
import androidx.loader.content.CursorLoader
import androidx.loader.content.Loader
import androidx.appcompat.app.AppCompatActivity
import android.widget.ArrayAdapter
import org.slf4j.LoggerFactory
import java.util.ArrayList

abstract class LoaderActivity: AppCompatActivity(), androidx.loader.app.LoaderManager.LoaderCallbacks<Cursor> {

    private val log = LoggerFactory.getLogger(SignUpActivity::class.java)

    var adapter: ArrayAdapter<String>? = null

    companion object {
        const val LOADER_ID = 1
    }

    private interface ProfileQuery {
        companion object {
            val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Email.IS_PRIMARY)

            const val ADDRESS = 0
        }
    }

    /**
     * see https://stackoverflow.com/a/42001556
     *
     * @param i
     * @param bundle
     * @return
     */
    override fun onCreateLoader(i: Int, bundle: Bundle?): androidx.loader.content.Loader<Cursor> {
        log.debug("onCreateLoader")

        return androidx.loader.content.CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                ContactsContract.Data.CONTENT_URI, ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE + " = ?", arrayOf(ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE),

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC")
    }

    override fun onLoadFinished(loader: androidx.loader.content.Loader<Cursor>, cursor: Cursor) {
        log.debug("onLoadFinished")

        val emails = ArrayList<String>()
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS))
            cursor.moveToNext()
        }

        addEmailsToAutoComplete(emails)
    }

    override fun onLoaderReset(loader: androidx.loader.content.Loader<Cursor>) {
        log.debug("onLoaderReset")
    }

    /**
     * Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
     *
     * @param emailAddressCollection
     */
    abstract fun addEmailsToAutoComplete(emailAddressCollection: List<String>)


}