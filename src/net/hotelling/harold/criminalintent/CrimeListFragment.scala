package net.hotelling.harold.criminalintent

import java.util.{List => JList}
import Helpers.onClick
import android.annotation.TargetApi
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.ListFragment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import android.os.Build
import android.view.LayoutInflater
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.AbsListView
import android.widget.AbsListView.MultiChoiceModeListener
import android.view.ActionMode

class CrimeListFragment extends ListFragment {
  val TAG = "CrimeListFragment"

  private var mCrimes: JList[Crime] = _
  private var mSubtitleVisible: Boolean = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
    getActivity().setTitle(R.string.crimes_title)
    mCrimes = CrimeLab.get(getActivity()).getCrimes()
    setRetainInstance(true)
    mSubtitleVisible = false
  }

  @TargetApi(11)
  override def onCreateView(inflater: LayoutInflater, parent: ViewGroup, savedInstanceState: Bundle): View = {
    val v = super.onCreateView(inflater, parent, savedInstanceState)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      if (mSubtitleVisible) {
        getActivity.getActionBar setSubtitle R.string.subtitle
      }
    }
    
    val listView = v.findViewById(android.R.id.list).asInstanceOf[ListView]
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
      registerForContextMenu(listView)
    } else {
      listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL)
      listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {
        override def onItemCheckedStateChanged(mode: ActionMode, pos: Int, id: Long, checked: Boolean) {}
        override def onCreateActionMode(mode: ActionMode, menu: Menu) = {
          mode.getMenuInflater.inflate(R.menu.crime_list_item_context, menu)
          true
        }
        override def onPrepareActionMode(mode: ActionMode, menu: Menu) = false
        override def onDestroyActionMode(mode: ActionMode) {}
        override def onActionItemClicked(mode: ActionMode, item: MenuItem) = item.getItemId match {
          case R.id.menu_item_delete_crime => {
            val adapter = getListAdapter().asInstanceOf[CrimeAdapter]
            val lab = CrimeLab.get(getActivity)
            (0 until adapter.getCount).reverse foreach {
              i => if (getListView isItemChecked i) lab deleteCrime adapter.getItem(i)
            }
            mode.finish()
            adapter.notifyDataSetChanged()
            true
          }
          case _ => false
        }
      })
    }

    v
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
        CrimeLab.get(getActivity) addCrime newCrime
        adapter.notifyDataSetChanged()
      }
    }

    // false
    Log.d(TAG, "are the views the same? " + (getListView() == view))

    // Android Javadoc says to call addFooterView before setListAdapter
    getListView().addFooterView(footerView)
    setListAdapter(adapter)
  }

  override def onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.fragment_crime_list, menu)
    val showSubtitle = menu findItem R.id.menu_item_show_subtitle
    if (mSubtitleVisible && showSubtitle != null) {
      showSubtitle setTitle R.string.hide_subtitle
    }
  }

  override def onListItemClick(lv: ListView, v: View, position: Int, id: Long) {
    val crime = mCrimes.get(position)
    val title = crime.getTitle
    Log.d(TAG, s"$title was clicked")

    // Another way is to get the crime from the ListAdapter
    val crime2 = getListAdapter.asInstanceOf[CrimeAdapter].getItem(position)
    val title2 = crime2.getTitle
    Log.d(TAG, s"$title2 was clicked")
    
    val intent = new Intent(getActivity(), classOf[CrimePagerActivity])
    intent.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId)
    startActivity(intent)
  }

  @TargetApi(11)
  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.menu_item_new_crime => {
        val crime = new Crime()
        CrimeLab.get(getActivity) addCrime crime
        val i = new Intent(getActivity, classOf[CrimePagerActivity])
        i.putExtra(CrimeFragment.EXTRA_CRIME_ID, crime.getId)
        startActivityForResult(i, 0)
        true
      }
      case R.id.menu_item_show_subtitle => {
        val actionBar = getActivity.getActionBar
        Option(actionBar.getSubtitle) match {
          case Some(subtitle) => {
            actionBar setSubtitle null
            mSubtitleVisible = false
            item setTitle R.string.show_subtitle
          }
          case None => {
            actionBar setSubtitle R.string.subtitle
            mSubtitleVisible = true
            item setTitle R.string.hide_subtitle
          }
        }
        true
      }
      case _ => super.onOptionsItemSelected(item)
    }
  }

  override def onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo) {
    getActivity.getMenuInflater.inflate(R.menu.crime_list_item_context, menu)
  }

  override def onContextItemSelected(item: MenuItem): Boolean = {
    val info = item.getMenuInfo().asInstanceOf[AdapterContextMenuInfo]
    val pos = info.position
    val adapter = getListAdapter().asInstanceOf[CrimeAdapter]
    val crime = adapter getItem pos
    item.getItemId match {
      case R.id.menu_item_delete_crime => {
        CrimeLab.get(getActivity) deleteCrime crime
        adapter.notifyDataSetChanged()
        true
      } 
      case _ => super.onContextItemSelected(item)
    }
  }

  override def onPause() {
    super.onPause()
    CrimeLab.get(getActivity).saveCrimes()
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
