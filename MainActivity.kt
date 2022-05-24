package com.example.networkhttpurlconnection

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.networkhttpurlconnection.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    val binding by lazy{ ActivityMainBinding.inflate(layoutInflater)}
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.buttonRequest.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { //버튼을 클릭하면 네트워크 작업을 요청하고 백그라운드에서 처리.
                try{
                    var urlText = binding.editUrl.text.toString()
                    if(!urlText.startsWith("https")){//입력된 주소가 https 로 시작하지 않으면
                        urlText = "https://${urlText}"//https:// 를 앞에 붙여준다.
                    }
                    var url = URL(urlText)//주소를 URL 객체로 변환
                    var urlConnection = url.openConnection() as HttpURLConnection //서버와의 연결 생성, HttpURLConnection으로 형변환
                    urlConnection.requestMethod = "GET"//연결된 커넥션에 요청 방식 설정
                    if(urlConnection.responseCode == HttpURLConnection.HTTP_OK){//응답이 정상이면 응답 데이터 처리
                        val streamReader = InputStreamReader(urlConnection.inputStream)//입력 스트림 연결
                        val buffered = BufferedReader(streamReader)//버퍼에 담아 데이터 읽을 준비

                        val content = StringBuilder()
                        while(true){//한줄씩 읽은 데이터를 content에 저장
                            val line = buffered.readLine()?: break
                            content.append(line)
                        }
                        buffered.close()//스트림 해제
                        urlConnection.disconnect()//커넥션 해제

                        launch(Dispatchers.Main){
                            binding.textContent.text = content.toString()//화면 텍스트뷰에 content 값 입력
                        }
                    }
                } catch (e: Exception){
                    e.printStackTrace()
                }
            }
        }
    }
}