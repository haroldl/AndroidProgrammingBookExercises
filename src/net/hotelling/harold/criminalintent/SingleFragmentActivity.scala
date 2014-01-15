package net.hotelling.harold.criminalintent

import android.support.v4.app.FragmentActivity
import net.hotelling.harold.criminalintent.MixIns.FragmentActivityHelpers
import android.os.Bundle
import android.util.Log
import android.support.v4.app.Fragment

trait SingleFragmentActivity extends FragmentActivity with FragmentActivityHelpers {

  val TAG: String
  
  def createFragment(): Fragment

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_fragment)

    addFragment(R.id.fragmentContainer) {
      Log.d(TAG, "creating fragment")
      createFragment()
    }
  }

}