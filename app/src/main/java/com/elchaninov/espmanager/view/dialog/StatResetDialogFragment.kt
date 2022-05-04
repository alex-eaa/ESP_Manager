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

class StatResetDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val listener = DialogInterface.OnClickListener { _, witch ->
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_RESPONSE to witch))
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Сброс статистики!")
            .setMessage("Вы подтверждаете сброс статистики? После сброса информацию невозможно будет восстановить.")
            .setPositiveButton("Да", listener)
            .setNegativeButton("Нет", null)
            .create()
    }

    companion object {
        val TAG = StatResetDialogFragment::class.java.simpleName
        val REQUEST_KEY = "$TAG:REQUEST_KEY"
        const val KEY_RESPONSE = "RESPONSE"

        fun show(manager: FragmentManager) {
            StatResetDialogFragment().show(manager, TAG)
        }

        fun setupListener(
            manager: FragmentManager,
            lifecycleOwner: LifecycleOwner,
            listener: () -> Unit
        ) {
            manager.setFragmentResultListener(REQUEST_KEY, lifecycleOwner) { _, bundle ->
                if (bundle.getInt(KEY_RESPONSE) == AlertDialog.BUTTON_POSITIVE) {
                    listener.invoke()
                }
            }
        }
    }
}
