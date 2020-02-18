package com.example.jmemo

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_input_uri.*

class UrlDialogFragment : DialogFragment() {

    private var onFragmentInteractionListener: OnFragmentInteractionListener? = null

    companion object{
        public val INPUT_URL_DIALOG = "input_url_dialog"
        fun getInstance() : UrlDialogFragment{
            val e = UrlDialogFragment()
            return e
        }
        /*@JvmStatic
        fun newInstance() =
            UrlDialogFragment().apply {
                arguments = Bundle().apply {
                }
            }*/
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
            onFragmentInteractionListener!!.onFragmentInteraction(urlEditText.text.toString())
            dismiss()
        }
        cancelButton.setOnClickListener {
            dismiss()
        }
        return v;
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            onFragmentInteractionListener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        onFragmentInteractionListener = null
    }
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(strOfUri: String)
    }

}
