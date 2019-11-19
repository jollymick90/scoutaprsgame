package arco.present.aprs.reader

import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import arco.present.aprs.reader.common.TAGLOG
import arco.present.aprs.reader.common.mainAct
import arco.present.aprs.reader.ui.HomeFragment
import arco.present.aprs.reader.ui.MapFragment
import arco.present.aprs.reader.ui.ToolsFragment
import kotlinx.android.synthetic.main.fragment_map.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

class MainActivity : AppCompatActivity() {

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {
                Log.i(TAGLOG, "Got to home")
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment.newInstance()).commit();

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_dashboard -> {
                Log.i(TAGLOG, "Got to map")
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, MapFragment.instance()).commit();

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_notifications -> {
                Log.i(TAGLOG, "Got to tools")
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, ToolsFragment.newInstance()).commit();

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainAct = this;

        setContentView(R.layout.activity_main)
        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val homeFragment: HomeFragment = HomeFragment.newInstance();
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, homeFragment).commit()

    }

}
