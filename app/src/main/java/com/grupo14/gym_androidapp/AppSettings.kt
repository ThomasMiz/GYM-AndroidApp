package com.grupo14.gym_androidapp

class AppSettings {
    companion object {
        private var detailedExecutionEnabled: Boolean = true

        fun getIsDetailedExecutionEnabled(): Boolean {
            // TODO: Cargar de algun lugar
            return detailedExecutionEnabled
        }

        fun setIsDetailedExecutionEnabled(newValue: Boolean) {
            // TODO: Guardar en algun lugar
            detailedExecutionEnabled = newValue
        }
    }
}