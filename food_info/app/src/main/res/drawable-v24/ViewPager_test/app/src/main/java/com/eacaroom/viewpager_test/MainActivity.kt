package com.eacaroom.viewpager_test

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* ViewPager 추가 */
        val viewPager_news : ViewPager2 = findViewById(R.id.viewPager_news)
        viewPager_news.offscreenPageLimit = 1 // 1개의 페이지 미리 로드
        viewPager_news.adapter = ViewPagerAdapter(getNewsList()) //adapter 생성
        viewPager_news.orientation = ViewPager2.ORIENTATION_HORIZONTAL // 방향 가로로 swipe
    }

    // ViewPager에 들어갈 item
    private fun getNewsList(): ArrayList<Int>{
        return arrayListOf<Int>(R.drawable.news1, R.drawable.news2, R.drawable.news3, R.drawable.news4, R.drawable.news5 )
    }
}