package com.brightsight.magisk.core.base

import android.content.BroadcastReceiver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import com.brightsight.magisk.core.wrap

abstract class BaseReceiver : BroadcastReceiver() {

    final override fun onReceive(context: Context, intent: Intent?) {
        onReceive(context.wrap() as ContextWrapper, intent)
    }

    abstract fun onReceive(context: ContextWrapper, intent: Intent?)
}
