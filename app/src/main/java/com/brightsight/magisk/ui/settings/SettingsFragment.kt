package com.brightsight.magisk.ui.settings

import android.os.Bundle
import android.view.View
import com.brightsight.magisk.R
import com.brightsight.magisk.arch.BaseUIFragment
import com.brightsight.magisk.databinding.FragmentSettingsMd2Binding
import com.brightsight.magisk.di.viewModel
import com.brightsight.magisk.ktx.addSimpleItemDecoration
import com.brightsight.magisk.ktx.addVerticalPadding
import com.brightsight.magisk.ktx.fixEdgeEffect
import com.brightsight.magisk.ktx.setOnViewReadyListener

class SettingsFragment : BaseUIFragment<SettingsViewModel, FragmentSettingsMd2Binding>() {

    override val layoutRes = R.layout.fragment_settings_md2
    override val viewModel by viewModel<SettingsViewModel>()

    override fun onStart() {
        super.onStart()

        activity.title = resources.getString(R.string.settings)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.settingsList.setOnViewReadyListener {
            binding.settingsList.scrollToPosition(0)
        }

        val resource = requireContext().resources
        val l_50 = resource.getDimensionPixelSize(R.dimen.l_50)
        val l1 = resource.getDimensionPixelSize(R.dimen.l1)
        binding.settingsList.addVerticalPadding(
            0,
            l1
        )
        binding.settingsList.addSimpleItemDecoration(
            left = l1,
            top = l_50,
            right = l1,
            bottom = l_50,
        )
        binding.settingsList.fixEdgeEffect()
    }

    override fun onResume() {
        super.onResume()
        viewModel.items.forEach { it.refresh() }
    }

}
