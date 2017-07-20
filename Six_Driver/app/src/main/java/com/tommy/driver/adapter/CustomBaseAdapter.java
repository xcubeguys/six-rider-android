package com.tommy.driver.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tommy.driver.R;
import com.tommy.driver.utils.LogUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by test on 22/5/17.
 */
public class CustomBaseAdapter extends BaseAdapter {
    Context context;
    List<YourTrips> rowItems;
    Double total = 0.0;

    public CustomBaseAdapter(Context context, List<YourTrips> items) {
        this.context = context;
        this.rowItems = items;
    }

    /*private view holder class*/
    private class ViewHolder {
        ImageView imageView;
        TextView txtTitle;
        TextView txtTime;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;

        LayoutInflater mInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.baseadapter_layout, null);
            holder = new ViewHolder();

            holder.txtTitle = (TextView) convertView.findViewById(R.id.cashend);
            holder.txtTime = (TextView) convertView.findViewById(R.id.timeend);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        YourTrips rowItem = (YourTrips) getItem(position);
        total = Double.parseDouble(rowItem.getDailycash()) - Double.parseDouble(rowItem.getAdmincommission());
        holder.txtTitle.setText("$ " + convertToDecimal(total));
        holder.txtTime.setText(getDate(Long.parseLong(rowItem.getEndTime())));

        return convertView;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        TimeZone tz = TimeZone.getTimeZone("GMT");
        cal.setTimeInMillis(time * 1000);
        cal.add(Calendar.MILLISECOND, tz.getOffset(cal.getTimeInMillis()));
        SimpleDateFormat sdf;
        sdf = new SimpleDateFormat("hh:mm a");
        Date currenTimeZone = (Date) cal.getTime();
        //LogUtils.i("Trip Date==>"+sdf.format(currenTimeZone));
        return sdf.format(currenTimeZone);
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    public String convertToDecimal(Double amount) {

        if (amount > 0) {
            LogUtils.i("THE AMOUNT IS" + new DecimalFormat("0.00").format(amount));
            return new DecimalFormat("0.00").format(amount);
        } else {
            return String.valueOf(0);
        }
    }
}
