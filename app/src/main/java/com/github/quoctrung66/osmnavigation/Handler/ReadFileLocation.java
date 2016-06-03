package com.github.quoctrung66.osmnavigation.Handler;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by QUOC TRUNG on 4/29/2016.
 */
public class ReadFileLocation extends AsyncTask<Void, Void, Void> {

    private Long time_old = 0L;
    private boolean pause = false;

    //Listener
    private ReadFileListener readFileListener;

    //Location create
    private Location mLastLocation;

    private Context mContext;
    private String filename;

    public ReadFileLocation(Context mContext, String filename) {
        this.mContext = mContext;
        this.filename = filename;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLastLocation = new Location(LocationManager.GPS_PROVIDER);
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(mContext.getAssets().open(filename)));
            while (true){
                if (!pause) {
                    String line = br.readLine();
                    if (line != null) {
                        String[] content = line.split(",");
                        Long delta_time = Long.parseLong(content[5]);
                        while (true) {
                            if (System.currentTimeMillis() - time_old > delta_time) {
                                time_old = System.currentTimeMillis();
                                mLastLocation.setLatitude(Double.parseDouble(content[0]));
                                mLastLocation.setLongitude(Double.parseDouble(content[1]));
                                mLastLocation.setBearing(Float.parseFloat(content[2]));
                                mLastLocation.setAccuracy(Float.parseFloat(content[3]));
                                mLastLocation.setSpeed(Float.parseFloat(content[4]));
                                mLastLocation.setTime(Long.parseLong(content[5]));
                                if (readFileListener != null) {
                                    readFileListener.onReadLine(mLastLocation);
                                }
                                break;
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addReadFileListener(ReadFileListener listener){
        this.readFileListener = listener;
    }
    public interface ReadFileListener{
        void onReadLine(Location location);
    }
    public void toggle(){
        pause = !pause;
    }
}
