package com.skku_summer.food_info

import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.skku_summer.food_info.R
import com.github.mikephil.charting.charts.BarChart
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
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.*


class ResultActivity : AppCompatActivity() {
    interface API2{
        @GET("/{product_name}")
        fun getSearch(@Path("product_name") product_name: String): Call<List<ResultGetSearch>>

    }

    var list = mutableListOf<ResultGetSearch>()
    var categoryName = arrayListOf("빵", "샌드위치", "샐러드",
        "아이스크림", "초콜릿", "사탕",
        "과자", "젤리", "시리얼",
        "탄산음료", "과/채음료", "커피",
        "라면", "김치", "유제품",
        "유산균", "잼", "소스")

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



    private lateinit var barChart_main : BarChart
    private var gram : Float = 100f
    private var category_idx : Int = 0
    private lateinit var ttest_str : String
    private lateinit var info_arr_per : FloatArray
    private lateinit var info_arr_gram : FloatArray
    private lateinit var category_per : FloatArray

    private lateinit var compare_dialog : View
    private lateinit var search_dialog : View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_result)
        toolbar.title=""
        toolbar.inflateMenu(R.menu.menu_result)
        setSupportActionBar(toolbar)

        findViewById<FloatingActionButton>(R.id.fab2_result).setOnClickListener {
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        } //메인으로 floating button

        findViewById<FloatingActionButton>(R.id.fab_result).setOnClickListener {
            val intent = Intent(this@ResultActivity, CameraActivity::class.java)
            startActivity(intent)
            finish()
        } //카메라로 floating button

        val buttons = arrayListOf<Button>(
            findViewById(R.id.button1),
            findViewById(R.id.button2),
            findViewById(R.id.button3),
            findViewById(R.id.button4),
            findViewById(R.id.button5),
            findViewById(R.id.button6),
            findViewById(R.id.button7),
            findViewById(R.id.button8)
        ) //각 영양성분 button array 설정
        var btn_on_flag = arrayOf<Boolean>(false, false, false, false, false, false, false, false)
        var labels: ArrayList<String> = ArrayList()

        val view_more = findViewById<Button>(R.id.view_more)
        view_more.setOnClickListener {
            var intentt = Intent(this@ResultActivity, ResultActivity2::class.java)
            intentt.putExtra("info_arr_gram", info_arr_gram)
            intentt.putExtra("info_arr_per", info_arr_per)
            intentt.putExtra("gram", gram)
            intentt.putExtra("category", category_idx)
            startActivity(intentt)
//            overridePendingTransition(0, 0)
        }

        val save_button = findViewById<Button>(R.id.save_btn)
        save_button.setOnClickListener {
            showsavedialog()
        }

        var text_allergy = findViewById<TextView>(R.id.text_allergy_result)
        var inFs: FileInputStream = openFileInput("user_allergy.txt")
        var txt = ByteArray(13)
        inFs.read(txt)
        inFs.close()

        var tmp_allergy = arrayListOf("계란", "우유", "치즈", "밀가루", "참깨", "콩", "게", "새우", "조개", "복숭아", "오이", "마늘", "돼지고기")
        var user_info_allergy = txt.toString(Charsets.UTF_8).trim()
        var str_temp = StringBuilder()
        for (i in 0..user_info_allergy.length - 1) {
            if (user_info_allergy[i] == '1') str_temp.append(tmp_allergy[i] + " ")
        }
        text_allergy.text = str_temp.toString()

        var text_disease = findViewById<TextView>(R.id.text_disease_result)
        inFs = openFileInput("user_disease.txt")
        txt = ByteArray(8)
        inFs.read(txt)
        inFs.close()

