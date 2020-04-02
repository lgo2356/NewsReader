package honeycode.hun.newsreader

import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_splash.*

class SplashActivity : AppCompatActivity() {
    private val internetPermissionCode: Int = 0x01

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this)
            .load(ContextCompat.getDrawable(this, R.drawable.main_splash_image))
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