package arco.present.aprs.reader.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import arco.present.aprs.reader.R

class HomeFragment : Fragment() {
    companion object {
        fun newInstance(): HomeFragment {

            return HomeFragment();
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }
}