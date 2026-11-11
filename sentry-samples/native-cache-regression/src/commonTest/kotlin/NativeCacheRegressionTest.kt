import io.sentry.kotlin.multiplatform.Sentry
import kotlin.test.Test

class NativeCacheRegressionTest {
    @Test
    fun captureWithoutInitializationIsSafe() {
        Sentry.captureMessage("Native cache regression")
    }
}
