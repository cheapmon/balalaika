package com.github.cheapmon.balalaika.data.result

/** Progress of an asynchronous operation */
public sealed class ProgressState<out T, out M, out E> {
    /** Operation has been started */
    public object Init : ProgressState<Nothing, Nothing, Nothing>()

    /** Operation is in progress */
    public data class InProgress<M>(
        val percentage: Int,
        val message: M? = null
    ) : ProgressState<Nothing, M, Nothing>()

    /** Operation is finished and has either succeeded or failed */
    public data class Finished<T, E>(val data: Result<T, E>) : ProgressState<T, Nothing, E>()

    /** Listener for progress events while the operation is ongoing */
    internal interface Listener<M> {
        /** Indicate current progress */
        suspend fun progress(inProgress: InProgress<M>)
    }
}
