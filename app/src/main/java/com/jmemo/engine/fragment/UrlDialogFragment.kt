package com.jmemo.engine.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.jmemo.engine.R
import kotlinx.android.synthetic.main.dialog_input_uri.*

class UrlDialogFragment : DialogFragment() {

    private var onUriDialogFragmentInteractionListener: OnUriDialogFragmentInteractionListener? = null

    companion object{
        val INPUT_URL_FROM_DIALOG = "input_url_dialog"
        fun getInstance() : UrlDialogFragment {
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
            onUriDialogFragmentInteractionListener!!.onUriDialogFragmentInteraction(urlEditText.text.toString())
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return v;
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnUriDialogFragmentInteractionListener) {
            onUriDialogFragmentInteractionListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        onUriDialogFragmentInteractionListener = null
    }
    interface OnUriDialogFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onUriDialogFragmentInteraction(url: String)
    }

}
