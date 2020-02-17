package com.example.jmemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelStore
import io.realm.Realm
import kotlinx.android.synthetic.main.dialog_input_uri.*

class UrlDialogFragment : DialogFragment() {

    val realm = Realm.getDefaultInstance()

    companion object{
        public val INPUT_URL_DIALOG = "input_url_dialog"
        fun getInstance() : UrlDialogFragment{
            val e = UrlDialogFragment()
            return e
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.dialog_input_uri, container)
        val okButton = v.findViewById(R.id.okButton) as Button
        val cancelButton = v.findViewById(R.id.cancelButton) as Button

        okButton.setOnClickListener {
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return v;
    }

}
