package dev.olog.core.interactor.lastfm

import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.executor.IoScheduler
import dev.olog.core.interactor.base.CompletableUseCaseWithParam
import dev.olog.core.prefs.AppPreferencesGateway
import io.reactivex.Completable
import javax.inject.Inject

class UpdateLastFmUserCredentials @Inject constructor(
    schedulers: IoScheduler,
    private val gateway: AppPreferencesGateway,
    private val lastFmEncrypter: IEncrypter

) : CompletableUseCaseWithParam<UserCredentials>(schedulers) {

    override fun buildUseCaseObservable(param: UserCredentials): Completable {
        return Completable.create {
            val user = encryptUser(param)
            gateway.setLastFmCredentials(user)

            it.onComplete()
        }
    }

    private fun encryptUser(user: UserCredentials): UserCredentials {
        return UserCredentials(
            lastFmEncrypter.encrypt(user.username),
            lastFmEncrypter.encrypt(user.password)
        )
    }

}