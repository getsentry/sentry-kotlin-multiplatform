import io.sentry.kotlin.multiplatform.Span
import kotlin.test.BeforeTest
import kotlin.test.Test

class SpanTest {
    class Fixture {
        fun getSut(): Span {
            return Span()
        }
    }

    private lateinit var fixture: Fixture

    @BeforeTest
    fun setUp() {
        fixture = Fixture()
    }

    @Test
    fun `span can be created`() {

    }
}