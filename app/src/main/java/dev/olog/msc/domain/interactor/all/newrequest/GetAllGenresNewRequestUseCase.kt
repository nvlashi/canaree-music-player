package dev.olog.msc.domain.interactor.all.newrequest

import dev.olog.msc.core.entity.Genre
import dev.olog.msc.core.executor.ComputationScheduler
import dev.olog.msc.domain.gateway.GenreGateway
import dev.olog.msc.core.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllGenresNewRequestUseCase @Inject constructor(
    schedulers: ComputationScheduler,
    private val gateway: GenreGateway

) : ObservableUseCase<List<Genre>>(schedulers) {

    override fun buildUseCaseObservable(): Observable<List<Genre>> {
        return gateway.getAllNewRequest()
    }
}