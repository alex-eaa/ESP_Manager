package com.elchaninov.espmanager.view.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.core.os.bundleOf
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.LifecycleOwner
import com.elchaninov.espmanager.model.ms.MsForSendModel

class SaveDialogFragment : AppCompatDialogFragment() {

    private val msSetupForSendModel: MsForSendModel?
        get() = requireArguments().getParcelable(ARG_MS_MODEL_FOR_SEND)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = DialogInterface.OnClickListener { _, witch ->
            setFragmentResult(
                REQUEST_KEY,
                bundleOf(KEY_RESPONSE to witch, ARG_MS_MODEL_FOR_SEND to msSetupForSendModel)
            )
        }

        return AlertDialog.Builder(requireContext())
            .setMessage("Сохранить настройки?")
            .setPositiveButton("Да", listener)
            .setNegativeButton("Нет", null)
            .create()
    }

    companion object {
        val TAG = SaveDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:REQUEST_KEY"
        const val KEY_RESPONSE = "RESPONSE"

        const val ARG_MS_MODEL_FOR_SEND = "ARG_MS_MODEL_FOR_SEND"

        fun show(
            manager: FragmentManager,
            msForSendModel: MsForSendModel?
        ) {
            val dialogFragment = SaveDialogFragment()
            dialogFragment.arguments = bundleOf(ARG_MS_MODEL_FOR_SEND to msForSendModel)
            dialogFragment.show(manager, TAG)
        }

        fun <M : MsForSendModel> setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: (M) -> Unit
        ) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, bundle ->
                if (bundle.getInt(KEY_RESPONSE) == AlertDialog.BUTTON_POSITIVE) {
                    bundle.getParcelable<M>(ARG_MS_MODEL_FOR_SEND)
                        ?.let { listener.invoke(it) }
                }
            }
        }
    }
}
