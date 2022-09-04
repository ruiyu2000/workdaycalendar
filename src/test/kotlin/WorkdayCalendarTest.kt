import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*


internal class WorkdayCalendarTest {

    private lateinit var workdayCalendar: WorkdayCalendar

    @BeforeEach
    fun setUp() {
        workdayCalendar = WorkdayCalendar(
            GregorianCalendar(2004, Calendar.JANUARY, 1, 8, 0),
            GregorianCalendar(2004, Calendar.JANUARY, 1, 16, 0)
        )
    }

    @Test
    fun isWeekend() {
        assertEquals(false, WorkdayCalendar.isWeekend(GregorianCalendar(2022, Calendar.SEPTEMBER, 2, 0, 0)))
        assertEquals(true, WorkdayCalendar.isWeekend(GregorianCalendar(2022, Calendar.SEPTEMBER, 3, 0, 0)))
        assertEquals(true, WorkdayCalendar.isWeekend(GregorianCalendar(2022, Calendar.SEPTEMBER, 4, 0, 0)))
        assertEquals(false, WorkdayCalendar.isWeekend(GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 0, 0)))
    }

    @Test
    fun isHoliday() {
        workdayCalendar.setRecurringHoliday(LocalDate.of(2004, 1, 1))
        workdayCalendar.setHoliday(LocalDate.of(2004, 12, 25))

        assertEquals(false, workdayCalendar.isHoliday(GregorianCalendar(2022, Calendar.SEPTEMBER, 4, 0, 0)))
        assertEquals(true, workdayCalendar.isHoliday(GregorianCalendar(2004, Calendar.JANUARY, 1, 0, 1)))
        assertEquals(true, workdayCalendar.isHoliday(GregorianCalendar(2005, Calendar.JANUARY, 1, 0, 1)))
        assertEquals(true, workdayCalendar.isHoliday(GregorianCalendar(2004, Calendar.DECEMBER, 25, 0, 0)))
        assertEquals(false, workdayCalendar.isHoliday(GregorianCalendar(2005, Calendar.DECEMBER, 25, 0, 0)))
    }

    @Test
    fun isWithinWorkingHours() {
        data class TestData(
            val expected: Boolean,
            val calendar: Calendar
        )
        arrayOf(
            TestData(false, GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 0, 0)),
            TestData(false, GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 7, 59, 59)),
            TestData(true, GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 8, 0)),
            TestData(true, GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 15, 59, 59)),
            TestData(false, GregorianCalendar(2022, Calendar.SEPTEMBER, 5, 16, 0, 0)),
        ).forEach {
            assertEquals(it.expected, workdayCalendar.isWithinWorkingHours(it.calendar))
        }
    }

    @Test
    fun `works with supplied inputs`() {
        workdayCalendar.setRecurringHoliday(LocalDate.of(2004, 5, 17))
        workdayCalendar.setHoliday(LocalDate.of(2004, 5, 27))

        data class TestData(
            val startDate: Date,
            val increment: Float,
            val expected: Date
        )
        arrayOf(
            // 24-05-2004 18:05 med tillegg av -5.5 arbeidsdager er 14-05-2004 12:00
            TestData(
                GregorianCalendar(2004, Calendar.MAY, 24, 18, 5).time,
                -5.5f,
                GregorianCalendar(2004, Calendar.MAY, 14, 12, 0).time,
            ),
            // 24-05-2004 19:03 med tillegg av 44.723656 arbeidsdager er 27-07-2004 13:47
            TestData(
                GregorianCalendar(2004, Calendar.MAY, 24, 19, 3).time,
                44.723656f,
                GregorianCalendar(2004, Calendar.JULY, 27, 13, 47, 21).time,
            ),
            // 24-05-2004 18:03 med tillegg av -6.7470217 arbeidsdager er 13-05-2004 10:02
            TestData(
                GregorianCalendar(2004, Calendar.MAY, 24, 18, 3).time,
                -6.7470217f,
                GregorianCalendar(2004, Calendar.MAY, 13, 10, 1, 25).time,
            ),
            // 24-05-2004 08:03 med tillegg av 12.782709 arbeidsdager er 10-06-2004 14:18
            TestData(
                GregorianCalendar(2004, Calendar.MAY, 24, 8, 3).time,
                12.782709f,
                GregorianCalendar(2004, Calendar.JUNE, 10, 14, 18, 42).time,
            ),
            // 24-05-2004 07:03 med tillegg av 8.276628 arbeidsdager er 04-06-2004 10:12
            TestData(
                GregorianCalendar(2004, Calendar.MAY, 24, 7, 3).time,
                8.276628f,
                GregorianCalendar(2004, Calendar.JUNE, 4, 10, 12, 46).time,
            ),
        ).forEach {
            assertEquals(
                it.expected,
                workdayCalendar.getWorkdayIncrement(it.startDate, it.increment)
            )
        }
    }
}
