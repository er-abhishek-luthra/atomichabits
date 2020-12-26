/*
 * Copyright (C) 2016 Álinson Santos Xavier <isoron@gmail.com>
 *
 * This file is part of Loop Habit Tracker.
 *
 * Loop Habit Tracker is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * Loop Habit Tracker is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.isoron.uhabits.core.models

import org.isoron.uhabits.core.utils.*

data class Habit(
        var color: PaletteColor = PaletteColor(8),
        var description: String = "",
        var frequency: Frequency = Frequency.DAILY,
        var id: Long? = null,
        var isArchived: Boolean = false,
        var name: String = "",
        var position: Int = 0,
        var question: String = "",
        var reminder: Reminder? = null,
        var targetType: Int = AT_LEAST,
        var targetValue: Double = 0.0,
        var type: Int = YES_NO_HABIT,
        var unit: String = "",
        var uuid: String? = null,
        val computedEntries: EntryList,
        val originalEntries: EntryList,
        val scores: ScoreList,
        val streaks: StreakList,
) {
    var observable = ModelObservable()

    val isNumerical: Boolean
        get() = type == NUMBER_HABIT

    val uriString: String
        get() = "content://org.isoron.uhabits/habit/$id"

    fun hasReminder(): Boolean = reminder != null

    fun isCompletedToday(): Boolean {
        val today = DateUtils.getTodayWithOffset()
        val value = computedEntries.get(today).value
        return if (isNumerical) {
            if (targetType == AT_LEAST) {
                value / 1000.0 >= targetValue
            } else {
                value / 1000.0 <= targetValue
            }
        } else {
            value != Entry.NO && value != Entry.UNKNOWN
        }
    }

    fun recompute() {
        scores.recompute()
        streaks.recompute()
        computedEntries.recomputeFrom(
                originalEntries = originalEntries,
                frequency = frequency,
                isNumerical = isNumerical,
        )
    }

    fun copyFrom(other: Habit) {
        this.color = other.color
        this.description = other.description
        this.frequency = other.frequency
        // this.id should not be copied
        this.isArchived = other.isArchived
        this.name = other.name
        this.position = other.position
        this.question = other.question
        this.reminder = other.reminder
        this.targetType = other.targetType
        this.targetValue = other.targetValue
        this.type = other.type
        this.unit = other.unit
        this.uuid = other.uuid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Habit) return false

        if (color != other.color) return false
        if (description != other.description) return false
        if (frequency != other.frequency) return false
        if (id != other.id) return false
        if (isArchived != other.isArchived) return false
        if (name != other.name) return false
        if (position != other.position) return false
        if (question != other.question) return false
        if (reminder != other.reminder) return false
        if (targetType != other.targetType) return false
        if (targetValue != other.targetValue) return false
        if (type != other.type) return false
        if (unit != other.unit) return false
        if (uuid != other.uuid) return false

        return true
    }

    override fun hashCode(): Int {
        var result = color.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + frequency.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        result = 31 * result + isArchived.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + position
        result = 31 * result + question.hashCode()
        result = 31 * result + (reminder?.hashCode() ?: 0)
        result = 31 * result + targetType
        result = 31 * result + targetValue.hashCode()
        result = 31 * result + type
        result = 31 * result + unit.hashCode()
        result = 31 * result + (uuid?.hashCode() ?: 0)
        return result
    }

    companion object {
        const val AT_LEAST = 0
        const val AT_MOST = 1
        const val NUMBER_HABIT = 1
        const val YES_NO_HABIT = 0
    }
}