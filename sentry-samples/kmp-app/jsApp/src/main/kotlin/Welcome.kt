import csstype.px
import csstype.rgb
import react.FC
import react.Props
import emotion.react.css
import io.sentry.kotlin.multiplatform.Sentry
import io.sentry.kotlin.multiplatform.SentryLevel
import react.dom.html.InputType
import react.dom.html.ReactHTML.button
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.input
import react.useState
import sample.kpm_app.Platform

external interface WelcomeProps : Props {
    var name: String
}

val Welcome = FC<WelcomeProps> { props ->
    var name by useState(props.name)
    div {
        css {
            padding = 5.px
            backgroundColor = rgb(8, 97, 22)
            color = rgb(56, 246, 137)
        }
        +"Hello, ${Platform().platform}"
    }
    input {
        css {
            marginTop = 5.px
            marginBottom = 5.px
            fontSize = 14.px
        }
        type = InputType.text
        value = name
        onChange = { event ->
            name = event.target.value
        }
    }
    button {
        onClick = {
            Sentry.captureMessage("From Kotlin Multiplatform Js Browser") {
                it.level = SentryLevel.FATAL
                it.setContext("moment", "ME")
                it.setTag("js-tag", "test test")
            }

            Sentry.captureMessage("No context available")
        }
        +"Capture message"
    }
    button {
        onClick = {
            try {
                throw RuntimeException("test")
            } catch (err: Throwable) {
                Sentry.captureException(err)
            }
        }
        +"Capture Exception"
    }
}
