package net.hotelling.harold.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;

public class CrimeLab {
  private static CrimeLab sCrimeLab;

  private Context mAppContext;
  private List<Crime> mCrimes;
  
  private CrimeLab(Context appContext) {
    mAppContext = appContext;
    mCrimes = new ArrayList<Crime>();
    for (int i = 0; i < 100; i++) {
      Crime newCrime = new Crime();
      newCrime.setTitle("Crime #" + i);
      newCrime.setSolved(i % 2 == 0);
      mCrimes.add(newCrime);
    }
  }
  
  public static CrimeLab get(Context c) {
    if (sCrimeLab == null) {
      sCrimeLab = new CrimeLab(c.getApplicationContext());
    }
    return sCrimeLab;
  }

  public List<Crime> getCrimes() {
    return mCrimes;
  }
  
  public Crime getCrime(UUID crimeId)  {
    for (Crime crime : mCrimes) {
      if (crime.getId().equals(crimeId)) {
        return crime;
      }
    }
    return null;
  }

}
