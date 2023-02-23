package ru.ll.coffeebonus.ui.login

import android.content.Intent
import android.content.IntentSender
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import dagger.hilt.android.AndroidEntryPoint
import ru.ll.coffeebonus.R
import ru.ll.coffeebonus.databinding.FragmentLoginBinding
import ru.ll.coffeebonus.ui.BaseFragment
import timber.log.Timber

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel>() {

    companion object {
        const val REQ_ONE_TAP = 42
    }

    override val viewModel: LoginViewModel by viewModels()

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
//    val auth: FirebaseAuth by inject()

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        viewLifecycleOwner.lifecycleScope.launch {
//            viewModel.loginStateObservable
//                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
//                .filter { it }
//                .collect { viewModel.onLoginSuccess() }
//        }
        binding.buttonLogin.setOnClickListener {
            oneTapClient = Identity.getSignInClient(requireActivity())
            signInRequest = BeginSignInRequest.builder()
                .setPasswordRequestOptions(
                    BeginSignInRequest.PasswordRequestOptions.builder()
                        .setSupported(true)
                        .build()
                )
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(getString(R.string.your_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(false)
                        .build()
                )
                .build()
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(requireActivity()) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null
                        )
                    } catch (e: IntentSender.SendIntentException) {
                        Timber.e("Couldn't start One Tap UI: ${e.localizedMessage}")
                        Toast.makeText(
                            requireContext(),
                            "Не удалось запустить авторизацию через гугл ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                .addOnFailureListener(requireActivity()) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Timber.e(e, "Не удалось запустить авторизацию через гугл")
                    if (e is ApiException) {
                        if (e.statusCode == 16) {
                            Toast.makeText(
                                requireContext(),
                                "Не обнаружено ни одного гугл аккаунта",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Не удалось запустить авторизацию ${e.statusCode}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось запустить авторизацию ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken!!
                    Timber.d("пользователь ${credential.displayName}")
                    // Initialize Firebase Auth
//                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                    auth.signInWithCredential(firebaseCredential)
//                        .addOnCompleteListener(requireActivity()) { task ->
//                            if (task.isSuccessful) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Timber.d("signInWithCredential:success")
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Timber.w("signInWithCredential:failure", task.exception)
//                                Toast.makeText(
//                                    requireContext(),
//                                    "Не удалось авторизоваться в firebase ${task.exception?.message}",
//                                    Toast.LENGTH_LONG
//                                ).show()
//                            }
//                        }

                } catch (e: ApiException) {
                    Timber.e(e, "Ошибка логина в гугл, статускод = ${e.statusCode}")
                    if (e.statusCode == CommonStatusCodes.CANCELED) {
//Юзер отменил авторизацию - ничего не делать
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Не удалось завершить авторизацию в гугл ${e.statusCode}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}