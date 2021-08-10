package com.cookandroid.realtest

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.io.BufferedWriter
import java.io.File
import java.io.FileInputStream
import java.io.FileWriter
import java.lang.StringBuilder

class ResultActivity2 : AppCompatActivity(){

    private lateinit var barChart : BarChart
    private lateinit var horChart1 : HorizontalBarChart
    private lateinit var horChart2 : HorizontalBarChart
    private lateinit var horChart3 : HorizontalBarChart
    private lateinit var horChart4 : HorizontalBarChart
    private lateinit var horChart5 : HorizontalBarChart
    private lateinit var horChart6 : HorizontalBarChart
    private lateinit var horChart7 : HorizontalBarChart
    private lateinit var horChart8 : HorizontalBarChart
    private lateinit var horChartSet : ArrayList<HorizontalBarChart>
    private lateinit var pieChart1 : PieChart
    private lateinit var pieChart2 : PieChart
    private lateinit var pieChart3 : PieChart
    private lateinit var pieChart4 : PieChart
    private lateinit var pieChart5 : PieChart
    private lateinit var pieChart6 : PieChart
    private lateinit var pieChart7 : PieChart
    private lateinit var pieChart8 : PieChart
    private lateinit var pieChartSet : ArrayList<PieChart>
    private lateinit var chart1_text : TextView
    private lateinit var chart2_text : TextView
    private lateinit var chart3_text : TextView
    private lateinit var chart4_text : TextView
    private lateinit var chart5_text : TextView
    private lateinit var chart6_text : TextView
    private lateinit var chart7_text : TextView
    private lateinit var chart8_text : TextView
    private lateinit var chartTextSet : ArrayList<TextView>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result2)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_result2)
        toolbar.title = ""
        toolbar.inflateMenu(R.menu.menu_nothing)
        setSupportActionBar(toolbar)

        val scrollView2=findViewById<ScrollView>(R.id.scrollView_result2)
        findViewById<FloatingActionButton>(R.id.fab2_result2).setOnClickListener {
            scrollView2.post {
                scrollView2.fullScroll(ScrollView.FOCUS_UP)
            }
        } //최상위로 스크롤 floating button

        val buttons = arrayListOf<Button>(
            findViewById(R.id.button1_result2),
            findViewById(R.id.button2_result2),
            findViewById(R.id.button3_result2),
            findViewById(R.id.button4_result2),
            findViewById(R.id.button5_result2),
            findViewById(R.id.button6_result2),
            findViewById(R.id.button7_result2),
            findViewById(R.id.button8_result2)
        ) //각 영양성분 button array 설정

        val linearlayouts = arrayListOf<LinearLayout>(
            findViewById(R.id.chart1),
            findViewById(R.id.chart2),
            findViewById(R.id.chart3),
            findViewById(R.id.chart4),
            findViewById(R.id.chart5),
            findViewById(R.id.chart6),
            findViewById(R.id.chart7),
            findViewById(R.id.chart8)
        ) // 각 차트 레이아웃 연결

        for(i in 0..7) {
            buttons[i].setOnClickListener {
                scrollView2.post {
                    scrollView2.smoothScrollTo(0, linearlayouts[i].top - 32)
                }
            }
        } // 반복문을 이용한 buttons의 클릭 설정






        var text_allergy = findViewById<TextView>(R.id.text_allergy_result2)
        var inFs : FileInputStream = openFileInput("user_allergy.txt")
        var txt = ByteArray(13)
        inFs.read(txt)
        inFs.close()

        var tmp_allergy = arrayListOf("계란", "우유", "치즈", "밀가루", "참깨", "콩", "게", "새우", "조개", "복숭아", "오이", "마늘", "돼지고기")
        var user_info_allergy = txt.toString(Charsets.UTF_8).trim()
        var str_temp = StringBuilder()
        for(i in 0..user_info_allergy.length-1){
            if(user_info_allergy[i] == '1') str_temp.append(tmp_allergy[i] + " ")
        }
        text_allergy.text = str_temp.toString()

        var text_disease = findViewById<TextView>(R.id.text_disease_result2)
        inFs = openFileInput("user_disease.txt")
        txt = ByteArray(8)
        inFs.read(txt)
        inFs.close()

