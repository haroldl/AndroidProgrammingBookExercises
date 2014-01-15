package net.hotelling.harold.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.util.Log
import java.util.UUID

class CrimeActivity extends SingleFragmentActivity {
  override val TAG = "CrimeActivity"

  override def createFragment() = {
    val crimeId = getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID).asInstanceOf[UUID]
    CrimeFragment(crimeId)
  }

}
