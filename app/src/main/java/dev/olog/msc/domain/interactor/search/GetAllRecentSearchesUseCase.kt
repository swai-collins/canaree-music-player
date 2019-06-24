package dev.olog.msc.domain.interactor.search

import dev.olog.core.entity.SearchResult
import dev.olog.core.executor.IoScheduler
import dev.olog.core.gateway.RecentSearchesGateway
import dev.olog.msc.domain.interactor.base.ObservableUseCase
import io.reactivex.Observable
import javax.inject.Inject

class GetAllRecentSearchesUseCase @Inject constructor(
    scheduler: IoScheduler,
    private val recentSearchesGateway: RecentSearchesGateway

) : ObservableUseCase<List<SearchResult>>(scheduler) {

    override fun buildUseCaseObservable(): Observable<List<SearchResult>> {
        return recentSearchesGateway.getAll()
    }
}