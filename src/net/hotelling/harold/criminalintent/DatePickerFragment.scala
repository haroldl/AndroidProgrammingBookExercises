package net.hotelling.harold.criminalintent

import android.support.v4.app.DialogFragment
import android.os.Bundle
import android.app.Dialog
import android.app.AlertDialog
import java.util.Date
import java.util.Calendar
import android.widget.DatePicker
import android.widget.DatePicker.OnDateChangedListener
import java.util.GregorianCalendar
import android.content.Intent
import android.content.DialogInterface
import android.app.Activity

object DatePickerFragment {
  val EXTRA_DATE = "net.hotelling.harold.criminalintent.date"

  def apply(date: Date): DatePickerFragment = {
    val args= new Bundle()
    args.putSerializable(EXTRA_DATE, date)
    val fragment = new DatePickerFragment()
    fragment.setArguments(args)
    fragment
  }
}

class DatePickerFragment extends DialogFragment {
  import DatePickerFragment._
  import Calendar.{YEAR, MONTH, DAY_OF_MONTH}

  private var mDate: Date = _

  override def onCreateDialog(savedInstanceState: Bundle): Dialog = {
    mDate = getArguments.getSerializable(EXTRA_DATE).asInstanceOf[Date]
    
    val cal = Calendar.getInstance
    cal setTime mDate
    val (year, month, day) = (cal get YEAR, cal get MONTH, cal get DAY_OF_MONTH)

    val v = getActivity().getLayoutInflater().inflate(R.layout.dialog_date, null)

    val datePicker = v.findViewById(R.id.dialog_date_datePicker).asInstanceOf[DatePicker]
    datePicker.init(year, month, day, new OnDateChangedListener() {
      override def onDateChanged(view: DatePicker, year: Int, month: Int, day: Int) {
        mDate = new GregorianCalendar(year, month, day).getTime
        getArguments.putSerializable(EXTRA_DATE, mDate)
      }
    })

    new AlertDialog.Builder(getActivity())
      .setView(v)
      .setTitle(R.string.date_picker_title)
      .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
        override def onClick(dialog: DialogInterface, which: Int) {
          sendResult(Activity.RESULT_OK)
        }
      })
      .create()
  }
  
  private def sendResult(resultCode: Int) {
    Option(getTargetFragment()) match {
      case Some(targetFragment) => {
        val i = new Intent()
        i.putExtra(EXTRA_DATE, mDate)
        targetFragment.onActivityResult(getTargetRequestCode(), resultCode, i)
      }
      case None => // ignore
    }
  }
}