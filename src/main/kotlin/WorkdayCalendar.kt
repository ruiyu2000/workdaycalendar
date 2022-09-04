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

    fun getWorkdayIncrement(startDate: Date, incrementInWorkdays: Float): Date {

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

        val start = GregorianCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            workdayStart.get(Calendar.HOUR_OF_DAY),
            workdayStart.get(Calendar.MINUTE),
            workdayStart.get(Calendar.SECOND),
        ).apply { add(Calendar.MILLISECOND, -1) }.time
        val stop = GregorianCalendar(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
            workdayStop.get(Calendar.HOUR_OF_DAY),
            workdayStop.get(Calendar.MINUTE),
            workdayStop.get(Calendar.SECOND),
        ).time

        return calendar.time.after(start) && calendar.time.before(stop)
    }

    companion object {
        fun isWeekend(calendar: Calendar): Boolean {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }
    }
}
