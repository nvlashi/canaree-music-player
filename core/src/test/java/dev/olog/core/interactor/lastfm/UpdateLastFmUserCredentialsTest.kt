package dev.olog.core.interactor.lastfm

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import dev.olog.core.IEncrypter
import dev.olog.core.entity.UserCredentials
import dev.olog.core.prefs.AppPreferencesGateway
import dev.olog.test.shared.MainCoroutineRule
import dev.olog.test.shared.runBlocking
import org.junit.Rule
import org.junit.Test

class UpdateLastFmUserCredentialsTest {

    @get:Rule
    val coroutineRule = MainCoroutineRule()

    @Test
    fun testInvoke() = coroutineRule.runBlocking {
        // given
        val user = UserCredentials("user", "pwd")

        val gateway = mock<AppPreferencesGateway>()
        val encrypter = mock<IEncrypter> {
            on { encrypt(user.username) } doReturn "abc"
            on { encrypt(user.password) } doReturn "123"
        }

        val sut = UpdateLastFmUserCredentials(gateway, encrypter)

        // when
        sut(user)

        // then
        verify(encrypter).encrypt(user.username)
        verify(encrypter).encrypt(user.password)
        verify(gateway).setLastFmCredentials(UserCredentials("abc", "123"))
    }

}