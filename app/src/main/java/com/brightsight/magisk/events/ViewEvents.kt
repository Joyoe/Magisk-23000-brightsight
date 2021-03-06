package com.brightsight.magisk.events

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.navigation.NavDirections
import com.brightsight.magisk.MainDirections
import com.brightsight.magisk.R
import com.brightsight.magisk.arch.*
import com.brightsight.magisk.core.Const
import com.brightsight.magisk.core.base.ActivityResultCallback
import com.brightsight.magisk.core.base.BaseActivity
import com.brightsight.magisk.core.model.module.OnlineModule
import com.brightsight.magisk.events.dialog.MarkDownDialog
import com.brightsight.magisk.utils.Utils
import com.brightsight.magisk.view.MagiskDialog
import com.brightsight.magisk.view.Shortcuts

class ViewActionEvent(val action: BaseActivity.() -> Unit) : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) = action(activity)
}

class OpenReadmeEvent(private val item: OnlineModule) : MarkDownDialog() {
    override suspend fun getMarkdownText() = item.notes()
    override fun build(dialog: MagiskDialog) {
        super.build(dialog)
        dialog.applyButton(MagiskDialog.ButtonType.NEGATIVE) {
            titleRes = android.R.string.cancel
        }.cancellable(true)
    }
}

class PermissionEvent(
    private val permission: String,
    private val callback: (Boolean) -> Unit
) : ViewEvent(), ActivityExecutor {

    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) =
        activity.withPermission(permission) {
            onSuccess {
                callback(true)
            }
            onFailure {
                callback(false)
            }
        }
}

class BackPressEvent : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        activity.onBackPressed()
    }
}

class DieEvent : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        activity.finish()
    }
}

class ShowUIEvent(private val delegate: View.AccessibilityDelegate?)
    : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        activity.setContentView()
        activity.setAccessibilityDelegate(delegate)
    }
}

class RecreateEvent : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        activity.recreate()
    }
}

class MagiskInstallFileEvent(private val callback: ActivityResultCallback)
    : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("*/*")
        try {
            activity.startActivityForResult(intent, callback)
            Utils.toast(R.string.patch_file_msg, Toast.LENGTH_LONG)
        } catch (e: ActivityNotFoundException) {
            Utils.toast(R.string.app_not_found, Toast.LENGTH_SHORT)
        }
    }
}

class NavigationEvent(
    private val directions: NavDirections
) : ViewEvent(), ActivityExecutor {
    override fun invoke(activity: com.brightsight.magisk.arch.BaseUIActivity<*, *>) {
        (activity as? com.brightsight.magisk.arch.BaseUIActivity<*, *>)?.apply {
            directions.navigate()
        }
    }
}

class AddHomeIconEvent : ViewEvent(), ContextExecutor {
    override fun invoke(context: Context) {
        Shortcuts.addHomeIcon(context)
    }
}

class SelectModuleEvent : ViewEvent(), FragmentExecutor {
    override fun invoke(fragment: BaseUIFragment<*, *>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).setType("application/zip")
        try {
            fragment.apply {
                activity.startActivityForResult(intent) { code, intent ->
                    if (code == Activity.RESULT_OK && intent != null) {
                        intent.data?.also {
                            MainDirections.actionFlashFragment(Const.Value.FLASH_ZIP, it).navigate()
                        }
                    }
                }
            }
        } catch (e: ActivityNotFoundException) {
            Utils.toast(R.string.app_not_found, Toast.LENGTH_SHORT)
        }
    }
}
