package com.marcorighini.notifydanger.map

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.marcorighini.notifydanger.R


class DangerDialogFragment : DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = AlertDialog.Builder(activity!!)
            .setTitle(getString(R.string.danger_title))
            .setMessage(getString(R.string.danger_message))
            .create()

    companion object {
        const val TAG = "DangerDialogFragment"
    }
}