package com.elchaninov.espmanager.view.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.fragment.app.FragmentManager

class ErrorDialogFragment : AppCompatDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return AlertDialog.Builder(requireContext())
            .setTitle("Ошибка")
            .setMessage("Проверьте правильность ввода параметров.")
            .setNegativeButton("Отмена", null)
            .create()
    }

    companion object {
        val TAG = ErrorDialogFragment::class.java.simpleName

        fun show(manager: FragmentManager) {
            ErrorDialogFragment().show(manager, TAG)
        }
    }
}