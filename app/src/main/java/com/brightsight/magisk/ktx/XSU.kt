package com.brightsight.magisk.ktx

import android.content.Context
import com.brightsight.magisk.core.Config
import com.brightsight.magisk.core.Const
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun reboot(reason: String = if (Config.recovery) "recovery" else "") {
    Shell.su("/system/bin/svc power reboot $reason || /system/bin/reboot $reason").submit()
}

fun relaunchApp(context: Context) {
    val intent = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
    val args = mutableListOf("am", "start", "--user", Const.USER_ID.toString())
    val cmd = intent.toCommand(args).joinToString(separator = " ")
    Shell.su("run_delay 1 \"$cmd\"").exec()
    Runtime.getRuntime().exit(0)
}

suspend fun Shell.Job.await() = withContext(Dispatchers.IO) { exec() }
