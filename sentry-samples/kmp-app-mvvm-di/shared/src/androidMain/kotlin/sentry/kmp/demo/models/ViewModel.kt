package sentry.kmp.demo.models

import androidx.lifecycle.ViewModel as AndroidXViewModel
import androidx.lifecycle.viewModelScope as androidXViewModelScope
import kotlinx.coroutines.CoroutineScope

actual abstract class ViewModel actual constructor() : AndroidXViewModel() {
  actual val viewModelScope: CoroutineScope = androidXViewModelScope

  actual override fun onCleared() {
    super.onCleared()
  }
}
