import java.util.*

class WorkdayCalendar(val workdayStart: Calendar, val workdayStop: Calendar) {

    val holidays = mutableSetOf<Calendar>()
    val recurringHolidays = mutableSetOf<Calendar>()

    fun setHoliday(holiday: Calendar) {
        holidays += holiday
    }

    fun setRecurringHoliday(recurringHoliday: Calendar) {
        recurringHolidays += recurringHoliday
    }

    fun getWorkdayIncrement(startDate: Date, incrementInWorkdays: Float): Date {

        return startDate
    }
}
