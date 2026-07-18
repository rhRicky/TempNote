package com.tempnote.app.ui

import androidx.annotation.IdRes
import androidx.annotation.StringRes
import com.tempnote.app.R

enum class RetentionOption(
    val hours: Int,
    @StringRes val labelRes: Int,
    @IdRes val buttonId: Int,
) {
    HOURS_6(6, R.string.retention_6h, R.id.button6Hours),
    HOURS_24(24, R.string.retention_24h, R.id.button24Hours),
    DAYS_7(168, R.string.retention_7d, R.id.button7Days),
    ;

    companion object {
        fun fromHours(hours: Int): RetentionOption {
            return values().firstOrNull { it.hours == hours } ?: HOURS_24
        }

        fun fromButtonId(@IdRes buttonId: Int): RetentionOption {
            return values().firstOrNull { it.buttonId == buttonId } ?: HOURS_24
        }
    }
}
