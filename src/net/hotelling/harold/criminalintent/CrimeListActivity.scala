package net.hotelling.harold.criminalintent

class CrimeListActivity extends SingleFragmentActivity {
  override val TAG = "CrimeListActivity"
  override def createFragment() = new CrimeListFragment()
}