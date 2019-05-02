package ListAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import Help.LocationData;
import gps.gps_uniproject.Mainmenu;
import gps.gps_uniproject.R;


public class CustomGPSResultsAdapter extends BaseAdapter {

    Context context;

    public CustomGPSResultsAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return Mainmenu.GPSData.locationData.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null) {
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = mInflater.inflate(R.layout.customgpsrow, null);
        }

        final TextView longitude = (TextView) v.findViewById(R.id.GPSLongitude);
        final TextView latitude = (TextView) v.findViewById(R.id.GPSLatitude);
        final TextView time = (TextView) v.findViewById(R.id.GPSTime);

        LocationData locationData = new LocationData();
        locationData = Mainmenu.GPSData.locationData.get(i);

        String sLongitude = String.valueOf(locationData.longitude);
        String sLatitude = String.valueOf(locationData.latitude);
        String sTime = String.valueOf(locationData.locationTime);

        longitude.setText(sLongitude);
        latitude.setText(sLatitude);
        time.setText(sTime);

        return v;
    }
}

