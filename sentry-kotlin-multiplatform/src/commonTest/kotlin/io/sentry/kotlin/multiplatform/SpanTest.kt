import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.Span
import io.sentry.kotlin.multiplatform.SpanImpl
import io.sentry.kotlin.multiplatform.TestSpan
import kotlin.test.BeforeTest
import kotlin.test.Test

class SpanTest {
    class Fixture {
        fun getSut(): SpanImpl {
            return SpanImpl()
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