//        var tmp_disease = arrayListOf("열량", "나트륨", "탄수화물", "당류", "식이섬유", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
        var tmp_disease = arrayListOf("나트륨", "탄수화물", "당류", "지방", "트랜스지방", "포화지방", "콜레스테롤", "단백질")
        var user_info_disease = txt.toString(Charsets.UTF_8).trim()
        str_temp = StringBuilder()
        for (i in 0..user_info_disease.length - 1) {
            if (user_info_disease[i] == '1') {
                str_temp.append(tmp_disease[i] + " ")
                btn_on_flag[i] = true
                labels.add(tmp_disease[i])
            }
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

        val categories = arrayOf(
            c1, c2, c3, c4, c5, c6, c7, c8,
            c9, c10, c11, c12, c13, c14, c15, c16, c17, c18
        )

        val maxAmount = arrayOf<Float>(2000f, 324f, 100f, 51f, 0f, 15f, 300f, 55f)

        var intent = getIntent()

        if (intent.hasExtra("filename")) {
//            save_button.visibility = View.GONE
            var filename = intent.getStringExtra("filename")
            Log.i("AAAAA", "$filename")

            var inFs = openFileInput(filename + ".json")
            var isr = InputStreamReader(inFs)
            var bufferedReader = BufferedReader(isr)
            var sb = StringBuilder()
            while (true) {
                var s = bufferedReader.readLine()
                if (s == null) break
                else sb.append(s)
            }

            var d = sb.toString()
            Log.i("AAAAA", "$d")
            val jsonobj = JSONObject(d)
            category_idx = jsonobj.get("category_idx").toString().toInt()
            // 파일 이름으로 읽기
            gram = jsonobj.get("gram").toString().toFloat()
            ttest_str = "test"
            var temp1 = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            var temp2 = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
            var temp_per = jsonobj.getJSONArray("arr_per")
            var temp_gram = jsonobj.getJSONArray("arr_gram")
            for (i in 0..7) {
                temp1[i] = temp_per[i].toString().toFloat()
                temp2[i] = temp_gram[i].toString().toFloat()
            }
            info_arr_per = temp1
            info_arr_gram = temp2
        } else {
            gram = intent.getFloatExtra("gram", 100f)
            category_idx = intent.getIntExtra("category", 0)
            ttest_str = intent.getStringExtra("test")
            info_arr_per = intent.getFloatArrayExtra("info_arr_per")
            info_arr_gram = intent.getFloatArrayExtra("info_arr_gram")
            Log.i("MyCamApp", "$category_idx")
        }


        category_per = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        for (i in 0..7) {
            categories[category_idx][i] = categories[category_idx][i] * (gram / 100f)
            if (maxAmount[i] == 0f) category_per[i] = 0f
            else category_per[i] = categories[category_idx][i] / maxAmount[i] * 100f
        }


        for (i in 0..7) {
            if (btn_on_flag[i] == true) {
                buttons[i].setBackgroundResource(R.drawable.custom_button_clicked)
            }
            else{
                buttons[i].setBackgroundResource(R.drawable.custom_button_notclick)
            }
            buttons[i].setOnClickListener {
                if (btn_on_flag[i] == true) {
                    buttons[i].setBackgroundResource(R.drawable.custom_button_notclick)
                    btn_on_flag[i] = false
                    setBarChart(btn_on_flag, info_arr_per, category_per)
                    barChart_main.invalidate()
                } else {
                    buttons[i].setBackgroundResource(R.drawable.custom_button_clicked)
                    btn_on_flag[i] = true
                    setBarChart(btn_on_flag, info_arr_per, category_per)
                    barChart_main.invalidate()
                }
            }
        } // 반복문을 이용한 buttons의 클릭 설정


        initView()
        initBarChart(labels)
        setBarChart(btn_on_flag, info_arr_per, category_per)

        val product_name=categoryName[category_idx]
        val BASE_URL_API = "http://115.85.180.148:5000"
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL_API)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(API2::class.java)
        val callGetSearch = api.getSearch(product_name)

        callGetSearch.enqueue(object : Callback<List<ResultGetSearch>> {
            override fun onResponse(
                call: Call<List<ResultGetSearch>>,
                response: Response<List<ResultGetSearch>>
            ) {

                Log.d(ContentValues.TAG, "성공 : ${response.raw()}")
                var data : List<ResultGetSearch>? = response?.body()
                if(list.isEmpty()){
                    for ( i in data!!) {
                        i.let {
                            val title = it.title
                            val content = it.content
                            val imageurl= it.imageurl
                            val product_url=it.product_url
                            list.add(
                                ResultGetSearch(
                                    title,
                                    content,
                                    imageurl,
                                    product_url
                                )
                            )

                            Log.i("data", i.toString())

                        }

                    }
                }


            }

            override fun onFailure(call: Call<List<ResultGetSearch>>, t: Throwable) {
                Log.d(ContentValues.TAG, "실패 : $t")
            }
        })

    }





    private fun setBarChart(flag: Array<Boolean>,
                            items_per: FloatArray,
                            category_per: FloatArray){

        val a = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        var labels : ArrayList<String> = ArrayList()
        val colorList = ArrayList<Int>()
        val entries1 = ArrayList<BarEntry>()
        val entries2 = ArrayList<BarEntry>()
        var p = 0f
        for(i in 0..7){
            if(flag[i]) {
                entries1.add(BarEntry(0.9f + p, items_per[i]!!))
                entries2.add(BarEntry(1.2f + p++, category_per[i]!!))
                labels.add(a[i])

                if(items_per[i] > category_per[i]) colorList.add(ContextCompat.getColor(this@ResultActivity,
                    R.color.chart_red
                ))
                else colorList.add(ContextCompat.getColor(this@ResultActivity,
                    R.color.main_color
                ))
            }
        }

        val d1 = BarDataSet(entries1, "DataSet")
            .apply{
                setDrawIcons(false)
                setDrawValues(true)
                colors = colorList
                valueFormatter = PerFormatter()
                valueTextColor = ContextCompat.getColor(this@ResultActivity,
                    R.color.black
                )
                valueTextSize = 14f
            }
        val d2 = BarDataSet(entries2, "DataSet")
            .apply{
                setDrawIcons(false)
                setDrawValues(true)
                color = ContextCompat.getColor(this@ResultActivity,
                    R.color.chart_yellow
                )
                valueFormatter = PerFormatter()
                valueTextColor = ContextCompat.getColor(this@ResultActivity,
                    R.color.black
                )
                valueTextSize = 14f
            }

        val dataSet = ArrayList<IBarDataSet>()
        dataSet.add(d2)
        dataSet.add(d1)

        val data = BarData(dataSet)
            .apply{
                barWidth = 0.5f
            }

        barChart_main.data = data
        barChart_main.xAxis.valueFormatter = BarLabelFormatter(labels)
    }


    private fun initView() {
        barChart_main = findViewById(R.id.bar_chart_main)
    }


    private fun initBarChart(labels: ArrayList<String>){
        barChart_main.run{
            axisRight.isEnabled = false
            description.isEnabled = false
            setMaxVisibleValueCount(17) // 최대 갯수
            setDrawValueAboveBar(true) // ??
            setTouchEnabled(false) // 터치
            setPinchZoom(false) // 확대
            setDrawBarShadow(false) // 그림자
            setDrawGridBackground(false) // 격자구조
            animateY(800) // 애니메이션

            legend.run{
                isEnabled = true
                extraBottomOffset = 15f
                verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                val legenditem1 = LegendEntry()
                legenditem1.formColor = ContextCompat.getColor(this@ResultActivity,
                    R.color.main_color
                )
                legenditem1.label = "제품 함량"
                val legenditem2 = LegendEntry()
                legenditem2.formColor = ContextCompat.getColor(this@ResultActivity,
                    R.color.chart_red
                )
                legenditem2.label = "제품 함량 (주의)"
                val legenditem3 = LegendEntry()
                legenditem3.formColor = ContextCompat.getColor(this@ResultActivity,
                    R.color.chart_yellow
                )
                legenditem3.label = "카테고리 평균 함량"
                val legendArray = arrayOf(legenditem1, legenditem2, legenditem3)
                setCustom(legendArray)
            }

            xAxis.run { // 아래 라벨 x축
                isEnabled = true // 라벨 표시 설정
                position = XAxis.XAxisPosition.BOTTOM // 라벨 위치 설정
                valueFormatter = BarLabelFormatter(labels) // 라벨 값 포멧 설정
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


    fun showsavedialog(){
        val builder = AlertDialog.Builder(this@ResultActivity)
        builder.setTitle("제품 정보 입력")

        val input = EditText(this)
        input.setHint("오리온 초코파이")
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

                var temp = JSONObject()
                temp.put("category_idx", category_idx)
                temp.put("gram", gram)
                var temp1 = JSONArray()
                var temp2 = JSONArray()
                for(i in 0..7) {
                    temp1.put(info_arr_per[i])
                    temp2.put(info_arr_gram[i])
                }
                temp.put("arr_per", temp1)
                temp.put("arr_gram", temp2)
                var data = temp.toString()
                bufferWriter.write(data)
                bufferWriter.close()

                Toast.makeText(applicationContext, "'$f_name' 저장되었습니다.", Toast.LENGTH_SHORT).show()
            }
            else{ Toast.makeText(applicationContext, "'$f_name' 파일이 존재합니다.", Toast.LENGTH_SHORT).show() }
        })

        builder.setNegativeButton("취소", DialogInterface.OnClickListener{ dialog, which ->
            dialog.cancel()
            Toast.makeText(applicationContext, "취소되었습니다.", Toast.LENGTH_SHORT).show()
        })

        builder.show()
    }

    inner class BarLabelFormatter(labels: ArrayList<String>) : ValueFormatter(){
        private  val labels = labels
        override fun getAxisLabel(
            value: Float,
            axis: AxisBase?
        ): String {
            return labels.getOrNull(value.toInt()-1)?: value.toString()
        }
    }

    inner class PerFormatter : ValueFormatter(){
        override fun getFormattedValue(value: Float): String {
            val v = value.toString().split(".")
            return v[0] + "%"
        }
    }

    data class ResultGetSearch(
        @SerializedName("title") val title: String,
        @Expose
        @SerializedName("content") val content: String,
        @SerializedName("imageurl") val imageurl: String,
        @SerializedName("product_url") val product_url: String
    )


    inner class MyListAdapter(var mCtx:Context, var resource:Int, var items:List<ResultGetSearch>)
        :ArrayAdapter<ResultGetSearch>( mCtx , resource , items ){
        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val layoutInflater : LayoutInflater = LayoutInflater.from(mCtx)
            val view : View = layoutInflater.inflate(resource , null )
            val imageView :ImageView = view.findViewById(R.id.iconIv)
            var textView : TextView = view.findViewById(R.id.titleTv)
            var textView1 : TextView = view.findViewById(R.id.descTv)
            var product_linkage : LinearLayout = view.findViewById(R.id.coupang)

            var person : ResultGetSearch = items[position]

            val url = person.imageurl
            val product_ur = person.product_url
            Glide.with(this@ResultActivity).load(url).into(imageView)
            textView.text = person.title
            textView1.text = person.content

            product_linkage.setOnClickListener {
                var pro = product_ur
                var intent2 = Intent(Intent.ACTION_VIEW, Uri.parse(pro))
                startActivity(intent2)

            }

            return view
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {//상품 크롤링 다이얼로그

                search_dialog = View.inflate(this@ResultActivity,
                    R.layout.dialog_main, null) //dialog_main.xml 대입
                var listView_search = search_dialog.findViewById<ListView>(R.id.listView_search)
                var adapter_search = MyListAdapter(this, R.layout.row, list)
                adapter_search.notifyDataSetChanged()
                listView_search.adapter = adapter_search

                val dlg = AlertDialog.Builder(this@ResultActivity)
                dlg.setTitle("관련 상품 목록")
                dlg.setView(search_dialog)
                dlg.setPositiveButton("닫기", null)
                dlg.show()
                return true
            }

            R.id.compare -> {
                compare_dialog = View.inflate(this@ResultActivity,
                    R.layout.dialog_history, null)

                var filePath = "/data/data/com.skku_summer.food_info/files/"
                var listView_history = compare_dialog.findViewById<ListView>(R.id.listView_history)
                var fileList = ArrayList<String>() //디렉토리 내 저장된 json 파일 리스트

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

                var adapter_history = ArrayAdapter(this, android.R.layout.simple_list_item_1, fileList)
                listView_history.adapter = adapter_history

                listView_history.setOnItemClickListener { parent, view, position, id ->
                    var intentt = Intent(this@ResultActivity, CompareActivity::class.java)
                    intentt.putExtra("filename", fileList[position])
                    intentt.putExtra("info_arr_gram", info_arr_gram)
                    intentt.putExtra("info_arr_per", info_arr_per)
                    intentt.putExtra("gram", gram)
                    intentt.putExtra("category", category_idx)
                    startActivity(intentt)
                }

                var dlg = AlertDialog.Builder(this@ResultActivity)
                dlg.setTitle("최근 기록과 비교하기")
                dlg.setIcon(R.drawable.compare_black)
                dlg.setView(compare_dialog)
                dlg.setPositiveButton("닫기", null)
                dlg.show()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}