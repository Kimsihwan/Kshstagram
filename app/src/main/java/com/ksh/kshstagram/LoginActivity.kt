package com.ksh.kshstagram

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    // 로그인을 관리할수 있는 클래스
    var auth : FirebaseAuth? = null
    var googleSignInClient : GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        auth = FirebaseAuth.getInstance();
        email_login_button.setOnClickListener { createAndLoginEmail() }
        google_sign_in_button.setOnClickListener { googleLogin() }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    // 구글로그인에 접근하는걸 허가해주는 아이디(인증키)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
    }

    // 회원가입
    fun createAndLoginEmail(){
        auth?.createUserWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                moveMainPage(auth?.currentUser)
                //Toast.makeText(this,"아이디 생성 성공", Toast.LENGTH_LONG).show()
            } else if(task.exception?.message.isNullOrEmpty()){
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            } else {
                // 로그인
                signinEmail()
            }
        }
    }

    // 로그인
    fun signinEmail() {
        auth?.signInWithEmailAndPassword(email_edittext.text.toString(),password_edittext.text.toString())?.addOnCompleteListener {
            task ->
            if(task.isSuccessful) {
                moveMainPage(auth?.currentUser)
                //Toast.makeText(this,"로그인이 성공 했습니다.", Toast.LENGTH_LONG).show()
            } else if(task.exception?.message.isNullOrEmpty()) {
                Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun moveMainPage(user :FirebaseUser?) {
        // 유저가 있을경우
        if(user != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    // 구글 로그인
    fun googleLogin(){
        var signInIntent = googleSignInClient?.signInIntent
        startActivityForResult(signInIntent,GOOGLE_LOGIN_CODE)
    }

    // 파이어베이스로 계정 넘기기
    fun firebaseAuthWithGoogle(account : GoogleSignInAccount){
        var credential = GoogleAuthProvider.getCredential(account.idToken,null)
        auth?.signInWithCredential(credential)
    }

    // 구글로그인을 클릭했을때 창이뜨고 그 창에 원하는 계정을 값이 넘어오는 코드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            if(result.isSuccess){
                var account = result.signInAccount
                firebaseAuthWithGoogle(account!!)
            }
        }
    }
}
