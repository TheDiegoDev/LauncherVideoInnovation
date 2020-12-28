package guinea.diego.launchervideoinnovation.utils

interface BaseCallback<T> {
    fun onResult(result: T)
    fun onError(error: Error)
}


