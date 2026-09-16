package com.itanes.appturismo.res.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.radiobutton.MaterialRadioButton
import com.itanes.appturismo.AppTurismoApp
import com.itanes.appturismo.R
import utils.SettingsManager


class SettingsFragment : Fragment() {

    private lateinit var settingsManager: SettingsManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_settings, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingsManager = (requireActivity().application as AppTurismoApp).settingsManager

        val showNotesSwitch = view.findViewById<SwitchMaterial>(R.id.showNotesSwitch)
        val lightRadio = view.findViewById<MaterialRadioButton>(R.id.lightRadio)
        val darkRadio = view.findViewById<MaterialRadioButton>(R.id.darkRadio)
        val systemRadio = view.findViewById<MaterialRadioButton>(R.id.systemRadio)

        // Estado inicial
        showNotesSwitch.isChecked = settingsManager.showNotesByDefault
        when (settingsManager.themeMode) {
            SettingsManager.THEME_LIGHT -> lightRadio.isChecked = true
            SettingsManager.THEME_DARK -> darkRadio.isChecked = true
            else -> systemRadio.isChecked = true
        }

        // Listeners
        showNotesSwitch.setOnCheckedChangeListener { _, checked ->
            settingsManager.showNotesByDefault = checked
        }

        lightRadio.setOnClickListener { settingsManager.themeMode = SettingsManager.THEME_LIGHT }
        darkRadio.setOnClickListener { settingsManager.themeMode = SettingsManager.THEME_DARK }
        systemRadio.setOnClickListener { settingsManager.themeMode = SettingsManager.THEME_SYSTEM }
    }
}