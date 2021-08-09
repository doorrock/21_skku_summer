package com.cookandroid.realtest

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tbuonomo.viewpagerdotsindicator.DotsIndicator
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import kotlin.concurrent.timer


class MainActivity : AppCompatActivity() {

    private var backPressedTime: Long = 0

    override fun onBackPressed() {
        // 2초내 다시 클릭하면 앱 종료
        if (System.currentTimeMillis() - backPressedTime < 2000) {
            ActivityCompat.finishAffinity(this)
            return
        }

        // 처음 클릭 메시지
        Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show()
        backPressedTime = System.currentTimeMillis()
    }


    lateinit var user_dialog: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // 메인화면 toolbar -> 사용 안하는 것
        val toolbar = findViewById<Toolbar>(R.id.toolbar_main)
        toolbar.title = "  Main Toolbar"
        toolbar.inflateMenu(R.menu.menu_nothing)
        setSupportActionBar(toolbar)

        /*저장된 file list 구성*/
        var fileView = findViewById<ListView>(R.id.listView_scrap)
        var fileList = ArrayList<String>() //디렉토리 내 저장된 json 파일 리스트

        var filePath = "/data/data/com.cookandroid.realtest/files/"
        var listFiles = File(filePath).listFiles() //해당 경로의 모든 파일을 File[] 타입 변수에 저장
        var fileName: String //파일 전체 이름
        var extName: String //확장자 이름
        for (file in listFiles!!) {
            fileName = file.name
            extName = fileName.substringAfterLast(".") //확장자 추출
            if (extName == "json") {
                fileList.add(fileName.substringBeforeLast("."))
            }
        }

        /*adapter 생성*/
        var fileAd = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
        fileView.adapter = fileAd

        fileView.setOnItemClickListener{parent, view, position, id ->
            var fName = fileList[position]
            var intent = Intent(applicationContext, ResultActivity::class.java)
            intent.putExtra("filename", fName)
            startActivity(intent)
        }

        fileView.setOnItemLongClickListener{parent, view, position, id ->

            /*listview item 삭제 위한 dialog*/
            var fName = fileList[position]
            val file_path = "${filesDir}" + "/" + "$fName" + ".json"
            var file = File(file_path)
            val dlt = AlertDialog.Builder(this@MainActivity)
            dlt.setMessage("'$fName' 파일을 삭제하시겠습니까?")
            dlt.setIcon(R.drawable.file)

            dlt.setPositiveButton("확인", DialogInterface.OnClickListener{ dialog, which ->

                // 'fName'인 파일 존재하면 삭제
                if(file.exists()) {
                    /*fileView.removeViewAt(position)
                    fileAd.notifyDataSetChanged()*/
                    file.delete()

                }
                if(!file.exists()) {
                    Toast.makeText(applicationContext, "파일이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    fileAd.notifyDataSetChanged()
                }
            })

            dlt.setNegativeButton("취소", DialogInterface.OnClickListener{ dialog, which ->
                dialog.cancel()
                Toast.makeText(applicationContext, "취소되었습니다.", Toast.LENGTH_SHORT).show()
            })

            dlt.show()

            true
        }


        // camera button 연결
        var btnCamera = findViewById<FloatingActionButton>(R.id.btn1)
        btnCamera.setOnClickListener {
            var intent = Intent(applicationContext, CameraActivity::class.java)
            startActivity(intent)
        }


        /*ViewPager Adapter 생성*/
        val dotsIndicator = findViewById<DotsIndicator>(R.id.dots_indicator)
        val viewPager_news : ViewPager2 = findViewById(R.id.viewPager_news)
//        viewPager_news.offscreenPageLimit = 1 // 1개의 페이지 미리 로드
        viewPager_news.adapter = ViewPagerAdapter(getNewsList()) //adapter 생성
        dotsIndicator.setViewPager2(viewPager_news)
        viewPager_news.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향 가로로 swipe
    }

    inner class ViewPagerAdapter (newsList: ArrayList<Int>) : RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {
        var item = newsList

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PagerViewHolder((parent))

        override fun getItemCount(): Int = item.size

        override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
            holder.news.setImageResource(item[position])
        }

        inner class PagerViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder
            (LayoutInflater.from(parent.context).inflate(R.layout.news_list_item, parent, false)) {

            val news = itemView.findViewById<ImageView>(R.id.every_news)!!
        }
    }

    // ViewPager에 들어갈 item
    private fun getNewsList(): ArrayList<Int> {
        return arrayListOf<Int>(
            R.drawable.news1,
            R.drawable.news2,
            R.drawable.news3,
            R.drawable.news4,
            R.drawable.news5
        )
    }
}