package net.hotelling.harold.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.ViewPager
import java.util.{List => JList}
import java.util.UUID
import scala.collection.JavaConversions._

class CrimePagerActivity extends FragmentActivity {

  private var mViewPager: ViewPager = _
  private var mCrimes: JList[Crime] = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    mViewPager = new ViewPager(this)
    mViewPager setId R.id.viewPager
    setContentView(mViewPager)
    
    mCrimes = CrimeLab.get(this).getCrimes
    
    val fm = getSupportFragmentManager()
    mViewPager.setAdapter(new FragmentStatePagerAdapter(fm) {
      override def getCount(): Int = mCrimes.size
      override def getItem(pos: Int): Fragment = CrimeFragment(mCrimes.get(pos).getId) 
    })
    
    val crimeId = getIntent().getSerializableExtra(CrimeFragment.EXTRA_CRIME_ID).asInstanceOf[UUID]
    mCrimes.zipWithIndex find { case (c: Crime, i: Int) => c.getId == crimeId } match {
      case Some((crime, index)) => {
        updateTitle(index)
        mViewPager.setCurrentItem(index)
      }
      case None => // Allow ViewPager to display the 1st item by default.
    }
    
    mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      override def onPageScrollStateChanged(state: Int) { }
      override def onPageScrolled(pos: Int, posOffset: Float, posOffsetPixels: Int) { }
      override def onPageSelected(pos: Int) { updateTitle(pos) }
    })
  }
  
  def updateTitle(pos: Int) {
    Option(mCrimes.get(pos).getTitle) match {
      case Some(title) => setTitle(title)
      case None => setTitle(R.string.crime_title_label)
    }
  }

}