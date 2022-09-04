import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*


internal class WorkdayCalendarTest {

    lateinit var workdayCalendar: WorkdayCalendar

    @BeforeEach
    fun setUp() {
        workdayCalendar = WorkdayCalendar(
            GregorianCalendar(2004, Calendar.JANUARY, 1, 8, 0),
            GregorianCalendar(2004, Calendar.JANUARY, 1, 16, 0)
        )
    }

    @Test
    fun `works with supplied inputs`() {
        workdayCalendar.setRecurringHoliday(GregorianCalendar(2004, Calendar.MAY, 17, 0, 0))
        workdayCalendar.setHoliday(GregorianCalendar(2004, Calendar.MAY, 27, 0, 0))

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
