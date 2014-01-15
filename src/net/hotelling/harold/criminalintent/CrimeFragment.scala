package net.hotelling.harold.criminalintent

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.CheckBox
import android.widget.Button
import android.widget.CompoundButton.OnCheckedChangeListener
import android.widget.CompoundButton
import Helpers._
import java.util.UUID
import android.util.Log

object CrimeFragment {
  val TAG = "CrimeFragment"
  val EXTRA_CRIME_ID = "net.hotelling.harold.criminalintent.extra_crime_fragment_crime_id" 

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

    mDateButton.setText(mCrime.getDate.toString)
    mDateButton.setEnabled(true)

    mSolvedCheckBox.setChecked(mCrime.isSolved)
    mSolvedCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
      override def onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        mCrime.setSolved(isChecked)
      }
    })

    v
  }

}
