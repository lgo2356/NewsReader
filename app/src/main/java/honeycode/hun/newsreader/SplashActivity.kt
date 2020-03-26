package honeycode.hun.newsreader

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.DisplayMetrics
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_splash.*
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat

class SplashActivity : AppCompatActivity() {
    private val internetPermissionCode: Int = 0x01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this)
            .load(ContextCompat.getDrawable(this, R.drawable.main_splash_image2))
            .circleCrop()
            .into(main_splash_image)

        Handler().postDelayed({
            finish()
        }, 1300)

//        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.INTERNET), internetPermissionCode)
//        }
    }
}