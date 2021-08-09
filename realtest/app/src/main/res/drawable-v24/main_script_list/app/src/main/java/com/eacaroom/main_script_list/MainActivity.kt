package com.eacaroom.main_script_list

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.io.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {


    var filePath = "/data/data/com.eacaroom.main_script_list/files/"
    lateinit var fileView: ListView
    lateinit var fileList:ArrayList<String>

    lateinit var edtDiary : TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ViewPager 추가 */
        val viewPager_news : ViewPager2 = findViewById(R.id.viewPager_news)
        viewPager_news.offscreenPageLimit = 1 // 1개의 페이지 미리 로드
        viewPager_news.adapter = ViewPagerAdapter(getNewsList()) //adapter 생성
        viewPager_news.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향 가로로 swipe


        // activity 이동
        setContentView(R.layout.activity_main)
        var jump_Activity = findViewById<Button>(R.id.jump_activity)
        jump_Activity.setOnClickListener {
            var intent = Intent(applicationContext, SecondActivity::class.java)
            startActivity(intent)
        }


        // 제품에 대한 분석 결과 저장 button
        val save_button = findViewById<Button>(R.id.save_btn)
        save_button.setOnClickListener{
            showsavedialog()
        }


        fileView = findViewById<ListView>(R.id.listView_scrap)
        fileList = ArrayList() //디렉토리 내 저장된 json 파일 리스트

        var listFiles = File(filePath).listFiles() //해당 경로의 모든 파일을 File[] 타입 변수에 저장
        var fileName:String //파일 전체 이름
        var extName:String //확장자 이름
        for (file in listFiles!!) {
            fileName=file.name
            extName=fileName.substringAfterLast(".") //확장자 추출
            if(extName=="json") {
                fileList.add(fileName.substringBeforeLast("."))
            }
        }

        /*adapter 생성*/
        var fileAd = ArrayAdapter(this,android.R.layout.simple_list_item_1, fileList)
        fileView.adapter = fileAd
        fileAd.notifyDataSetChanged()

        fileView.setOnItemClickListener{parent, view, position, id ->
            var fName = fileList[position]
            /*var intent = Intent(applicationContext, ResultActivity::class.java)
            intent.putExtra("fileName", fName)
            startActivity(intent)*/
        }



    }

    // ViewPager에 들어갈 item
    private fun getNewsList(): ArrayList<Int>{
        return arrayListOf<Int>(R.drawable.news1, R.drawable.news2, R.drawable.news3, R.drawable.news4, R.drawable.news5 )
    }

    // file 이름 입력 받는 save dialog 생성 함수
    fun showsavedialog(){
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle("제품 정보 입력")

        val input = EditText(this)
        input.setHint("오리온_초코파이")
        input.inputType = InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("저장", DialogInterface.OnClickListener{ dialog, which ->
            var f_name = input.text.toString()
            val file = File("${filesDir}/$f_name.json")

            // 동일한 파일명 존재하지 않을 때 json 형식 파일 저장
            if(!file.exists()){
                //f_name 파일명으로 받아 저장
                val fileWriter = FileWriter("${filesDir}/$f_name.json")
                val bufferWriter = BufferedWriter(fileWriter)

                bufferWriter.write("안녕하세요 초코파이입니다.")
                bufferWriter.close()

                Toast.makeText(applicationContext, "'$f_name' 저장되었습니다.", Toast.LENGTH_SHORT).show()
            } else{ Toast.makeText(applicationContext, "'$f_name' 파일이 존재합니다.", Toast.LENGTH_SHORT).show() }
        })

        builder.setNegativeButton("취소", DialogInterface.OnClickListener{dialog, which ->
            dialog.cancel()
            Toast.makeText(applicationContext, "취소되었습니다.", Toast.LENGTH_SHORT).show()
        })

        builder.show()
    }


}