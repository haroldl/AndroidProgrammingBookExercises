package net.hotelling.harold.criminalintent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.util.Log;

public class CrimeLab {
  private static final String TAG = "CrimeLab";
  private static final String FILENAME = "crimes.json";

  private static CrimeLab sCrimeLab;

  private Context mAppContext;
  private List<Crime> mCrimes;
  private CriminalIntentJSONSerializer mSerializer;

  private CrimeLab(Context appContext) {
    mAppContext = appContext;
    mSerializer = new CriminalIntentJSONSerializer(mAppContext, FILENAME);

    mCrimes = new ArrayList<Crime>();
    try {
      mCrimes.addAll(mSerializer.loadCrimes());
    } catch (Exception e) {
      Log.e(TAG, "Error loading crimes: ", e);
    }
  }
  
  public void addCrime(Crime c) {
    mCrimes.add(c);
  }

  public void deleteCrime(Crime c) {
    mCrimes.remove(c);
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

  public boolean saveCrimes() {
    try {
      mSerializer.saveCrimes(mCrimes);
      Log.d(TAG, "Crimes saved to file " + FILENAME);
      return true;
    } catch (Exception e) {
      Log.e(TAG, "Error saving crimes: ", e);
      return false;
    }
  }
}
