package dev.olog.domain.interactor.favorite

import dev.olog.domain.entity.favorite.FavoriteItemState
import dev.olog.domain.gateway.FavoriteGateway
import javax.inject.Inject

class UpdateFavoriteStateUseCase @Inject constructor(
    private val favoriteGateway: FavoriteGateway
) {

    suspend operator fun invoke(param: FavoriteItemState) {
        return favoriteGateway.updateFavoriteState(param)
    }
}