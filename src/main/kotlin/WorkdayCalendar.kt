import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class WorkdayCalendar(private val workdayStart: Calendar, private val workdayStop: Calendar) {

    private val holidays = mutableSetOf<LocalDate>()
    private val recurringHolidays = mutableSetOf<LocalDate>()

    fun setHoliday(holiday: LocalDate) {
        holidays += holiday
    }

    fun setRecurringHoliday(recurringHoliday: LocalDate) {
        recurringHolidays += recurringHoliday
    }

    enum class TimeDirection {
        BEFORE, AFTER
    }

    fun getWorkdayIncrement(startDate: Date, incrementInWorkdays: Float): Date {
        val calendar = Calendar.getInstance().apply { time = startDate }
        val millisecondsPerWorkday = workdayStart.timeInMillis - workdayStop.timeInMillis

        when (val timeDirection = if (incrementInWorkdays > 0) TimeDirection.AFTER else TimeDirection.BEFORE) {
            TimeDirection.AFTER -> {

            }
            TimeDirection.BEFORE -> {

            }
        }

        return startDate
    }

    fun isHoliday(localDate: LocalDate): Boolean {
        val isHoliday = holidays.contains(localDate)
        val isRecurringHoliday = recurringHolidays.find {
            it == LocalDate.of(it.year, localDate.monthValue, localDate.dayOfMonth)
        } != null
        return isHoliday || isRecurringHoliday
    }

    fun isHoliday(calendar: Calendar): Boolean {
        return isHoliday(calendar.time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
    }

    fun isWithinWorkingHours(calendar: Calendar): Boolean {
        if (isWeekend(calendar) || isHoliday(calendar)) return false

        val start = getWorkdayStartWithCalendar(calendar).apply { add(Calendar.MILLISECOND, -1) }.time
        val stop = getWorkdayStopWithCalendar(calendar).time

        return calendar.time.after(start) && calendar.time.before(stop)
    }

    private fun getWorkdayStartWithCalendar(calendar: Calendar): Calendar {
        return GregorianCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            workdayStart.get(Calendar.HOUR_OF_DAY),
            workdayStart.get(Calendar.MINUTE),
            workdayStart.get(Calendar.SECOND),
        )
    }

    private fun getWorkdayStopWithCalendar(calendar: Calendar): Calendar {
        return GregorianCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            workdayStop.get(Calendar.HOUR_OF_DAY),
            workdayStop.get(Calendar.MINUTE),
            workdayStop.get(Calendar.SECOND),
        )
    }

    fun goToNextWorkingHour(calendar: Calendar, direction: TimeDirection = TimeDirection.AFTER) {
        if (isWithinWorkingHours(calendar)) return

        when (direction) {
            TimeDirection.AFTER -> {
                val isAfterEnd = calendar.time.after(getWorkdayStopWithCalendar(calendar).time)
                val isBeforeMidnight = calendar.time.before(
                    getMidnight(calendar).apply { add(Calendar.DAY_OF_YEAR, 1) }.time
                )
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, workdayStart.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, workdayStart.get(Calendar.MINUTE))
                    set(Calendar.SECOND, workdayStart.get(Calendar.SECOND))
                    if (isAfterEnd && isBeforeMidnight) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                    while (isWeekend(this) || isHoliday(this)) {
                        add(Calendar.DAY_OF_YEAR, 1)
                    }
                }
            }

            TimeDirection.BEFORE -> {
                val isBeforeStart = calendar.time.before(getWorkdayStartWithCalendar(calendar).time)
                val isAfterMidnight = calendar.time.after(
                    getMidnight(calendar).apply { add(Calendar.MILLISECOND, -1) }.time
                )
                calendar.apply {
                    set(Calendar.HOUR_OF_DAY, workdayStop.get(Calendar.HOUR_OF_DAY))
                    set(Calendar.MINUTE, workdayStop.get(Calendar.MINUTE))
                    set(Calendar.SECOND, workdayStop.get(Calendar.SECOND))
                    if (isBeforeStart && isAfterMidnight) {
                        add(Calendar.DAY_OF_YEAR, -1)
                    }
                    while (isWeekend(this) || isHoliday(this)) {
                        add(Calendar.DAY_OF_YEAR, -1)
                    }
                }
            }
        }
    }

    companion object {
        fun isWeekend(calendar: Calendar): Boolean {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }

        private fun getMidnight(calendar: Calendar): Calendar {
            return GregorianCalendar(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                0,
                0,
                0
            )
        }
    }
}
