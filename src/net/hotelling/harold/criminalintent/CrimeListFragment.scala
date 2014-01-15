package net.hotelling.harold.criminalintent

import android.support.v4.app.ListFragment
import android.os.Bundle
import java.util.{List => JList}
import android.widget.ArrayAdapter
import android.widget.ListView
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.TextView
import android.widget.CheckBox
import android.content.Intent
import android.widget.Button

import Helpers._

class CrimeListFragment extends ListFragment {
  val TAG = "CrimeListFragment"

  private var mCrimes: JList[Crime] = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    getActivity().setTitle(R.string.crimes_title)
    mCrimes = CrimeLab.get(getActivity()).getCrimes()
  }

  override def onViewCreated(view: View, savedInstanceState: Bundle) {
    Log.d(TAG, "onViewCreated: trying to add footer and adapter")

    val adapter = new CrimeAdapter(mCrimes)
    val footerView = getActivity.getLayoutInflater.inflate(R.layout.list_footer, null)
    val addCrimeButton = footerView.findViewById(R.id.list_footer_addButton).asInstanceOf[Button]

    addCrimeButton setOnClickListener {
      view: View => {
        val newCrime = new Crime()
        newCrime setTitle "New Crime"
        mCrimes add newCrime
        adapter.notifyDataSetChanged()
      }
    }

    // false
    Log.d(TAG, "are the views the same? " + (getListView() == view))

    // Android Javadoc says to call addFooterView before setListAdapter
    getListView().addFooterView(footerView)
    setListAdapter(adapter)
  }

  override def onListItemClick(lv: ListView, v: View, position: Int, id: Long) {
    val crime = mCrimes.get(position)
    val title = crime.getTitle
    Log.d(TAG, s"$title was clicked")

    // Another way is to get the crime from the ListAdapter
    val crime2 = getListAdapter.asInstanceOf[CrimeAdapter].getItem(position)
    val title2 = crime2.getTitle
    Log.d(TAG, s"$title2 was clicked")
    
    val intent = new Intent(getActivity(), classOf[CrimeActivity])
    intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId)
    startActivity(intent)
  }

  override def onResume() {
    super.onResume()
    getListAdapter().asInstanceOf[CrimeAdapter].notifyDataSetChanged()
  }

  private class CrimeAdapter(crimes: JList[Crime])
    extends ArrayAdapter[Crime](getActivity(), 0, crimes)
  {
    override def getView(position: Int, convertView: View, parent: ViewGroup): View = {
      val view = Option(convertView) getOrElse {
        getActivity().getLayoutInflater().inflate(R.layout.list_item_crime, null)
      }
      val crime = getItem(position)
      val titleTextView = view.findViewById(R.id.crime_list_item_titleTextView).asInstanceOf[TextView]
      val dateTextView = view.findViewById(R.id.crime_list_item_dateTextView).asInstanceOf[TextView]
      val solvedCheckBox = view.findViewById(R.id.crime_list_item_solvedCheckBox).asInstanceOf[CheckBox]
      
      titleTextView setText crime.getTitle
      dateTextView setText crime.getDate.toString
      solvedCheckBox setChecked crime.isSolved

      view
    }    
  }

}
