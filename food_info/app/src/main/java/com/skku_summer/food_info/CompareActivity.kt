package com.skku_summer.food_info

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.skku_summer.food_info.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.LegendEntry
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class CompareActivity : AppCompatActivity() {

    lateinit var barChart_compare : BarChart
    private lateinit var horChart1 : HorizontalBarChart
    private lateinit var horChart2 : HorizontalBarChart
    private lateinit var horChart3 : HorizontalBarChart
    private lateinit var horChart4 : HorizontalBarChart
    private lateinit var horChart5 : HorizontalBarChart
    private lateinit var horChart6 : HorizontalBarChart
    private lateinit var horChart7 : HorizontalBarChart
    private lateinit var horChart8 : HorizontalBarChart
    private lateinit var horChartSet : ArrayList<HorizontalBarChart>

    private lateinit var chart1_text : TextView
    private lateinit var chart2_text : TextView
    private lateinit var chart3_text : TextView
    private lateinit var chart4_text : TextView
    private lateinit var chart5_text : TextView
    private lateinit var chart6_text : TextView
    private lateinit var chart7_text : TextView
    private lateinit var chart8_text : TextView
    private lateinit var chartTextSet : ArrayList<TextView>

    private lateinit var filename : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compare)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_compare)
        toolbar.title = "  Compare"
        toolbar.inflateMenu(R.menu.menu_nothing)
        setSupportActionBar(toolbar)

        val scrollView = findViewById<ScrollView>(R.id.scrollView_compare)
        findViewById<FloatingActionButton>(R.id.fab2_compare).setOnClickListener {
            scrollView.post{
                scrollView.fullScroll(ScrollView.FOCUS_UP)
            }
        }

        val buttons = arrayListOf<Button>(
            findViewById(R.id.button1_compare),
            findViewById(R.id.button2_compare),
            findViewById(R.id.button3_compare),
            findViewById(R.id.button4_compare),
            findViewById(R.id.button5_compare),
            findViewById(R.id.button6_compare),
            findViewById(R.id.button7_compare),
            findViewById(R.id.button8_compare)
        ) //??? ???????????? button array ??????

        val linearlayouts = arrayListOf<LinearLayout>(
            findViewById(R.id.chart1_compare),
            findViewById(R.id.chart2_compare),
            findViewById(R.id.chart3_compare),
            findViewById(R.id.chart4_compare),
            findViewById(R.id.chart5_compare),
            findViewById(R.id.chart6_compare),
            findViewById(R.id.chart7_compare),
            findViewById(R.id.chart8_compare)
        ) // ??? ?????? ???????????? ??????

        for(i in 0..7) {
            buttons[i].setOnClickListener {
                scrollView.post {
                    scrollView.smoothScrollTo(0, linearlayouts[i].top - 32)
                }
            }
        } // ???????????? ????????? buttons??? ?????? ??????

        var intent = getIntent()
        filename = intent.getStringExtra("filename")
        var inFs = openFileInput(filename + ".json")
        var isr = InputStreamReader(inFs)
        var bufferedReader = BufferedReader(isr)
        var sb = StringBuilder()
        while(true){
            var s = bufferedReader.readLine()
            if(s == null) break
            else sb.append(s)
        }

        var d = sb.toString()
        val jsonobj = JSONObject(d)
        var comp_per = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        var comp_gram = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        var temp_per = jsonobj.getJSONArray("arr_per")
        var temp_gram = jsonobj.getJSONArray("arr_gram")
        for(i in 0..7){
            comp_per[i] = temp_per[i].toString().toFloat()
            comp_gram[i] = temp_gram[i].toString().toFloat()
        }
        var info_arr_per = intent.getFloatArrayExtra("info_arr_per")
        var info_arr_gram = intent.getFloatArrayExtra("info_arr_gram")
        val maxAmount = arrayOf<Float>(2000f, 324f, 100f, 51f, 0f, 15f, 300f, 55f)


        val text_compare = findViewById<TextView>(R.id.text_compare)
        text_compare.text = "?????? ?????? : $filename"
        initView()
        initBarChart()
        initHorChart()
        setBarChart(info_arr_per, comp_per)
        setHorChart(info_arr_gram, comp_gram, maxAmount)
        setChartText(info_arr_gram, comp_gram)

    }


    private fun initView(){
        barChart_compare = findViewById(R.id.bar_chart_compare)
        horChart1 = findViewById(R.id.hor_chart1_compare)
        horChart2 = findViewById(R.id.hor_chart2_compare)
        horChart3 = findViewById(R.id.hor_chart3_compare)
        horChart4 = findViewById(R.id.hor_chart4_compare)
        horChart5 = findViewById(R.id.hor_chart5_compare)
        horChart6 = findViewById(R.id.hor_chart6_compare)
        horChart7 = findViewById(R.id.hor_chart7_compare)
        horChart8 = findViewById(R.id.hor_chart8_compare)
        horChartSet = ArrayList<HorizontalBarChart>()
        horChartSet.add(horChart1)
        horChartSet.add(horChart2)
        horChartSet.add(horChart3)
        horChartSet.add(horChart4)
        horChartSet.add(horChart5)
        horChartSet.add(horChart6)
        horChartSet.add(horChart7)
        horChartSet.add(horChart8)

        chart1_text = findViewById(R.id.chart1_text_compare)
        chart2_text = findViewById(R.id.chart2_text_compare)
        chart3_text = findViewById(R.id.chart3_text_compare)
        chart4_text = findViewById(R.id.chart4_text_compare)
        chart5_text = findViewById(R.id.chart5_text_compare)
        chart6_text = findViewById(R.id.chart6_text_compare)
        chart7_text = findViewById(R.id.chart7_text_compare)
        chart8_text = findViewById(R.id.chart8_text_compare)
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

    private fun initBarChart(){
        barChart_compare.run{
            axisRight.isEnabled = false
            description.isEnabled = false
            setMaxVisibleValueCount(17) // ?????? ??????
            setDrawValueAboveBar(true) // ??
            setTouchEnabled(false) // ??????
            setPinchZoom(false) // ??????
            setDrawBarShadow(false) // ?????????
            setDrawGridBackground(false) // ????????????
            animateY(800) // ???????????????

            legend.run{
                isEnabled = true
                extraBottomOffset = 15f
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                val legenditem1 = LegendEntry()
                legenditem1.formColor = ContextCompat.getColor(this@CompareActivity,
                    R.color.main_color
                )
                legenditem1.label = "?????? ??????"
                val legenditem2 = LegendEntry()
                legenditem2.formColor = ContextCompat.getColor(this@CompareActivity,
                    R.color.chart_yellow
                )
                legenditem2.label = "$filename ??????"
                val legendArray = arrayOf(legenditem1, legenditem2)
                setCustom(legendArray)
            }

            xAxis.run { // ?????? ?????? x???
                isEnabled = true // ?????? ?????? ??????
                position = XAxis.XAxisPosition.BOTTOM // ?????? ?????? ??????
                valueFormatter = BarLabelFormatter() // ?????? ??? ?????? ??????
                setDrawGridLines(false) // ????????????
                setDrawAxisLine(false) // ??????
                granularity = 1f // ?????? ??????
                textSize = 8f // ?????? ??????
                textColor = ContextCompat.getColor(context, R.color.black)
            }

            axisLeft.run { // ?????? y???
                isEnabled = true
                axisMinimum = 0f // ?????????
                axisMaximum = 100f // ?????????
                granularity = 20f // ??? ?????? ????????? ??????
                setDrawLabels(true) // ??? ?????? ??????
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
                    textColor = ContextCompat.getColor(context,
                        R.color.black
                    )
//                    setDrawAxisLine(false)
                }

                axisLeft.run { // ?????? y???
                    axisMinimum = 0f // ?????????
//                    axisMaximum = 100f // ?????????
//                    granularity = 20f // ??? ?????? ????????? ??????
                    setDrawLabels(true) // ??? ?????? ??????
                    setDrawAxisLine(true)
                    textColor = ContextCompat.getColor(context,
                        R.color.black
                    )
                }

                axisRight.run { // ?????? y???
                    axisMinimum = 0f // ?????????
//                    axisMaximum = 100f // ?????????
//                    granularity = 20f // ??? ?????? ????????? ??????
                    setDrawLabels(true) // ??? ?????? ??????
                    setDrawAxisLine(true)
                    textColor = ContextCompat.getColor(context,
                        R.color.black
                    )
                }

                animateY(800)
            }
        }
    }

    private fun setBarChart(items_per: FloatArray,
                            comp_per: FloatArray){

        val a = arrayOf("?????????","????????????","??????","??????","???????????????","????????????","???????????????","?????????")
        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        for(i in 0..7){
            entries1.add(BarEntry(0.8f + i, items_per[i]!!))
            entries2.add(BarEntry(1.2f + i, comp_per[i]!!))
        }

        val d1 = BarDataSet(entries1, "DataSet")
            .apply{
                setDrawIcons(false)
                setDrawValues(true)
                color = ContextCompat.getColor(this@CompareActivity,
                    R.color.main_color
                )
                valueFormatter = PerFormatter()
                valueTextColor = ContextCompat.getColor(this@CompareActivity,
                    R.color.black
                )
                valueTextSize = 8f
            }

        val d2 = BarDataSet(entries2, "DataSet")
            .apply{
                setDrawIcons(false)
                setDrawValues(true)
                color = ContextCompat.getColor(this@CompareActivity,
                    R.color.chart_yellow
                )
                valueFormatter = PerFormatter()
                valueTextColor = ContextCompat.getColor(this@CompareActivity,
                    R.color.black
                )
                valueTextSize = 8f
            }

        val dataSet = ArrayList<IBarDataSet>()
        dataSet.add(d1)
        dataSet.add(d2)

        val data = BarData(dataSet)
            .apply{
                barWidth = 0.4f
            }

        barChart_compare.data = data
    }

    private fun setHorChart(items_gram: FloatArray,
                            comp_gram: FloatArray,
                            maxAmount: Array<Float>){
        val a = arrayOf("?????????","????????????","??????","??????","???????????????","????????????","???????????????","?????????")
        for(i in 0..7) {
            val colorList = ArrayList<Int>()
            val entries = ArrayList<BarEntry>()
            entries.add(BarEntry(3f, maxAmount[i]))
            entries.add(BarEntry(2f, items_gram[i]))
            entries.add(BarEntry(1f, comp_gram[i]!!))

            colorList.add(ContextCompat.getColor(this@CompareActivity,
                R.color.chart_green
            ))
            colorList.add(ContextCompat.getColor(this@CompareActivity,
                R.color.main_color
            ))
            colorList.add(ContextCompat.getColor(this@CompareActivity,
                R.color.chart_yellow
            ))

            val d = BarDataSet(entries, "DataSet")
                .apply {
                    setDrawIcons(false)
                    setDrawValues(true)
                    colors = colorList
                    valueTextColor = ContextCompat.getColor(this@CompareActivity,
                        R.color.black
                    )
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

    private fun setChartText(items_gram: FloatArray,
                             comp_gram: FloatArray){
        val name = arrayOf("?????????","????????????","??????","??????","???????????????","????????????","???????????????","?????????")
        for(i in 0..7){
            var sb = StringBuilder()
            if(items_gram[i] > comp_gram[i]) sb.append("?????? ????????? ${filename}?????? ${name[i]} ????????? ${items_gram[i] - comp_gram[i]}?????? ????????????.")
            else if(items_gram[i] < comp_gram[i]) sb.append("?????? ????????? ${filename}?????? ${name[i]} ????????? ${comp_gram[i] - items_gram[i]}?????? ????????????.")
            else sb.append("?????? ????????? ${name[i]} ????????? ${filename}??? ????????? ????????????.")

            chartTextSet[i].text = sb.toString()
        }
    }

    inner class BarLabelFormatter : ValueFormatter(){
        private val labels = arrayOf("?????????","????????????","??????","??????","???????????????","????????????","???????????????","?????????")
        override fun getAxisLabel(
            value: Float,
            axis: AxisBase?
        ): String {
            return labels.getOrNull(value.toInt()-1)?: value.toString()
        }
    }

    inner class HorLabelFormatter : ValueFormatter(){
        private val labels = arrayOf("$filename ??????", "?????? ??????", "1??? ?????? ?????????")
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