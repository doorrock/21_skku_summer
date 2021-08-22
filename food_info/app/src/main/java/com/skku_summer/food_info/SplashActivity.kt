package com.skku_summer.food_info

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission


class SplashActivity: AppCompatActivity() {
    var flag = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setPermission()
    }

    override fun onResume() {
        super.onResume()
        if(flag) {
            var intent = Intent(applicationContext, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setPermission(){
        val permission = object : PermissionListener {
            override fun onPermissionGranted() {
                // 권한 허용됨
                Toast.makeText(this@SplashActivity, "권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
                flag = true
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                // 권한 허용 안됨
                Toast.makeText(this@SplashActivity, "권한이 거부되었습니다.", Toast.LENGTH_SHORT).show()
                flag = false
            }
        }

        TedPermission.with(this)
            .setPermissionListener(permission)
            .setRationaleMessage("카메라 앱을 사용하시려면 권한을 허용해주세요.")
            .setDeniedMessage("권한을 거부하셨습니다. [앱 설정] -> [권한] 항목에서 허용해주세요.")
            .setPermissions(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }
}
