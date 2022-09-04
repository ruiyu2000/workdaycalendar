import java.time.LocalDate
import java.time.ZoneId
import java.util.*

class WorkdayCalendar(val workdayStart: Calendar, val workdayStop: Calendar) {

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

    companion object {
        fun isWeekend(calendar: Calendar): Boolean {
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            return dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY
        }
    }
}
