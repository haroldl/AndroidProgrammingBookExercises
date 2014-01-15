package net.hotelling.harold.criminalintent

import java.util.UUID
import Helpers.blockToTextWatcher
import Helpers.onClick
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.EditText
import android.content.Intent
import android.app.Activity
import java.util.Date

object CrimeFragment {
  val TAG = "CrimeFragment"
  val EXTRA_CRIME_ID = "net.hotelling.harold.criminalintent.extra_crime_fragment_crime_id" 
  val DIALOG_DATE = "date"
  val REQUEST_DATE = 0

  def apply(crimeId: UUID): CrimeFragment = {
    val args = new Bundle()
    args.putSerializable(EXTRA_CRIME_ID, crimeId)
    Log.d(TAG, s"apply with crimeId $crimeId")

    val crimeFragment = new CrimeFragment()
    crimeFragment setArguments args
    crimeFragment
  }
}

class CrimeFragment extends Fragment {
  import CrimeFragment._

  private var mCrime: Crime = _
  private var mTitle: EditText = _
  private var mDateButton: Button = _
  private var mSolvedCheckBox: CheckBox = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val crimeId = getArguments().getSerializable(EXTRA_CRIME_ID).asInstanceOf[UUID]
    Log.d(TAG, "onCreate: crimeId = " + crimeId)
    mCrime = CrimeLab.get(getActivity()).getCrime(crimeId)
  }

  override def onCreateView(inflater: LayoutInflater, container: ViewGroup, savedInstanceState: Bundle): View = {
    val v = inflater.inflate(R.layout.fragment_crime, container, false)

    mTitle = v.findViewById(R.id.crimeTitle).asInstanceOf[EditText]
    mDateButton = v.findViewById(R.id.crime_date).asInstanceOf[Button]
    mSolvedCheckBox = v.findViewById(R.id.crime_solved).asInstanceOf[CheckBox]

    mTitle.setText(mCrime.getTitle)
    mTitle addTextChangedListener { s: String => mCrime.setTitle(s) }

    mDateButton.setEnabled(true)
    mDateButton setOnClickListener { v: View =>
      val fm = getActivity().getSupportFragmentManager()
      val dialog = DatePickerFragment(mCrime.getDate)
      dialog.setTargetFragment(CrimeFragment.this, REQUEST_DATE)
      dialog.show(fm, DIALOG_DATE)
    }
    updateDate()

    mSolvedCheckBox.setChecked(mCrime.isSolved)
    mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        mCrime.setSolved(isChecked)
      }
    })

    v
  }

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
    if (requestCode == REQUEST_DATE && resultCode == Activity.RESULT_OK) {
      val date = data.getSerializableExtra(DatePickerFragment.EXTRA_DATE).asInstanceOf[Date]
      mCrime setDate date
      updateDate()
    }
  }

  private def updateDate() {
    mDateButton setText mCrime.getDate.toString
  }
}