//        var tmp_disease = arrayListOf("열량", "나트륨", "탄수화물", "당류", "식이섬유", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
        var tmp_disease = arrayListOf("나트륨", "탄수화물", "당류", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
        var user_info_disease = txt.toString(Charsets.UTF_8).trim()
        str_temp = StringBuilder()
        for(i in 0..user_info_disease.length-1){
            if(user_info_disease[i] == '1') str_temp.append(tmp_disease[i] + " ")
        }
        text_disease.text = str_temp.toString()


        //"나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질"
        val c1 = arrayOf<Float>(313.18f, 40.09f, 14.84f, 11.99f, 0.11f, 5.70f, 34.43f, 7.60f)
        val c2 = arrayOf<Float>(395.56f, 16.63f, 4.18f, 12.21f, 0f, 3.00f, 48.81f, 9.49f)
        val c3 = arrayOf<Float>(199.32f, 8.86f, 7.13f, 8.81f, 0f, 1.51f, 5.07f, 2.94f)
        val c4 = arrayOf<Float>(59.18f, 18.69f, 16.54f, 8.04f, 0.08f, 5.61f, 17.69f, 2.93f)
        val c5 = arrayOf<Float>(95.78f, 50.50f, 38.24f, 31.22f, 0.14f, 19.27f, 8.76f, 6.82f)
        val c6 = arrayOf<Float>(81.49f, 95.45f, 54.61f, 2.2f, 0.018f, 1.25f, 1.12f, 5.39f)
        val c7 = arrayOf<Float>(241.70f, 56.08f, 14.80f, 14.80f, 0.09f, 5.97f, 13.00f, 6.62f)
        val c8 = arrayOf<Float>(21.82f, 23.31f, 21.10f, 0.79f, 0.011f, 0.73f, 0.12f, 0.68f)
        val c9 = arrayOf<Float>(330.96f, 63.61f, 22.04f, 2.95f, 0f, 0.95f, 0f, 4.83f)
        val c10 = arrayOf<Float>(8.59f, 6.90f, 8.22f, 0.02f, 0f, 0.004f, 0f, 0.04f)
        val c11 = arrayOf<Float>(16.66f, 12.53f, 10.98f, 0.09f, 0.003f, 0.04f, 0.02f, 0.33f)
        val c12 = arrayOf<Float>(86.37f, 24.23f, 18.21f, 4.58f, 0.001f, 3.03f, 5.10f, 3.55f)
        val c13 = arrayOf<Float>(589.00f, 4.36f, 1.29f, 0.87f, 0f, 0f, 0.79f, 1.37f)
        val c14 = arrayOf<Float>(301.73f, 14.16f, 0.12f, 3.07f, 0f, 1.17f, 11.20f, 2.32f)
        val c15 = arrayOf<Float>(66.69f, 6.20f, 5.93f, 4.16f, 0.10f, 2.44f, 11.30f, 3.99f)
        val c16 = arrayOf<Float>(66.20f, 16.49f, 7.91f, 0.41f, 0f, 0.13f, 0.26f, 1.75f)
        val c17 = arrayOf<Float>(29.73f, 55.08f, 48.39f, 0.49f, 0.003f, 0.19f, 2.15f, 0.46f)
        val c18 = arrayOf<Float>(744.74f, 13.52f, 9.66f, 7.14f, 0.06f, 1.88f, 4.83f, 3.07f)

        val categories = arrayOf(c1, c2, c3, c4, c5, c6, c7, c8,
            c9, c10, c11, c12, c13, c14, c15, c16, c17, c18)

        val maxAmount = arrayOf<Float>(2000f, 324f, 100f, 51f, 0f, 15f, 300f, 55f)



        var intent = getIntent()
        var gram = intent.getIntExtra("gram", 100)
        var category_idx = intent.getIntExtra("category", 0)
        var info_arr_per = intent.getFloatArrayExtra("info_arr_per")
        var info_arr_gram = intent.getFloatArrayExtra("info_arr_gram")



        for(i in 0..7) {
            categories[category_idx][i] = categories[category_idx][i] * (gram / 100)
        }

        var test_text = findViewById<TextView>(R.id.text_result2)
        test_text.text = makeResultText(categories, category_idx, info_arr_gram, user_info_disease)
        initView()
        initBarChart()
        initHorChart()
        initPieChart()
        setBarChart(info_arr_gram, info_arr_per)
        setHorChart(categories[category_idx], info_arr_gram, info_arr_per, maxAmount)
        setPieChart(info_arr_gram, info_arr_per)
        setChartText(categories, category_idx, info_arr_gram, info_arr_per)

    }

    private fun makeResultText(categories: Array<Array<Float>>,
                               category_idx: Int,
                               items_gram: FloatArray,
                               user_info: String): String {

        val name = arrayListOf("나트륨이","탄수화물이","당류가","지방이","트랜스지방이","포화지방이","콜레스테롤이","단백질이")
        val categoryName = arrayListOf("빵", "샌드위치", "샐러드",
            "아이스크림", "초콜릿", "사탕",
            "과자", "젤리", "시리얼",
            "탄산음료", "과/채음료", "커피",
            "라면", "김치", "유제품",
            "유산균", "잼", "소스")
        var sb = StringBuilder()

        for(i in 0..7) {
            if(user_info[i] == '1') {
                val a = items_gram[i].toInt()
                val b = categories[category_idx][i].toInt()
                if(a > b) {
                    var temp = a - b
                    sb.append("\n이 제품은 ${name[i]} 많습니다.\n")
                    sb.append("(${categoryName[category_idx]}의 평균에 비해 $temp 만큼 많습니다.)\n")
                }
                else if(a == b){
                    sb.append("\n이 제품의 ${name[i]} ${categoryName[category_idx]}의 평균치 입니다.\n")
                }
                else{
                    var temp = b - a
                    sb.append("\n이 제품은 ${name[i]} 적습니다.\n")
                    sb.append("(${categoryName[category_idx]}의 평균에 비해 $temp 만큼 적습니다.)\n")
                }
            }
        }

        return sb.toString()
    }


    private fun initView(){
        barChart = findViewById(R.id.bar_chart)
        horChart1 = findViewById(R.id.hor_chart1)
        horChart2 = findViewById(R.id.hor_chart2)
        horChart3 = findViewById(R.id.hor_chart3)
        horChart4 = findViewById(R.id.hor_chart4)
        horChart5 = findViewById(R.id.hor_chart5)
        horChart6 = findViewById(R.id.hor_chart6)
        horChart7 = findViewById(R.id.hor_chart7)
        horChart8 = findViewById(R.id.hor_chart8)
        horChartSet = ArrayList<HorizontalBarChart>()
        horChartSet.add(horChart1)
        horChartSet.add(horChart2)
        horChartSet.add(horChart3)
        horChartSet.add(horChart4)
        horChartSet.add(horChart5)
        horChartSet.add(horChart6)
        horChartSet.add(horChart7)
        horChartSet.add(horChart8)

        pieChart1 = findViewById(R.id.pie_chart1)
        pieChart2 = findViewById(R.id.pie_chart2)
        pieChart3 = findViewById(R.id.pie_chart3)
        pieChart4 = findViewById(R.id.pie_chart4)
        pieChart5 = findViewById(R.id.pie_chart5)
        pieChart6 = findViewById(R.id.pie_chart6)
        pieChart7 = findViewById(R.id.pie_chart7)
        pieChart8 = findViewById(R.id.pie_chart8)
        pieChartSet = ArrayList<PieChart>()
        pieChartSet.add(pieChart1)
        pieChartSet.add(pieChart2)
        pieChartSet.add(pieChart3)
        pieChartSet.add(pieChart4)
        pieChartSet.add(pieChart5)
        pieChartSet.add(pieChart6)
        pieChartSet.add(pieChart7)
        pieChartSet.add(pieChart8)

        chart1_text = findViewById(R.id.chart1_text)
        chart2_text = findViewById(R.id.chart2_text)
        chart3_text = findViewById(R.id.chart3_text)
        chart4_text = findViewById(R.id.chart4_text)
        chart5_text = findViewById(R.id.chart5_text)
        chart6_text = findViewById(R.id.chart6_text)
        chart7_text = findViewById(R.id.chart7_text)
        chart8_text = findViewById(R.id.chart8_text)
        chartTextSet = ArrayList<TextView>()
        chartTextSet.add(chart1_text)
        chartTextSet.add(chart2_text)
        chartTextSet.add(chart3_text)
        chartTextSet.add(chart4_text)
        chartTextSet.add(chart5_text)
        chartTextSet.add(chart6_text)
        chartTextSet.add(chart7_text)
        chartTextSet.add(chart8_text)

    }

    private fun setChartText(categories: Array<Array<Float>>,
                             category_idx: Int,
                             items_gram: FloatArray,
                             items_per: FloatArray){
        val name = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        for(i in 0..7){
            var sb = StringBuilder()
            if(items_gram[i] > categories[category_idx][i]) sb.append("해당 제품은 타 제품보다 ${name[i]} 함량이 높습니다.\n")
            else sb.append("해당 제품은 타 제품보다 ${name[i]} 함량이 낮습니다.\n")

            if(items_per[i] > 60) sb.append("1일 권장 섭취량의 ${items_per[i]}%로 과다합니다.")
            else if(items_per[i] > 30) sb.append("1일 권장 섭취량의 ${items_per[i]}%로 적당합니다.")
            else sb.append("1일 권장 섭취량의 ${items_per[i]}%로 부족합니다.")

            chartTextSet[i].text = sb.toString()
        }
    }
    private fun initBarChart(){
        barChart.run{
            axisRight.isEnabled = false
            description.isEnabled = false
            legend.isEnabled = false // 차트 범례
            setMaxVisibleValueCount(10) // 최대 갯수
            setDrawValueAboveBar(true) // ??
            setTouchEnabled(false) // 터치
            setPinchZoom(false) // 확대
            setDrawBarShadow(false) // 그림자
            setDrawGridBackground(false) // 격자구조
            animateY(800) // 애니메이션

            xAxis.run { // 아래 라벨 x축
                isEnabled = true // 라벨 표시 설정
                position = XAxis.XAxisPosition.BOTTOM // 라벨 위치 설정
                valueFormatter = BarLabelFormatter() // 라벨 값 포멧 설정
                setDrawGridLines(false) // 격자구조
                setDrawAxisLine(false) // 그림
                granularity = 1f // 간격 설정
                textSize = 10f // 라벨 크기
                textColor = ContextCompat.getColor(context, R.color.black)
            }

            axisLeft.run { // 왼쪽 y축
                isEnabled = true
                axisMinimum = 0f // 최소값
                axisMaximum = 100f // 최대값
                granularity = 20f // 값 만큼 라인선 설정
                setDrawLabels(true) // 값 셋팅 설정
                setDrawAxisLine(true)
                textColor = ContextCompat.getColor(context, R.color.black)
            }
        }
    }

    private fun initHorChart(){
        for(i in 0..7) {
            horChartSet[i].run {
                legend.isEnabled = false
                description.isEnabled = false
                setTouchEnabled(false)
                setPinchZoom(false)
                setDrawBarShadow(false)

                xAxis.run{
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawLabels(true)
                    valueFormatter = HorLabelFormatter()
                    setDrawGridLines(false)
                    setDrawAxisLine(false)
                    granularity = 1f
                    textSize = 10f
                    textColor = ContextCompat.getColor(context, R.color.black)
//                    setDrawAxisLine(false)
                }

                axisLeft.run { // 왼쪽 y축
                    axisMinimum = 0f // 최소값
//                    axisMaximum = 100f // 최대값
//                    granularity = 20f // 값 만큼 라인선 설정
                    setDrawLabels(true) // 값 셋팅 설정
                    setDrawAxisLine(true)
                    textColor = ContextCompat.getColor(context, R.color.black)
                }

                axisRight.run { // 왼쪽 y축
                    axisMinimum = 0f // 최소값
//                    axisMaximum = 100f // 최대값
//                    granularity = 20f // 값 만큼 라인선 설정
                    setDrawLabels(true) // 값 셋팅 설정
                    setDrawAxisLine(true)
                    textColor = ContextCompat.getColor(context, R.color.black)
                }

                animateY(800)
            }
        }
    }

    private fun setHorChart(categoryAmount : Array<Float>,
                            items_gram: FloatArray,
                            items_per: FloatArray,
                            maxAmount: Array<Float>){
        val a = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        for(i in 0..7) {
            val colorList = ArrayList<Int>()
            val entries = ArrayList<BarEntry>()
            entries.add(BarEntry(3f, maxAmount[i]))
            entries.add(BarEntry(2f, categoryAmount[i]))
            entries.add(BarEntry(1f, items_gram[i]!!))

            colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.chart_green))
            colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.chart_yellow))
            if(items_gram[i] > categoryAmount[i]) colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.chart_red))
            else colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.main_color))

            val d = BarDataSet(entries, "DataSet")
                .apply {
                    setDrawIcons(false)
                    setDrawValues(true)
                    colors = colorList
                    valueTextColor = ContextCompat.getColor(this@ResultActivity2, R.color.black)
                    valueTextSize = 10f
                }

            val dataSet = ArrayList<IBarDataSet>()
            dataSet.add(d)

            val data = BarData(dataSet)
                .apply {
                    barWidth = 0.7f
                }

            horChartSet[i].data = data
        }
    }

    private fun setBarChart(items_gram: FloatArray,
                            items_per: FloatArray){

        val a = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        val entries = ArrayList<BarEntry>()
        for(i in 0..7){
            entries.add(BarEntry(i+1f, items_per[i]!!))
        }

        val colorList = ArrayList<Int>()
//        for(c in ColorTemplate.COLORFUL_COLORS) colorList.add(c)
        colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.main_color))

        val d = BarDataSet(entries, "DataSet")
            .apply{
                setDrawIcons(false)
                setDrawValues(true)
                colors = colorList
                valueFormatter = PerFormatter()
                valueTextColor = ContextCompat.getColor(this@ResultActivity2, R.color.black)
                valueTextSize = 15f
            }

        val dataSet = ArrayList<IBarDataSet>()
        dataSet.add(d)

        val data = BarData(dataSet)
            .apply{
                barWidth = 0.7f
            }

        barChart.data = data
    }

    private fun initPieChart(){
        val a = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        for(i in 0..pieChartSet.size-1) {
            pieChartSet[i].run {
                description.isEnabled = true
                description.textSize = 15f
                description.text = "(하루 100% 기준)"

                legend.isEnabled = false
                setUsePercentValues(false)
                isDrawHoleEnabled = false
                isRotationEnabled = false
                animateY(800)
            }
        }
    }

    private fun setPieChart(items_gram: FloatArray,
                            items_per: FloatArray){
        val a = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        val colorList = ArrayList<Int>()
        colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.main_color))
        colorList.add(ContextCompat.getColor(this@ResultActivity2, R.color.shadow))

        for(i in 0..pieChartSet.size-1) {
            val entries = ArrayList<PieEntry>()

            val item = items_per[i]!!

            entries.add(PieEntry(item, a[i]))
            entries.add(PieEntry(100f - item, "REST"))

            val d = PieDataSet(entries, "DataSet")
                .apply {
                    colors = colorList
                    sliceSpace = 3f
                    selectionShift = 5f
                    valueFormatter = PerFormatter()
                    valueTextColor = ContextCompat.getColor(this@ResultActivity2, R.color.white)
                    valueTextSize = 20f
                }

            val pieData = PieData(d)
            pieChartSet[i].apply {
                data = pieData
            }

        }
    }

    inner class BarLabelFormatter : ValueFormatter(){
        private val labels = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        override fun getAxisLabel(
            value: Float,
            axis: AxisBase?
        ): String {
            return labels.getOrNull(value.toInt()-1)?: value.toString()
        }
    }

    inner class HorLabelFormatter : ValueFormatter(){
        private val labels = arrayOf("제품 함량", "카테고리 평균 함량", "1일 권장 섭취량")
        override fun getAxisLabel(value: Float, axis: AxisBase?): String {
            return labels.getOrNull(value.toInt()-1)?: value.toString()
        }
    }

    inner class PerFormatter : ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            val v = value.toString().split(".")
            return v[0] + "%"
        }
    }

}