package com.skku_summer.food_info

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.skku_summer.food_info.R
import okhttp3.*
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.lang.Exception
import java.lang.StringBuilder

class OcrActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ocr_loading)

        val toolbar = findViewById<Toolbar>(R.id.toolbar_ocr)
        toolbar.title = ""
        toolbar.inflateMenu(R.menu.menu_nothing)
        setSupportActionBar(toolbar)


        var img = findViewById<ImageView>(R.id.img_loading)
        Glide.with(this).load(R.drawable.loading).into(img)



        var intent_temp = getIntent()
        var gram : Int = intent_temp.getIntExtra("gram", 100)
        var category_idx : Int = intent_temp.getIntExtra("category", 0)

        var info_list = arrayOf("나트륨","탄수화물","당류","지방","트랜스지방","포화지방","콜레스테롤","단백질")
        var test_list = arrayOf("원재료명", "함량")
        var lastidx_list = arrayOf("유통기한", "함유")

        var info_arr_gram : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)
        var info_arr_per : FloatArray = floatArrayOf(0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f)

        var test_str = StringBuilder()
        var items = ArrayList<String>()

        val requestUrl = "https://dapi.kakao.com/v2/vision/text/ocr"
        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "image",
                "test.jpg",
                File("/storage/emulated/0/Pictures/MyCamApp/test.jpg").asRequestBody()
            ).build()

        val request = Request.Builder()
            .addHeader("Authorization", "KakaoAK temp")
            .addHeader("Accept-Charset", "UTF-8")
            .addHeader("Content-Type", "multipart/form-data")
            .addHeader("Host", "dapi.kakao.com")
            .url(requestUrl).post(requestBody).build()


        OkHttpClient().newCall(request).enqueue(object : Callback {
            var flag_end : Boolean = false
            var flag1 : Boolean = false
            var flag2 : Boolean = false

            var item_data = ArrayList<String>()
            lateinit var str_temp : String
            lateinit var intentt : Intent

            override fun onFailure(call: Call, e: IOException) {
                Log.d("AAAAA", e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.isSuccessful) {
                    var str_response = response.body?.string()
                    Log.i("AAAAA", str_response)
                    val jsonobj = JSONObject(str_response)
                    val jj: JSONArray = jsonobj.getJSONArray("result")
                    var size: Int = jj.length()
                    for (i in 0..size - 1) {
                        var json_test: JSONObject = jj.getJSONObject(i)
                        items.add(json_test.getJSONArray("recognition_words")[0].toString())
                        Log.i("AAAAA", items[i])
                        if(items[i]=="영양정보") flag1 = true
                        else if(test_list.contains(items[i])) flag2 = true
                        else if(lastidx_list.contains(items[i])) flag2 = false

                        if(flag1) {
                            if (info_list.contains(items[i])) test_str.append('-' + items[i] + '-')
                            else test_str.append(items[i] + '-')
                        }
                        else if(flag2){
                            test_str.append(items[i] + '-')
                        }
                    }

                    if(flag1) {
                        var ttest_str = test_str.toString()
                        Log.i("AAAAA", "$ttest_str")
                        for(i in 0..7){
                            ttest_str = ttest_str.replaceFirst("${info_list[i]}", "-${info_list[i]}-")
                        }
                        ttest_str = ttest_str.replace(' ', '-')
                        ttest_str = ttest_str.replace("(%)", "/")
                        ttest_str = ttest_str.replace('%', '-')
                        ttest_str = ttest_str.replace("mg", "-")
                        ttest_str = ttest_str.replace("ma", "-")
                        ttest_str = ttest_str.replace("mo", "-")
                        ttest_str = ttest_str.replace('g', '-')
                        ttest_str = ttest_str.replace('q', '-')
                        var start_idx = ttest_str.indexOf("나트륨")
                        var end_idx = ttest_str.indexOfLast { it == '-' }
                        ttest_str = ttest_str.substring(start_idx, end_idx)
                        ttest_str = ttest_str.replace("------", "-")
                        ttest_str = ttest_str.replace("-----", "-")
                        ttest_str = ttest_str.replace("----", "-")
                        ttest_str = ttest_str.replace("---", "-")
                        ttest_str = ttest_str.replace("--", "-")

                        Log.i("AAAAA", "$ttest_str")
                        var token = ttest_str.split('-')
                        for (i in 0..info_list.size - 1) {
                            var idx = token.indexOf(info_list[i])
                            var first_val = 0f
                            var sec_val = 0f
                            try {
                                first_val = token[idx + 1].toFloat()
                            } catch (e: Exception) {
                            }
                            try {
                                sec_val = token[idx + 2].toFloat()
                            } catch (e: Exception) {
                            }

                            info_arr_gram[i] = first_val
                            info_arr_per[i] = sec_val

                        }
                        str_temp = ttest_str
                        flag_end = true
                    }


                    else{
                        var ttest_str = test_str.toString()
                        ttest_str = ttest_str.replace("원재료명", "-")
                        ttest_str = ttest_str.replace("함량", "-")
                        ttest_str = ttest_str.replace("국산", "-")
                        ttest_str = ttest_str.replace('%', '-')
                        ttest_str = ttest_str.replace(')', '-')
                        ttest_str = ttest_str.replace('(', '-')
                        ttest_str = ttest_str.replace(',', '-')
                        ttest_str = ttest_str.replace(',', '-')
                        ttest_str = ttest_str.replace('.', '-')
                        ttest_str = ttest_str.replace('0', '-')
                        ttest_str = ttest_str.replace('1', '-')
                        ttest_str = ttest_str.replace('2', '-')
                        ttest_str = ttest_str.replace('3', '-')
                        ttest_str = ttest_str.replace('4', '-')
                        ttest_str = ttest_str.replace('5', '-')
                        ttest_str = ttest_str.replace('6', '-')
                        ttest_str = ttest_str.replace('7', '-')
                        ttest_str = ttest_str.replace('8', '-')
                        ttest_str = ttest_str.replace('9', '-')

                        ttest_str = ttest_str.replace("--------", "-")
                        ttest_str = ttest_str.replace("-------", "-")
                        ttest_str = ttest_str.replace("------", "-")
                        ttest_str = ttest_str.replace("-----", "-")
                        ttest_str = ttest_str.replace("----", "-")
                        ttest_str = ttest_str.replace("---", "-")
                        ttest_str = ttest_str.replace("--", "-")

                        ttest_str = ttest_str.substring(1, ttest_str.length-1)
                        var token = ttest_str.split('-')
                        token = token.distinct()
                        item_data.addAll(token)
                        Log.i("AAAAA","${item_data.javaClass.name}")
                        Log.i("AAAAA","${item_data.size}")
                        for(i in 0..item_data.size-1){
                            Log.i("AAAAA", "${item_data[i]}")
                        }
                        str_temp = ttest_str
                        flag_end = true
                    }


                }
                runOnUiThread{
                    Log.i("AAAAA", "$flag_end")
                    run {
                        if(response.isSuccessful){
                            if(flag_end){
                                if(flag1) {
                                    intentt = Intent(this@OcrActivity, ResultActivity::class.java)
                                    intentt.putExtra("info_arr_gram", info_arr_gram)
                                    intentt.putExtra("info_arr_per", info_arr_per)
                                    intentt.putExtra("gram", gram)
                                    intentt.putExtra("category", category_idx)
                                    intentt.putExtra("test", str_temp)
                                    startActivity(intentt)
                                    overridePendingTransition(0, 0)
                                    finish()
                                }
                                else {
                                    intentt = Intent(this@OcrActivity, ItemActivity::class.java)
                                    intentt.putExtra("data", item_data)
                                    intentt.putExtra("category", category_idx)
                                    intentt.putExtra("test", str_temp)
                                    startActivity(intentt)
                                    overridePendingTransition(0, 0)
                                    finish()
                                }
                            }
                        }
                    }

                }
            }
        })
    }

}
